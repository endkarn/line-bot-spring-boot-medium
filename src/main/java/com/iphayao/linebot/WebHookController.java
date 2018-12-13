package com.iphayao.linebot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebHookController {

    @GetMapping("/helloworld")
    String callBack(){
        return "helloworld";
    }

    @PostMapping("/helloworld")
    String postCall(){
        return "helloworld";
    }
}
