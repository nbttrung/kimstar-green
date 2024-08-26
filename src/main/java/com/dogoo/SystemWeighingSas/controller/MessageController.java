package com.dogoo.SystemWeighingSas.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @MessageMapping("/public/dogoo/ws-message")
    @SendTo("/public/dogoo/messages")
    public String send(String userName){

        System.out.println("trung log userName " + userName);
        return "Hello, " + userName;
    }
}
