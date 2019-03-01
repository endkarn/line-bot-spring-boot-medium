package com.iphayao.linebot.flex;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.flex.component.*;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.unit.FlexFontSize;
import com.linecorp.bot.model.message.flex.unit.FlexLayout;
import com.linecorp.bot.model.message.flex.unit.FlexMarginSize;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class MonitorFlexMessageSupplier implements Supplier<FlexMessage> {

    String serviceName;
    double sysCpuLoad;
    double proCpuLoad;
    double memTotal;
    double memFreeTotal;
    double currentMemUse;
    double availableCore;
    String osName;
    String osVersion;
    String osArch;
    JSONArray storage;
    int cpuPercentBlock;
    int memPercentBlock;
    String textCpuBlock;
    String textMemBlock;

    private void init() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        URL jsonUrl = new URL("http://103.253.72.79:8080/takecare-pos-service/sysinfo/metric");
        jsonUrl.openConnection().connect();
        Map map = mapper.readValue(jsonUrl, Map.class);
        JSONObject jsonObject = new JSONObject(map);

        serviceName = jsonObject.getString("serviceName");
        sysCpuLoad = jsonObject.getDouble("sysCpuLoad") * 100;
        proCpuLoad = jsonObject.getDouble("proCpuLoad") * 100;
        memTotal = jsonObject.getDouble("memTotal");
        memFreeTotal = jsonObject.getDouble("memFreeTotal");
        currentMemUse = jsonObject.getDouble("currentMemUse");
        availableCore = jsonObject.getDouble("availableCore");
        osName = jsonObject.getString("osName");
        osVersion = jsonObject.getString("osVersion");
        osArch = jsonObject.getString("osArch");
        storage = jsonObject.getJSONArray("storage");

        cpuPercentBlock = (int) sysCpuLoad / 10;
        memPercentBlock = (int) currentMemUse / 10;
        textCpuBlock = "";
        textMemBlock = "";
        for (int i = 0; i <= 10; i++) {
            if (i <= cpuPercentBlock)
                textCpuBlock = textCpuBlock + "█";
            else
                textCpuBlock = textCpuBlock + "▒";

            if (i <= memPercentBlock)
                textMemBlock = textMemBlock + "█";
            else
                textMemBlock = textMemBlock + "▒";

        }
    }

    @Override
    public FlexMessage get() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Image heroBlock = Image.builder()
                .url("https://assetsds.cdnedge.bluemix.net/sites/default/files/styles/big_2/public/feature/images/potato_pc.jpg")
                .size(Image.ImageSize.FULL_WIDTH)
                .aspectRatio(Image.ImageAspectRatio.R16TO9)
                .aspectMode(Image.ImageAspectMode.Cover)
                .build();
        Separator separator = Separator.builder().build();
        Text bodyBlockTextTitle = Text.builder()
                .text("Monitor VPS")
                .weight(Text.TextWeight.BOLD)
                .size(FlexFontSize.XL)
                .build();
        Box bodyBlockDetail = null;
        try {
            bodyBlockDetail = createInfoBox();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Box bodyBlock = Box.builder()
                .layout(FlexLayout.VERTICAL)
                .contents(asList(bodyBlockTextTitle, bodyBlockDetail))
                .build();
        Box footerBlock = createFooterBlock();

        Bubble bubbleContainer = Bubble.builder()
                .hero(heroBlock)
                .body(bodyBlock)
                .footer(footerBlock)
                .build();

        return new FlexMessage("Monitor", bubbleContainer);
    }

    private Box createInfoBox() throws JSONException {
        final Box serviceBox = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder()
                                .text("Service")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(1)
                                .build(),
                        Text.builder()
                                .text(serviceName)
                                .wrap(true)
                                .color("#666666")
                                .flex(5)
                                .build()
                )).build();
        final Box osDetailBox = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("OS")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(1)
                                .build(),
                        Text.builder()
                                .text(osName + " v." + osVersion)
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(5)
                                .build()
                )).build();
        final Box osArchBox = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("OS Arch")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(1)
                                .build(),
                        Text.builder()
                                .text(osArch + "," + availableCore + " cores")
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(5)
                                .build()
                )).build();
        final Box cpuUsageBox = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("CPU Usage")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(1)
                                .build(),
                        Text.builder()
                                .text("[" + textCpuBlock + "], (" + sysCpuLoad + "%)")
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(5)
                                .build()
                )).build();
        final Box memUageBox = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("Mem Usage")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(1)
                                .build(),
                        Text.builder()
                                .text("[" + textMemBlock + "], (" + currentMemUse + "%)")
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(5)
                                .build()
                )).build();

        final List<Box> storageBoxList = new ArrayList<>();
        for (int i = 0; i < storage.length(); i++) {
            JSONObject aStorage = storage.getJSONObject(i);
            Box aStorageBox = Box.builder()
                    .layout(FlexLayout.BASELINE)
                    .spacing(FlexMarginSize.SM)
                    .contents(asList(
                            Text.builder().text("Storeage Usage")
                                    .color("#aaaaaa")
                                    .size(FlexFontSize.SM)
                                    .flex(1)
                                    .build(),
                            Text.builder()
                                    .text("test \n\n\n\n test")
                                    .wrap(true)
                                    .color("#666666")
                                    .size(FlexFontSize.SM)
                                    .flex(5)
                                    .build()
                    )).build();
            storageBoxList.add(aStorageBox);

        }

        return Box.builder()
                .layout(FlexLayout.VERTICAL)
                .margin(FlexMarginSize.LG)
                .spacing(FlexMarginSize.SM)
                .contents(asList(serviceBox, osDetailBox, osArchBox, cpuUsageBox, memUageBox))
                .build();
    }

    private Box createFooterBlock() {
        final Spacer spacer = Spacer.builder().size(FlexMarginSize.SM).build();
        final Button websiteAction = Button.builder()
                .style(Button.ButtonStyle.PRIMARY)
                .height(Button.ButtonHeight.MEDIUM)
                .action(new URIAction("More Detail...", "https://example.com"))
                .build();

        return Box.builder()
                .layout(FlexLayout.VERTICAL)
                .spacing(FlexMarginSize.SM)
                .contents(asList(spacer, websiteAction))
                .build();
    }
}
