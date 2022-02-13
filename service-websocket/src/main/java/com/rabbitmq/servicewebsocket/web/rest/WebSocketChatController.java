package com.rabbitmq.servicewebsocket.web.rest;
import com.rabbitmq.servicewebsocket.web.model.BasicMessage;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketChatController {

    private final Environment env;

    public WebSocketChatController(Environment env) {
        this.env = env;
    }


    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/general")
    public BasicMessage sendMessage(@Payload BasicMessage webSocketChatMessage) {
        webSocketChatMessage.setPortServer(env.getProperty("local.server.port"));
        return webSocketChatMessage;
    }


    @MessageMapping("/chat.newUser")
    @SendTo("/topic/general")
    public BasicMessage newUser(@Payload BasicMessage webSocketChatMessage,
                                        SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", webSocketChatMessage.getSender());
        webSocketChatMessage.setPortServer(env.getProperty("local.server.port"));
        return webSocketChatMessage;
    }
}