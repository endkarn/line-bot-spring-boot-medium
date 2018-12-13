package com.iphayao.linebot;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebHookController {

    @PostMapping("/callbackk")
    String callBack(){
        return "call back";
    }
}
