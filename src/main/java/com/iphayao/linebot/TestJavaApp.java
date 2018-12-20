package com.iphayao.linebot;

import com.iphayao.linebot.flex.RestaurantFlexMessageSupplier;
import com.iphayao.linebot.flex.TakeCareFlexMessageSupplier;
import com.iphayao.linebot.helper.RichMenuHelper;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineMessagingClientImpl;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class TestJavaApp {

    private LineBotController lineBotController = new LineBotController();
    @Autowired
    private LineMessagingClient lineMessagingClient;

    public static void main(String args[]) throws IOException {

    }

    @Test
    void checkRichMenu() throws Exception {
        Yaml YAML = new Yaml();
        String pathConfigFlex = new ClassPathResource("richmenu/richmenu-flexs.yml").getFile().getAbsolutePath();
        Object yamlAsObject;
        try (FileInputStream is = new FileInputStream(pathConfigFlex)) {
            System.out.println("GOT ITT -++++++++++++++");
            yamlAsObject = YAML.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void checkFlexMessage() throws Exception{
        final LineMessagingClient client = LineMessagingClient
                .builder("dK2vVVNMwfM9LpESDP5ZO+NSdG95rR7/ZMDMboZykGrz7PfIc36wurdMV5hruEg43LLlThWM4tZdT4cUQ03SSTjivHDNbQabySkuOe+G0bNrTboNIQ5ZgllcQ1KIlnTYcUOQwIkgYLBIXQE45r9+3gdB04t89/1O/w1cDnyilFU=")
                .build();
        final PushMessage pushMessage = new PushMessage(
                "U79c1a767e9d7db6466d826af886103dd",
                new TakeCareFlexMessageSupplier().get());
        BotApiResponse botApiResponse = client.pushMessage(pushMessage).get();
        System.out.println(botApiResponse);
    }
}
