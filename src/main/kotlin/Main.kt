package com.dukita

// Events
import com.dukita.events.Mention

// Config
import io.github.cdimascio.dotenv.dotenv

// JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent

fun main() {
    val dotenv = dotenv {
        directory = "./"
        ignoreIfMissing = true
    }

    val token = dotenv["DISCORD_TOKEN"] ?: error("There was an error finding the token 🛑")

    val jda = JDABuilder.createDefault(token)
        .setActivity(
            Activity.customStatus("<3 Just a test!")
        )

        .enableIntents(GatewayIntent.MESSAGE_CONTENT)
        .addEventListeners(Mention())
        .build()

    jda.awaitReady()
    println("All started successfully! 🧶")
}