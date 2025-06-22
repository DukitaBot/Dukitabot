package com.dukita.events

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Mention : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot || event.isWebhookMessage) return

        val selfUser = event.jda.selfUser
        val message = event.message

        val messageContent = message.contentRaw

        val mention = selfUser.asMention
        val nicknameMention = "<@!${selfUser.id}>"

        val startsWithMention = messageContent.startsWith(mention) || messageContent.startsWith(nicknameMention)

        if (startsWithMention) {
            val authorMention = event.author.asMention

            message.reply("Hii ${authorMention}! <3").queue()
        }
    }
}