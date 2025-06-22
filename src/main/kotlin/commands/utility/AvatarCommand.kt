package com.dukita.commands.utility

import com.dukita.commands.ICommand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color

class AvatarCommand : ICommand {
    override val name: String = "avatar"
    override val aliases: List<String> = listOf("av", "foto")

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if (args.isEmpty()) {
            sendAvatarEmbed(event, event.author)
            return
        }

        val input = args[0]
        val targetUser = try {
            findUser(event, input) ?: run {
                event.channel.sendMessage("❌ Ops! I didn't find this user").queue()
                return
            }
        } catch (e: Exception) {
            event.channel.sendMessage("🔍 An error occurred.").queue()
            return
        }

        sendAvatarEmbed(event, targetUser)
    }

    private fun findUser(event: MessageReceivedEvent, input: String): User? {
        if (input.startsWith("<@") && input.endsWith(">")) {
            val id = input.replace(Regex("[<@!>]"), "")
            return event.jda.retrieveUserById(id).complete()
        }

        if (input.matches(Regex("\\d+"))) {
            return try {
                event.jda.retrieveUserById(input).complete()
            } catch (e: Exception) {
                null
            }
        }

        return event.guild.members.firstOrNull { member ->
            member.nickname?.equals(input, ignoreCase = true) == true ||
                    member.user.name.equals(input, ignoreCase = true)
        }?.user
    }

    private fun sendAvatarEmbed(event: MessageReceivedEvent, targetUser: User) {
        val avatarUrl = targetUser.effectiveAvatarUrl + "?size=1024"

        val embed = EmbedBuilder().apply {
            setTitle("🖼️ Avatar of ${targetUser.name}")
            setImage(avatarUrl) // A URL de 1024px é usada aqui
            setColor(if (targetUser.isBot) Color.PINK.rgb else Color(70, 130, 180).rgb)

            when {
                targetUser.id == event.author.id ->
                    setFooter("Despite everything, it's still you.")
                targetUser.isBot ->
                    setFooter("I'm charming and beautiful, aren't I?")
            }
        }.build()

        event.message.replyEmbeds(embed)
            .setActionRow(
                Button.link(avatarUrl, "Open in browser")
                    .withEmoji(net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode("😊"))
            )
            .queue()
    }
}