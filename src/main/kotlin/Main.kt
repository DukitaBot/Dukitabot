package com.dukita

import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent

fun main() {
    val dotenv = dotenv {
        directory = "./"
        ignoreIfMissing = true
    }

    val token = dotenv["DISCORD_TOKEN"] ?: error("There was an error finding the token 🛑")

    val jda = JDABuilder.createLight(token, emptyList())
        .setActivity(
            Activity.customStatus("<3 Just a test!")
        )
        .enableIntents(GatewayIntent.MESSAGE_CONTENT)
        .build()

    jda.awaitReady()
    println("All started successfully! 🧶")
}