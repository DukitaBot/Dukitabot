package com.dukita

// Config
import io.github.cdimascio.dotenv.dotenv

// JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent

// Reflections
import org.reflections.Reflections

fun main() {
    val dotenv = dotenv {
        directory = "./"
        ignoreIfMissing = true
    }

    val token = dotenv["DISCORD_TOKEN"] ?: error("There was an error finding the token 🛑")

    val builder = JDABuilder.createDefault(token)
        .setActivity(
            Activity.customStatus("<3 Just a test!")
        )
        .enableIntents(GatewayIntent.MESSAGE_CONTENT)

    try {
        val reflections = Reflections("com.dukita.events")

        val eventClasses = reflections.getSubTypesOf(ListenerAdapter::class.java)

        println("🔎 Searching for events")

        for (eventClass in eventClasses) {
            try {
                val listener = eventClass.getDeclaredConstructor().newInstance()
                builder.addEventListeners(listener)
                println("✅ Listener '${eventClass.simpleName}' started")
            } catch (e: Exception) {
                println("❌ Ops! '${eventClass.simpleName}': ${e.message}")
            }
        }
    } catch (e: Exception) {
        println("🚨 Crash: ${e.message}")
    }

    val jda = builder.build()

    jda.awaitReady()
    println("All started successfully! 🧶")
}