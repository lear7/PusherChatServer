package com.example.demo

import com.google.gson.Gson
import com.pusher.rest.Pusher
import jdk.nashorn.internal.runtime.regexp.joni.Config.log
import org.springframework.http.ResponseEntity
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.function.ServerResponse
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletResponse


@RestController
class MessageController {
    private val pusher = Pusher("1316846", "b60a9a22230f313794df", "03d0454ce0a082c90a12")

    private val eventName = "new_message"

    init {
        pusher.setCluster("ap1")
    }

    @RequestMapping("/message")
    fun postMessage(@RequestBody message: Message): ResponseEntity<Unit> {
        val channelName = message.channelName
        val result = pusher.trigger(channelName, eventName, message)
        log.println("Push result: ${Gson().toJson(result)}, source: ${Gson().toJson(message)}")
        return ResponseEntity.ok().build()
    }


    @PostMapping("/auth")
    fun authUser(socket_id: String, channel_name: String, response: HttpServletResponse) {
        try {
            val authenticate = pusher.authenticate(socket_id, channel_name)
            log.println("authUser succeed: $authenticate")
            response.setHeader("Access-Control-Allow-Origin", "*")
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")
            response.writer.write(authenticate)
        } catch (e: IOException) {
            log.println("authUser failed: ${e.localizedMessage}")
        }
    }
}