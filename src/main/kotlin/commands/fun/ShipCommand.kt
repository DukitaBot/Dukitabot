package com.dukita.commands.`fun`

import com.dukita.commands.ICommand
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.utils.FileUpload
import java.awt.*
import java.awt.geom.Ellipse2D
import java.awt.geom.GeneralPath
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.*
import kotlin.random.Random

class ShipCanvasCommand : ICommand {
    override val name: String = "ship"
    override val aliases: List<String> = listOf("shipar", "shippar")

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val mentionedUsers = event.message.mentions.users.toMutableList()
        val members = event.guild.members

        args.filter { arg ->
            !arg.startsWith("<@") && arg != name && !aliases.contains(arg)
        }.forEach { arg ->
            try {
                val user = event.jda.retrieveUserById(arg).complete()
                if (user != null && !mentionedUsers.any { it.id == user.id }) {
                    mentionedUsers.add(user)
                }
            } catch (e: Exception) {
                val member = members.firstOrNull { m ->
                    m.nickname?.equals(arg, ignoreCase = true) == true ||
                            m.user.name.equals(arg, ignoreCase = true)
                }
                member?.user?.let { user ->
                    if (!mentionedUsers.any { it.id == user.id }) {
                        mentionedUsers.add(user)
                    }
                }
            }
        }

        val isBotInvolved = mentionedUsers.any { it.id == event.jda.selfUser.id }

        if (mentionedUsers.size == 1 && mentionedUsers[0].id == event.author.id) {
            event.channel.sendMessage("💢 You can't ship yourself with yourself, silly!").queue()
            return
        }
        if (mentionedUsers.size !in 1..2) {
            event.channel.sendMessage("💢 silly `").queue()
            return
        }

        val (user1, user2) = if (mentionedUsers.size == 1) {
            Pair(event.author, mentionedUsers[0])
        } else {
            Pair(mentionedUsers[0], mentionedUsers[1])
        }

        event.channel.sendTyping().queue()

