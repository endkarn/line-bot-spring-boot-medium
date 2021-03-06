package com.iphayao.linebot;

import com.google.common.io.ByteStreams;
import com.iphayao.linebot.flex.*;
import com.iphayao.linebot.helper.RichMenuHelper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@LineMessageHandler
public class LineBotController {

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @EventMapping
    public void handleTextMessage(MessageEvent<TextMessageContent> event) throws IOException {
        log.info(event.toString());
        TextMessageContent message = event.getMessage();
        handleTextContent(event.getReplyToken(), event, message);
    }

    @EventMapping
    public void handleStickerMessage(MessageEvent<StickerMessageContent> event) {
        log.info(event.toString());
        StickerMessageContent message = event.getMessage();
        reply(event.getReplyToken(), new StickerMessage(
                message.getPackageId(), message.getStickerId()
        ));
    }

    @EventMapping
    public void handleLocationMessage(MessageEvent<LocationMessageContent> event) {
        log.info(event.toString());
        LocationMessageContent message = event.getMessage();
        reply(event.getReplyToken(), new LocationMessage(
                (message.getTitle() == null) ? "Location replied" : message.getTitle(),
                message.getAddress(),
                message.getLatitude(),
                message.getLongitude()
        ));
    }

    @EventMapping
    public void handleImageMessage(MessageEvent<ImageMessageContent> event) {
        log.info(event.toString());
        ImageMessageContent content = event.getMessage();
        String replyToken = event.getReplyToken();

        try {
            MessageContentResponse response = lineMessagingClient.getMessageContent(content.getId()).get();
            DownloadedContent jpg = saveContent("jpg", response);
            DownloadedContent previewImage = createTempFile("jpg");

            system("convert", "-resize", "240x",
                    jpg.path.toString(),
                    previewImage.path.toString());

            reply(replyToken, new ImageMessage(jpg.getUri(), previewImage.getUri()));

        } catch (InterruptedException | ExecutionException e) {
            reply(replyToken, new TextMessage("Cannot get image: " + content));
            throw new RuntimeException(e);
        }

    }

