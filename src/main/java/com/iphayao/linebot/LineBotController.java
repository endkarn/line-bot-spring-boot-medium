package com.iphayao.linebot;

import com.google.common.io.ByteStreams;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
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
    private static final Logger log = LoggerFactory.getLogger(LineBotController.class);

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @EventMapping
    public void handleTextMessage(MessageEvent<TextMessageContent> event) {
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

    private void handleTextContent(String replyToken, Event event, TextMessageContent content) {
        String text = content.getText().toLowerCase();

        log.info("Got text message from %s : %s", replyToken, text);

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
                                        new TextMessage("Display name: " + profile.getDisplayName()),
                                        new TextMessage("Status message: " + profile.getStatusMessage()),
                                        new TextMessage("User ID: " + profile.getUserId())
                                ));
                            });
                }
                break;
            }
            case "fiff": {
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
                String userId = event.getSource().getUserId();
                if (userId != null) {
                    lineMessagingClient.getProfile(userId)
                            .whenComplete((profile, throwable) -> {
                                if (throwable != null) {
                                    this.replyText(replyToken, throwable.getMessage());
                                    return;
                                }
                                this.replyText(replyToken, " {\n" +
                                        "            \"type\": \"flex\",\n" +
                                        "            \"altText\": \"This is a Flex message\",\n" +
                                        "            \"contents\": {\n" +
                                        "                \"type\": \"bubble\",\n" +
                                        "                \"styles\": {\n" +
                                        "                    \"footer\": {\n" +
                                        "                        \"separator\": true\n" +
                                        "                    }\n" +
                                        "                },\n" +
                                        "                \"body\": {\n" +
                                        "                    \"type\": \"box\",\n" +
                                        "                    \"layout\": \"vertical\",\n" +
                                        "                    \"contents\": [\n" +
                                        "                        {\n" +
                                        "                            \"type\": \"text\",\n" +
                                        "                            \"text\": \"RECEIPT\",\n" +
                                        "                            \"weight\": \"bold\",\n" +
                                        "                            \"color\": \"#1DB446\",\n" +
                                        "                            \"size\": \"sm\"\n" +
                                        "                        },\n" +
                                        "                        {\n" +
                                        "                            \"type\": \"text\",\n" +
                                        "                            \"text\": \"Brown Store\",\n" +
                                        "                            \"weight\": \"bold\",\n" +
                                        "                            \"size\": \"xxl\",\n" +
                                        "                            \"margin\": \"md\"\n" +
                                        "                        },\n" +
                                        "                        {\n" +
                                        "                            \"type\": \"text\",\n" +
                                        "                            \"text\": \"Miraina Tower, 4-1-6 Shinjuku, Tokyo\",\n" +
                                        "                            \"size\": \"xs\",\n" +
                                        "                            \"color\": \"#aaaaaa\",\n" +
                                        "                            \"wrap\": true\n" +
                                        "                        },\n" +
                                        "                        {\n" +
                                        "                            \"type\": \"separator\",\n" +
                                        "                            \"margin\": \"xxl\"\n" +
                                        "                        },\n" +
                                        "                        {\n" +
                                        "                            \"type\": \"box\",\n" +
                                        "                            \"layout\": \"vertical\",\n" +
                                        "                            \"margin\": \"xxl\",\n" +
                                        "                            \"spacing\": \"sm\",\n" +
                                        "                            \"contents\": [\n" +
                                        "                                {\n" +
                                        "                                    \"type\": \"box\",\n" +
                                        "                                    \"layout\": \"horizontal\",\n" +
                                        "                                    \"contents\": [\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"Energy Drink\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#555555\",\n" +
                                        "                                            \"flex\": 0\n" +
                                        "                                        },\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"$2.99\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#111111\",\n" +
                                        "                                            \"align\": \"end\"\n" +
                                        "                                        }\n" +
                                        "                                    ]\n" +
                                        "                                },\n" +
                                        "                                {\n" +
                                        "                                    \"type\": \"box\",\n" +
                                        "                                    \"layout\": \"horizontal\",\n" +
                                        "                                    \"contents\": [\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"Chewing Gum\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#555555\",\n" +
                                        "                                            \"flex\": 0\n" +
                                        "                                        },\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"$0.99\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#111111\",\n" +
                                        "                                            \"align\": \"end\"\n" +
                                        "                                        }\n" +
                                        "                                    ]\n" +
                                        "                                },\n" +
                                        "                                {\n" +
                                        "                                    \"type\": \"box\",\n" +
                                        "                                    \"layout\": \"horizontal\",\n" +
                                        "                                    \"contents\": [\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"Bottled Water\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#555555\",\n" +
                                        "                                            \"flex\": 0\n" +
                                        "                                        },\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"$3.33\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#111111\",\n" +
                                        "                                            \"align\": \"end\"\n" +
                                        "                                        }\n" +
                                        "                                    ]\n" +
                                        "                                },\n" +
                                        "                                {\n" +
                                        "                                    \"type\": \"separator\",\n" +
                                        "                                    \"margin\": \"xxl\"\n" +
                                        "                                },\n" +
                                        "                                {\n" +
                                        "                                    \"type\": \"box\",\n" +
                                        "                                    \"layout\": \"horizontal\",\n" +
                                        "                                    \"margin\": \"xxl\",\n" +
                                        "                                    \"contents\": [\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"ITEMS\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#555555\"\n" +
                                        "                                        },\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"3\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#111111\",\n" +
                                        "                                            \"align\": \"end\"\n" +
                                        "                                        }\n" +
                                        "                                    ]\n" +
                                        "                                },\n" +
                                        "                                {\n" +
                                        "                                    \"type\": \"box\",\n" +
                                        "                                    \"layout\": \"horizontal\",\n" +
                                        "                                    \"contents\": [\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"TOTAL\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#555555\"\n" +
                                        "                                        },\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"$7.31\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#111111\",\n" +
                                        "                                            \"align\": \"end\"\n" +
                                        "                                        }\n" +
                                        "                                    ]\n" +
                                        "                                },\n" +
                                        "                                {\n" +
                                        "                                    \"type\": \"box\",\n" +
                                        "                                    \"layout\": \"horizontal\",\n" +
                                        "                                    \"contents\": [\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"CASH\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#555555\"\n" +
                                        "                                        },\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"$8.0\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#111111\",\n" +
                                        "                                            \"align\": \"end\"\n" +
                                        "                                        }\n" +
                                        "                                    ]\n" +
                                        "                                },\n" +
                                        "                                {\n" +
                                        "                                    \"type\": \"box\",\n" +
                                        "                                    \"layout\": \"horizontal\",\n" +
                                        "                                    \"contents\": [\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"CHANGE\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#555555\"\n" +
                                        "                                        },\n" +
                                        "                                        {\n" +
                                        "                                            \"type\": \"text\",\n" +
                                        "                                            \"text\": \"$0.69\",\n" +
                                        "                                            \"size\": \"sm\",\n" +
                                        "                                            \"color\": \"#111111\",\n" +
                                        "                                            \"align\": \"end\"\n" +
                                        "                                        }\n" +
                                        "                                    ]\n" +
                                        "                                }\n" +
                                        "                            ]\n" +
                                        "                        },\n" +
                                        "                        {\n" +
                                        "                            \"type\": \"separator\",\n" +
                                        "                            \"margin\": \"xxl\"\n" +
                                        "                        },\n" +
                                        "                        {\n" +
                                        "                            \"type\": \"box\",\n" +
                                        "                            \"layout\": \"horizontal\",\n" +
                                        "                            \"margin\": \"md\",\n" +
                                        "                            \"contents\": [\n" +
                                        "                                {\n" +
                                        "                                    \"type\": \"text\",\n" +
                                        "                                    \"text\": \"PAYMENT ID\",\n" +
                                        "                                    \"size\": \"xs\",\n" +
                                        "                                    \"color\": \"#aaaaaa\",\n" +
                                        "                                    \"flex\": 0\n" +
                                        "                                },\n" +
                                        "                                {\n" +
                                        "                                    \"type\": \"text\",\n" +
                                        "                                    \"text\": \"#743289384279\",\n" +
                                        "                                    \"color\": \"#aaaaaa\",\n" +
                                        "                                    \"size\": \"xs\",\n" +
                                        "                                    \"align\": \"end\"\n" +
                                        "                                }\n" +
                                        "                            ]\n" +
                                        "                        }\n" +
                                        "                    ]\n" +
                                        "                }\n" +
                                        "            }\n" +
                                        "        }");
                            });
                }
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
}