        try {
            val shipName = generateShipName(user1.name, user2.name)
            val percentage = calculateChemistry(user1, user2)

            val messageContent = buildString {
                append("**💞 Hmmm, do we have a new couple here?**\n")
                append("`${user1.name}` + `${user2.name}` = ✨ **`$shipName`** ✨\n")
                append(getFunnyComment(percentage))

                if (isBotInvolved) {
                    append("\n🙄 **Oh, please!** Me ship like some common bot? *I deserve champagne and diamonds!*")
                }
            }

            event.message.reply(messageContent)
                .addFiles(createPremiumShipImage(user1, user2, percentage))
                .queue()

        } catch (e: Exception) {
            event.channel.sendMessage("💥 Oops! The love machine broke...").queue()
            e.printStackTrace()
        }
    }

    private fun generateShipName(name1: String, name2: String): String {
        if (name1.isEmpty()) return name2
        if (name2.isEmpty()) return name1

        val half1 = name1.length / 2
        val part1 = name1.substring(0, half1)

        val half2 = (name2.length + 1) / 2
        val part2 = name2.substring(half2)

        return (part1 + part2).replaceFirstChar { it.uppercase() }
    }

    private fun calculateChemistry(user1: User, user2: User): Int {
        val nameCompat = (user1.name.hashCode() + user2.name.hashCode()) % 101
        val creationDiff = abs(user1.timeCreated.toEpochSecond() - user2.timeCreated.toEpochSecond())
        val timeFactor = (1_000_000_000L / max(1, creationDiff)).toInt().coerceAtMost(30)
        val randomBoost = Random.nextInt(0, 21)

        return (nameCompat * 0.6 + timeFactor * 0.3 + randomBoost * 0.1).toInt().coerceIn(0, 100)
    }

    private fun getFunnyComment(percentage: Int): String {
        val comments = when {
            percentage == 100 -> listOf(
                "**ABSOLUTE PERFECTION!** 💎 The universe approves!",
                "**Soulmate found!** 💘 Perfect couple!"
            )
            percentage >= 90 -> listOf(
                "**Almost perfect!** An epic romance! 🎬",
                "**Movie couple!** Hollywood will want the rights! 🎥"
            )
            percentage >= 70 -> listOf(
                "**Good chemistry!** You can call it a power couple! 💪",
                "**It has a future!** You can now schedule the wedding! 💍"
            )
            percentage >= 50 -> listOf(
                "**It's possible to work...** Maybe with a date? 🍷",
                "**Not so bad...** Maybe with more interaction? 👀"
            )
            percentage >= 30 -> listOf(
                "**Hmm...** Better to stay in the friendzone? 🤔",
                "**Almost there...** Or not? Who knows... 🎭"
            )
            percentage >= 10 -> listOf(
                "**Almost zero...** Better not even try! 🙅‍♂️",
                "**Difficult huh...** But never say never! ✨"
            )
            else -> listOf(
                "**Total disaster!** Even the universe rejects it! ☄️",
                "**Incompatible!** Not in another life! \uD83D\uDC7B"
            )
        }
        return comments.random()
    }

    private fun createPremiumShipImage(user1: User, user2: User, percentage: Int): FileUpload {
        val width = 1000
        val height = 450
        val avatarSize = 200
        val cornerRadius = 60

        val font = loadFont("/Poppins-Bold.ttf")?.deriveFont(Font.BOLD, 36f)
            ?: Font("Arial Rounded MT Bold", Font.BOLD, 36)

        val boldFont = font.deriveFont(Font.BOLD, 42f)
        val titleFont = font.deriveFont(Font.BOLD, 28f)

        val avatar1 = try {
            ImageIO.read(URL(user1.avatarUrl?.replace("?size=128", "?size=512")
                ?: user1.defaultAvatarUrl))
        } catch (e: Exception) {
            createDefaultAvatar(user1.name.first())
        }

        val avatar2 = try {
            ImageIO.read(URL(user2.avatarUrl?.replace("?size=128", "?size=512")
                ?: user2.defaultAvatarUrl))
        } catch (e: Exception) {
            createDefaultAvatar(user2.name.first())
        }

        val canvas = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g = canvas.createGraphics().apply {
            setRenderingHints(mapOf(
                RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
                RenderingHints.KEY_TEXT_ANTIALIASING to RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB,
                RenderingHints.KEY_RENDERING to RenderingHints.VALUE_RENDER_QUALITY,
                RenderingHints.KEY_FRACTIONALMETRICS to RenderingHints.VALUE_FRACTIONALMETRICS_ON
            ))
        }

        drawCosmicBackground(g, width, height)
        drawGlassCard(g, width, height, cornerRadius)
        drawUserAvatars(g, avatar1, avatar2, width, height, avatarSize, cornerRadius)
        drawCentralSparkles(g, width, height, percentage)
        drawChemistryMeter(g, width, height, percentage, cornerRadius, boldFont)
        drawUserNames(g, user1.name, user2.name, width, height, titleFont)
        drawSpecialEffects(g, width, height, percentage)

        g.dispose()
        return FileUpload.fromData(convertToByteArray(canvas), "ship_${System.currentTimeMillis()}.png")
    }

    private fun drawCosmicBackground(g: Graphics2D, width: Int, height: Int) {
        val cosmicGradient = GradientPaint(
            0f, 0f, Color(15, 5, 35),
            width.toFloat(), height.toFloat(), Color(5, 15, 45)
        )
        g.paint = cosmicGradient
        g.fillRect(0, 0, width, height)

        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f)
        repeat(150) {
            val size = Random.nextDouble(0.5, 2.5).toFloat()
            g.color = Color(255, 255, 255, Random.nextInt(150, 255))
            g.fillOval(
                Random.nextInt(width),
                Random.nextInt(height),
                size.toInt(),
                size.toInt()
            )
        }
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
    }

    private fun drawGlassCard(g: Graphics2D, width: Int, height: Int, cornerRadius: Int) {
        val cardWidth = width - 120
        val cardHeight = height - 90
        val cardX = 60
        val cardY = 45

        g.color = Color(40, 35, 75, 180)
        g.fillRoundRect(cardX, cardY, cardWidth, cardHeight, cornerRadius, cornerRadius)

        g.stroke = BasicStroke(2f)
        g.color = Color(255, 255, 255, 60)
        g.drawRoundRect(cardX, cardY, cardWidth, cardHeight, cornerRadius, cornerRadius)

        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f)
        g.color = Color(150, 180, 255, 50)
        g.fillRoundRect(cardX + 10, cardY + 10, cardWidth - 20, cardHeight - 20, cornerRadius, cornerRadius)
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
    }

    private fun drawUserAvatars(
        g: Graphics2D,
        avatar1: BufferedImage,
        avatar2: BufferedImage,
        width: Int,
        height: Int,
        size: Int,
        cornerRadius: Int
    ) {
        val cardWidth = width - 120
        val cardHeight = height - 90
        val cardX = 60
        val cardY = 45

        val posX1 = cardX + 60
        val posX2 = cardX + cardWidth - 60 - size
        val posY = cardY + (cardHeight - size) / 2

        drawAvatarGlow(g, posX1, posY, size, Color(100, 200, 255))
        drawAvatarGlow(g, posX2, posY, size, Color(255, 100, 200))

        drawCircularAvatar(g, avatar1, posX1, posY, size, Color(100, 200, 255))
        drawCircularAvatar(g, avatar2, posX2, posY, size, Color(255, 100, 200))
    }

    private fun drawAvatarGlow(g: Graphics2D, x: Int, y: Int, size: Int, color: Color) {
        val glowSize = size + 40
        val glowX = x - 20
        val glowY = y - 20

        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f)
        val radialGradient = RadialGradientPaint(
            Point2D.Float((x + size/2).toFloat(), (y + size/2).toFloat()),
            glowSize.toFloat(),
            floatArrayOf(0.1f, 0.9f),
            arrayOf(
                Color(color.red, color.green, color.blue, 150),
                Color(color.red, color.green, color.blue, 0)
            )
        )
        g.paint = radialGradient
        g.fillOval(glowX, glowY, glowSize, glowSize)
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
    }

    private fun drawCircularAvatar(g: Graphics2D, avatar: BufferedImage, x: Int, y: Int, size: Int, borderColor: Color) {
        g.color = Color(0, 0, 0, 100)
        g.fillOval(x + 3, y + 5, size, size)

        g.clip = Ellipse2D.Float(x.toFloat(), y.toFloat(), size.toFloat(), size.toFloat())
        g.drawImage(avatar, x, y, size, size, null)
        g.clip = null

        g.stroke = BasicStroke(5f)
        g.color = borderColor
        g.drawOval(x, y, size, size)

        g.stroke = BasicStroke(2f)
        g.color = Color(255, 255, 255, 120)
        g.drawOval(x + 2, y + 2, size - 4, size - 4)
    }

    private fun drawCentralSparkles(g: Graphics2D, width: Int, height: Int, percentage: Int) {
        val centerX = width / 2f
        val centerY = height / 2f - 40

        val (element, size, color) = when {
            percentage == 100 -> Triple("💎", 100, Color(255, 215, 0))
            percentage >= 95 -> Triple("💖", 90, Color(255, 50, 150))
            percentage >= 85 -> Triple("💘", 85, Color(255, 100, 200))
            percentage >= 70 -> Triple("❤️", 80, Color(255, 0, 50))
            percentage >= 50 -> Triple("✨", 70, Color(255, 255, 100))
            percentage >= 30 -> Triple("💓", 60, Color(255, 150, 150))
            else -> Triple("💔", 60, Color(150, 150, 150))
        }

        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f)
        g.color = Color(color.red, color.green, color.blue, 80)
        g.fillOval((centerX - size/2).toInt(), (centerY - size/2).toInt(), size, size)
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)

        g.font = Font("Segoe UI Emoji", Font.PLAIN, size)
        val bounds = g.fontMetrics.getStringBounds(element, g)
        g.color = color
        g.drawString(element, centerX - bounds.width.toFloat()/2, centerY + bounds.height.toFloat()/3)

        if (percentage > 70) {
            drawSparkles(g, centerX.toInt(), centerY.toInt(), size/2, 15, color)
        }
    }

    private fun drawSparkles(g: Graphics2D, x: Int, y: Int, radius: Int, count: Int, baseColor: Color) {
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)
        repeat(count) {
            val angle = Random.nextDouble() * 2 * Math.PI
            val distance = Random.nextDouble() * radius
            val sparkleX = x + (distance * cos(angle)).toInt()
            val sparkleY = y + (distance * sin(angle)).toInt()

            val size = Random.nextInt(8, 20)
            val alpha = Random.nextInt(150, 255)
            g.color = Color(
                baseColor.red,
                baseColor.green,
                baseColor.blue,
                alpha
            )

            fillStar(g, sparkleX, sparkleY, size/4, size, 5)
        }
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
    }

    private fun drawChemistryMeter(
        g: Graphics2D,
        width: Int,
        height: Int,
        percentage: Int,
        cornerRadius: Int,
        font: Font
    ) {
        val barWidth = 700
        val barHeight = 35
        val barX = (width - barWidth) / 2
        val barY = height - 80

        g.color = Color(30, 25, 60, 200)
        g.fillRoundRect(barX, barY, barWidth, barHeight, cornerRadius, cornerRadius)

        g.stroke = BasicStroke(3f)
        g.color = Color(80, 75, 120, 180)
        g.drawRoundRect(barX, barY, barWidth, barHeight, cornerRadius, cornerRadius)

        val progressWidth = (barWidth * (percentage / 100.0)).toInt()
        if (progressWidth > 0) {
            val progressGradient = GradientPaint(
                barX.toFloat(), 0f, getProgressStartColor(percentage),
                (barX + progressWidth).toFloat(), 0f, getProgressEndColor(percentage)
            )
            g.paint = progressGradient
            g.fillRoundRect(barX, barY, progressWidth, barHeight, cornerRadius, cornerRadius)

            g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)
            g.color = Color.WHITE
            g.fillRoundRect(barX + progressWidth - 40, barY - 5, 40, barHeight + 10, cornerRadius, cornerRadius)
            g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
        }

        val percentText = "$percentage%"
        g.font = font
        val textWidth = g.fontMetrics.stringWidth(percentText)

        g.color = Color(0, 0, 0, 180)
        repeat(3) { offset ->
            g.drawString(percentText, (width - textWidth)/2f + offset, (barY - 20).toFloat() + offset)
        }

        g.color = when {
            percentage > 80 -> Color(255, 255, 180)
            percentage > 50 -> Color.WHITE
            else -> Color(200, 200, 200)
        }
        g.drawString(percentText, (width - textWidth)/2f, (barY - 20).toFloat())

        val label = when {
            percentage == 100 -> "PERFECT"
            percentage >= 90 -> "INCREDIBLE"
            percentage >= 80 -> "EXCELLENT"
            percentage >= 70 -> "GOOD"
            percentage >= 50 -> "REASONABLE"
            percentage >= 30 -> "WEAK"
            else -> "BAD"
        }

        g.font = font.deriveFont(24f)
        val labelWidth = g.fontMetrics.stringWidth(label)
        g.color = Color(255, 255, 255, 180)
        g.drawString(label, (width - labelWidth)/2f, (barY - 55).toFloat())
    }

    private fun drawUserNames(g: Graphics2D, name1: String, name2: String, width: Int, height: Int, font: Font) {
        val cardWidth = width - 120
        val cardX = 60
        val cardY = 45

        g.font = font
        g.color = Color(180, 220, 255)
        val name1Width = g.fontMetrics.stringWidth(name1)
        g.drawString(name1, (cardX + 60).toFloat(), (cardY + 50).toFloat())

        g.color = Color(255, 180, 220)
        val name2Width = g.fontMetrics.stringWidth(name2)
        g.drawString(name2, (cardX + cardWidth - 60 - name2Width).toFloat(), (cardY + 50).toFloat())
    }

    private fun drawSpecialEffects(g: Graphics2D, width: Int, height: Int, percentage: Int) {
        if (percentage > 60) {
            g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)
            g.font = Font("Segoe UI Emoji", Font.PLAIN, 24)
            repeat(10 + (percentage / 10)) {
                val heart = when (Random.nextInt(4)) {
                    0 -> "❤️"
                    1 -> "💕"
                    2 -> "💖"
                    else -> "💗"
                }
                val x = Random.nextInt(width)
                val y = Random.nextInt(height)
                g.color = Color(255, 150 + Random.nextInt(100), 150 + Random.nextInt(100), 150 + Random.nextInt(105))
                g.drawString(heart, x.toFloat(), y.toFloat())
            }
            g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
        }

        val lineY = height / 2 - 40
        val lineStartX = 60 + 60 + 200 + 30
        val lineEndX = width - 60 - 60 - 200 - 30

        g.stroke = BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
            0f, floatArrayOf(15f, 10f), (System.currentTimeMillis() / 50 % 25).toFloat())
        g.color = Color(255, 255, 255, 100)
        g.drawLine(lineStartX, lineY, lineEndX, lineY)
    }

    private fun createDefaultAvatar(initial: Char): BufferedImage {
        val size = 512
        val image = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics()

        val hue = initial.code.toFloat() % 360
        g.color = Color.getHSBColor(hue / 360, 0.7f, 0.7f)
        g.fillRect(0, 0, size, size)

        g.color = Color.WHITE
        g.font = Font("Arial", Font.BOLD, 400)
        val metrics = g.fontMetrics
        val x = (size - metrics.charWidth(initial)) / 2
        val y = (size - metrics.height) / 2 + metrics.ascent
        g.drawString(initial.toString(), x, y)

        g.dispose()
        return image
    }

    private fun getProgressStartColor(percentage: Int): Color {
        return when {
            percentage >= 90 -> Color(255, 80, 180)
            percentage >= 80 -> Color(255, 100, 200)
            percentage >= 70 -> Color(255, 120, 0)
            percentage >= 50 -> Color(255, 200, 0)
            percentage >= 30 -> Color(150, 220, 255)
            else -> Color(180, 180, 180)
        }
    }

    private fun getProgressEndColor(percentage: Int): Color {
        return when {
            percentage >= 90 -> Color(200, 40, 255)
            percentage >= 80 -> Color(255, 40, 150)
            percentage >= 70 -> Color(255, 60, 60)
            percentage >= 50 -> Color(255, 150, 40)
            percentage >= 30 -> Color(100, 180, 255)
            else -> Color(150, 150, 150)
        }
    }

    private fun convertToByteArray(image: BufferedImage): ByteArray {
        return ByteArrayOutputStream().apply {
            ImageIO.write(image, "png", this)
        }.toByteArray()
    }

    private fun fillStar(g: Graphics2D, x: Int, y: Int, innerRadius: Int, outerRadius: Int, points: Int) {
        val star = GeneralPath()
        val angle = Math.PI / points

        star.moveTo(x.toFloat(), (y - outerRadius).toFloat())

        for (i in 1 until points * 2) {
            val r = if (i % 2 == 0) outerRadius else innerRadius
            val currAngle = angle * i
            star.lineTo(
                (x + r * sin(currAngle)).toFloat(),
                (y - r * cos(currAngle)).toFloat()
            )
        }

        star.closePath()
        g.fill(star)
    }

    private fun loadFont(path: String): Font? {
        return try {
            this::class.java.getResourceAsStream(path)?.use {
                Font.createFont(Font.TRUETYPE_FONT, it)
            }
        } catch (e: Exception) {
            null
        }
    }
}