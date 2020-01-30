package com.guet.flexbox.handshake.gui

import java.awt.EventQueue
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.imageio.ImageIO
import javax.swing.JFrame
import kotlin.system.exitProcess

object GuiApplication {

    private const val WINDOW_SIZE: Int = 300

    fun run(port: Int) {
        System.clearProperty("java.awt.headless")
        if (GraphicsEnvironment.isHeadless()) {
            return
        }
        EventQueue.invokeLater {
            val imageView = createImagePanel()
            NetworkWatcher.addListener {
                val url = "http://$it:${port}"
                val image = QrCodeImage
                        .generate(url, WINDOW_SIZE)
                EventQueue.invokeLater {
                    imageView.image = image
                }
            }
        }
    }

    private fun createImagePanel(): ImageView {
        val imageView = ImageView()
        imageView.setSize(WINDOW_SIZE, WINDOW_SIZE)
        JFrame().apply {
            GuiApplication::class.java
                    .classLoader
                    .getResourceAsStream("static/icon.png")
                    ?.use {
                        iconImage = ImageIO.read(it)
                    }
            title = "Handshake"
            defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            setSize(WINDOW_SIZE, WINDOW_SIZE)
            isResizable = false
            val content = contentPane
            content.add(imageView)
            val kit = Toolkit.getDefaultToolkit()
            //获取屏幕的尺寸
            val screenSize = kit.screenSize
            //获取屏幕的宽
            val screenWidth = screenSize.width
            //获取屏幕的高
            val screenHeight = screenSize.height
            //设置窗口居中显示
            setLocation(
                    screenWidth / 2 - WINDOW_SIZE / 2,
                    screenHeight / 2 - WINDOW_SIZE / 2
            )
            addWindowListener(object : WindowAdapter() {
                override fun windowClosed(e: WindowEvent?) {
                    exitProcess(0)
                }
            })
            isVisible = true
            isAlwaysOnTop = true
            EventQueue.invokeLater {
                isAlwaysOnTop = false
            }
        }
        return imageView
    }

}