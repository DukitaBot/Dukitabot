package com.dukita

import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import org.reflections.Reflections

fun main() {
    val dotenv = dotenv {
        directory = "./"
        ignoreIfMissing = true
    }

    val token = dotenv["DISCORD_TOKEN"] ?: error("Discord token not found in the .env file 🛑")

    val builder = JDABuilder.createDefault(token)
        .setActivity(
            Activity.customStatus("<3 Just a test!")
        )
        .enableIntents(GatewayIntent.MESSAGE_CONTENT)


    try {
        val reflections = Reflections("com.dukita.events")
        val eventClasses = reflections.getSubTypesOf(ListenerAdapter::class.java)

        println("🔎 Searching for event listeners...")

        for (eventClass in eventClasses) {
            try {
                val listener = eventClass.getDeclaredConstructor().newInstance()
                builder.addEventListeners(listener)
                println("✅ Listener '${eventClass.simpleName}' registered successfully.")
            } catch (e: Exception) {
                println("❌ Failed to register listener '${eventClass.simpleName}': ${e.message}")
            }
        }
    } catch (e: Exception) {
        println("🚨 A critical error occurred during listener scanning: ${e.message}")
    }

    val jda = builder.build()

    jda.awaitReady()
    println("All started successfully! 🧶")
}