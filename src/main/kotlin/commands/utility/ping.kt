package com.dukita.commands.utility

import com.dukita.commands.ICommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Ping : ICommand {
    override val name: String = "ping"
    override val aliases: List<String> = listOf("p")

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val gatewayPing = event.jda.gatewayPing
        event.channel.sendMessage(" API: `${gatewayPing}ms`").queue()
    }
}