package com.dukita.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface ICommand {
    val name: String

    val aliases: List<String>
        get() = emptyList()

    fun execute(event: MessageReceivedEvent, args: List<String>)
}