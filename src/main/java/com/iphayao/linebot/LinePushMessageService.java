package com.iphayao.linebot;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ExecutionException;

public class LinePushMessageService {

    private static final Logger log = LoggerFactory.getLogger(LinePushMessageService.class);

    void linePushMessage(){
        final LineMessagingClient client = LineMessagingClient
                .builder("dK2vVVNMwfM9LpESDP5ZO+NSdG95rR7/ZMDMboZykGrz7PfIc36wurdMV5hruEg43LLlThWM4tZdT4cUQ03SSTjivHDNbQabySkuOe+G0bNrTboNIQ5ZgllcQ1KIlnTYcUOQwIkgYLBIXQE45r9+3gdB04t89/1O/w1cDnyilFU=")
                .build();

        final TextMessage textMessage = new TextMessage("Heroku Status : Online ["+new Date()+"]");
        final PushMessage pushMessage = new PushMessage(
                "U79c1a767e9d7db6466d826af886103dd",
                textMessage);

        final BotApiResponse botApiResponse;
        try {
            botApiResponse = client.pushMessage(pushMessage).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        }

        System.out.println(botApiResponse);
        log.info("Service is Online now !!");
    }
}
