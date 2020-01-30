package com.guet.flexbox.handshake

import com.guet.flexbox.handshake.gui.GuiApplication
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class MockServerApplication : ApplicationRunner {

    companion object {
        @Volatile
        var focus: String? = null
    }

    override fun run(args: ApplicationArguments) {
        var port = 8080
        if (args.containsOption("package.focus")) {
            focus = args.getOptionValues("package.focus").first()
        }
        if (args.containsOption("server.port")) {
            port = args.getOptionValues("server.port").first()
                    .toIntOrNull() ?: 8080
        }
        GuiApplication.run(port)
    }
}