    @EventMapping
    public void handlePostbackEvent(PostbackEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, "GET SOME POSTBACk");
//        this.replyText(replyToken,
//                "Got postback data " + event.getPostbackContent().getData() + ", param " + event
//                        .getPostbackContent().getParams().toString());
    }


    private void handleTextContent(String replyToken, Event event, TextMessageContent content) throws IOException {
        String text = content.getText().toLowerCase();

        log.info("Got text message from %s : %s", replyToken, text);

        if (text.contains("cmd")){

        }

        switch (text) {
            case "profile": {
                String userId = event.getSource().getUserId();
                if (userId != null) {
                    lineMessagingClient.getProfile(userId)
                            .whenComplete((profile, throwable) -> {
                                if (throwable != null) {
                                    this.replyText(replyToken, throwable.getMessage());
                                    return;
                                }
                                this.reply(replyToken, Arrays.asList(
                                        new TextMessage("Reply token: " + replyToken),
                                        new TextMessage("Display name: " + profile.getDisplayName()),
                                        new TextMessage("Status message: " + profile.getStatusMessage()),
                                        new TextMessage("User ID: " + profile.getUserId())
                                ));
                            });
                }
                break;
            }
            case "liff": {
                String userId = event.getSource().getUserId();
                if (userId != null) {
                    lineMessagingClient.getProfile(userId)
                            .whenComplete((profile, throwable) -> {
                                if (throwable != null) {
                                    this.replyText(replyToken, throwable.getMessage());
                                    return;
                                }
                                this.replyText(replyToken, "line://app/1630213822-5nm9yX07");
                            });
                }
                break;
            }
            case "flex": {
                System.out.println("\n\n\n check " + getClass().getClassLoader().getResource("richmenu.yml").getPath());
//                String pathImageFlex = new ClassPathResource("richmenu/richmenu-flexs.jpg").getFile().getPath();
//                String pathConfigFlex = new ClassPathResource("richmenu/richmenu-flexs.yml").getFile().getAbsolutePath();
                String pathImageFlex = getClass().getClassLoader().getResource("richmenu-flexs.jpg").getPath();
                String pathConfigFlex = getClass().getClassLoader().getResource("richmenu.yml").getPath();
                System.out.println("\n\n\n\n\n getClass().getClassLoader().getResourceAsStream(\"richmenu.yml\") " + getClass().getClassLoader().getResourceAsStream("richmenu.yml").read());
                Yaml YAML = new Yaml();
                Object yamlConfigAsObject;
                yamlConfigAsObject = YAML.load(getClass().getClassLoader().getResourceAsStream("richmenu/richmenu-flexs.yml"));
//                RichMenu richMenu = new ObjectMapper().convertValue(yamlAsObject,RichMenu.class);
//                System.out.println("GOT ITT -++++++++++++++"+yamlAsObject.toString());
                byte[] bytesOfImageFlex = ByteStreams.toByteArray(getClass().getClassLoader().getResourceAsStream("richmenu-flexs.jpg"));
                String userId = event.getSource().getUserId();
                if (userId != null) {
                    lineMessagingClient.getProfile(userId)
                            .whenComplete((profile, throwable) -> {
                                if (throwable != null) {
                                    this.replyText(replyToken, throwable.getMessage());
                                    return;
                                }
                                RichMenuHelper.createRichMenu(lineMessagingClient, yamlConfigAsObject, pathImageFlex, userId, bytesOfImageFlex);
                            });
                }
                break;
            }
            case "flex back": {
                String userId = event.getSource().getUserId();
                if (userId != null) {
                    lineMessagingClient.getProfile(userId)
                            .whenComplete((profile, throwable) -> {
                                if (throwable != null) {
                                    this.replyText(replyToken, throwable.getMessage());
                                    return;
                                }
                                RichMenuHelper.deleteRichMenu(lineMessagingClient, userId);
                            });
                }
                break;
            }
            case "flex restaurant": {
                this.reply(replyToken, new RestaurantFlexMessageSupplier().get());
                break;
            }
            case "flex menu": {
                this.reply(replyToken, new RestaurantMenuFlexMessageSupplier().get());
                break;
            }
            case "flex receipt": {
                this.reply(replyToken, new ReceiptFlexMessageSupplier().get());
                break;
            }
            case "flex news": {
                this.reply(replyToken, new NewsFlexMessageSupplier().get());
                break;
            }
            case "flex ticket": {
                this.reply(replyToken, new TicketFlexMessageSupplier().get());
                break;
            }
            case "flex catalogue": {
                this.reply(replyToken, new CatalogueFlexMessageSupplier().get());
                break;
            }
            case "quickreply": {
                this.reply(replyToken, new MessageWithQuickReplySupplier().get());
                break;
            }
            case "takecare": {
                this.reply(replyToken, Arrays.asList(new TakeCareFlexMessageSupplier().get(),
                        new LocationMessage("Take Care Salon of Beauty", "19 31 ซอย สุขุมวิท 19 แขวง คลองเตยเหนือ เขต วัฒนา กรุงเทพมหานคร 10110", 13.7380889, 100.5602276)));
                break;
            }
            case "monitor": {
                this.reply(replyToken, new MonitorFlexMessageSupplier().get());
                break;
            }
            case "cmd whoami": {
//                this.replyText(replyToken, "Enter your command...");
                String[] commandText =text.split(" ");
                String cmd = commandText[0];
                String commandLine = commandText[1];
                this.replyText(replyToken, callSHH(commandLine));
                break;
            }
            default:
                log.info("Return echo message %s : %s", replyToken, text);
                this.replyText(replyToken, text);
        }
    }

    private void handleStickerContent(String replyToken, StickerMessageContent content) {
        reply(replyToken, new StickerMessage(
                content.getPackageId(), content.getStickerId()
        ));
    }

    private void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken is not empty");
        }

        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "...";
        }
        this.reply(replyToken, new TextMessage(message));
    }

    private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        try {
            BotApiResponse response = lineMessagingClient.replyMessage(
                    new ReplyMessage(replyToken, messages)
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void system(String... args) {
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        try {
            Process start = processBuilder.start();
            int i = start.waitFor();
            log.info("result: {} => {}", Arrays.toString(args), i);
        } catch (InterruptedException e) {
            log.info("Interrupted", e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static DownloadedContent saveContent(String ext, MessageContentResponse response) {
        log.info("Content-type: {}", response);
        DownloadedContent tempFile = createTempFile(ext);
        try (OutputStream outputStream = Files.newOutputStream(tempFile.path)) {
            ByteStreams.copy(response.getStream(), outputStream);
            log.info("Save {}: {}", ext, tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static DownloadedContent createTempFile(String ext) {
        String fileName = LocalDateTime.now() + "-" + UUID.randomUUID().toString() + "." + ext;
        Path tempFile = Application.downloadedContentDir.resolve(fileName);
        tempFile.toFile().deleteOnExit();
        return new DownloadedContent(tempFile, createUri("/downloaded/" + tempFile.getFileName()));

    }

    private static String createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path).toUriString();
    }

    @Value
    public static class DownloadedContent {
        Path path;
        String uri;
    }

    private String callSHH(String command) {
        String user = "root";
        String password = "dst!hdw";
        String host = "103.253.72.79";
        int port = 22;

        //String remoteFile="/home/john/test.txt";
        //String yes="yes";

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("Establishing Connection...");
            session.connect();
            Channel channel = session.openChannel("exec");

            // After this it'll ask for confirmation and password
            ((ChannelExec) channel).setCommand(command);
            channel.connect();

            InputStream output = channel.getInputStream();

            System.out.println("after stream");
            int readByte = output.read();
            StringBuilder outputBuffer = new StringBuilder();
            while (readByte != 0xffffffff) {
                //System.out.println("read byte" + readByte);
                outputBuffer.append((char) readByte);
                readByte = output.read();
            }
            System.out.println(outputBuffer.toString());
            channel.disconnect();
            return outputBuffer.toString();
        } catch (Exception e) {
            System.err.print("error message" + e);
        }
       return null;
    }
}
