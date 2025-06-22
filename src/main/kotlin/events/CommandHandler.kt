package com.dukita.events

import com.dukita.commands.ICommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.reflections.Reflections

class CommandHandler : ListenerAdapter() {
    companion object {
        const val PREFIX = "d?"
    }

    private val commandMap = mutableMapOf<String, ICommand>()

    init {
        println("🔎 Looking for commands...")
        try {
            val reflections = Reflections("com.dukita.commands")
            val commandClasses = reflections.getSubTypesOf(ICommand::class.java)

            for (commandClass in commandClasses) {
                if (commandClass.isInterface) continue

                try {
                    val command = commandClass.getDeclaredConstructor().newInstance()

                    commandMap[command.name.lowercase()] = command
                    command.aliases.forEach { alias -> commandMap[alias.lowercase()] = command }

                    println("✅ Command '${command.name}' loaded.")
                } catch (e: Exception) {
                    println("❌ Failed to load command '${commandClass.simpleName}': ${e.message}")
                }
            }
            println("✨ ${commandMap.values.distinct().size} commands loaded successfully.")
        } catch (e: Exception) {
            println("🚨 A critical error occurred during command scanning: ${e.message}")
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot || event.isWebhookMessage) return
        if (!event.message.contentRaw.startsWith(PREFIX)) return

        val messageContent = event.message.contentRaw

        val parts = messageContent.removePrefix(PREFIX).trim().split(Regex("\\s+"))

        val commandName = parts[0].lowercase()
        val args = parts.drop(1)

        val command = commandMap[commandName]

        if (command != null) {
            try {
                command.execute(event, args)
            } catch (e: Exception) {
                event.channel.sendMessage("It seems that the command `${command.name}` is having problems :/").queue()
                e.printStackTrace()
            }
        }
    }
}