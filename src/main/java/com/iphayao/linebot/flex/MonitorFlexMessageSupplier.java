package com.iphayao.linebot.flex;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.flex.component.*;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.unit.FlexAlign;
import com.linecorp.bot.model.message.flex.unit.FlexFontSize;
import com.linecorp.bot.model.message.flex.unit.FlexLayout;
import com.linecorp.bot.model.message.flex.unit.FlexMarginSize;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
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
    String ipHost;
    String textFullStorage = "";

    private void init() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        URL jsonUrl = new URL("http://103.13.31.37:3000/sysinfo/metric");
        jsonUrl.openConnection().connect();
        Map map = mapper.readValue(jsonUrl, Map.class);
        JSONObject jsonObject = new JSONObject(map);

        serviceName = jsonObject.getString("serviceName");
        sysCpuLoad = jsonObject.getDouble("sysCpuLoad") * 100;
        proCpuLoad = jsonObject.getDouble("proCpuLoad") * 100;
        memTotal = jsonObject.getDouble("memTotal");
        memFreeTotal = jsonObject.getDouble("memFreeTotal");
        currentMemUse = jsonObject.getDouble("currentMemUse") * 100;
        availableCore = jsonObject.getDouble("availableCore");
        osName = jsonObject.getString("osName");
        osVersion = jsonObject.getString("osVersion");
        osArch = jsonObject.getString("osArch");
        storage = jsonObject.getJSONArray("storage");
        ipHost = jsonObject.getString("ipHost");

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

        for (int i = 0; i < storage.length(); i++) {
            JSONObject aStorage = storage.getJSONObject(i);
            String absPath = aStorage.getString("absPath");
            double freeSpace = aStorage.getLong("freeSpace") / 1073741824.00;
            double totalSpace = aStorage.getLong("totalSpace") / 1073741824.00;
            double usableSpace = aStorage.getLong("usableSpace") / 1073741824.00;
            textFullStorage = textFullStorage + String.format("partition (%s) \n [%.2f gb/%.2f gb] ~%.2f%% \n", absPath, totalSpace - usableSpace, totalSpace, ((totalSpace - usableSpace) / totalSpace) * 100);
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
//        Separator separator = Separator.builder().build();
        Text bodyBlockTextTitle = Text.builder()
                .text("Monitor VPS")
                .weight(Text.TextWeight.BOLD)
                .size(FlexFontSize.XL)
                .build();
        Text bodyBlockSubTextTitle = Text.builder()
                .text("#"+ipHost)
                .weight(Text.TextWeight.REGULAR)
                .size(FlexFontSize.XXS)
                .build();
        Box bodyBlockDetail = createInfoBox();
        Box bodyBlock = Box.builder()
                .layout(FlexLayout.VERTICAL)
                .contents(asList(bodyBlockTextTitle, bodyBlockSubTextTitle, bodyBlockDetail))
                .build();
        Box footerBlock = createFooterBlock();

        Bubble bubbleContainer = Bubble.builder()
                .hero(heroBlock)
                .body(bodyBlock)
                .footer(footerBlock)
                .build();

        return new FlexMessage("Monitor", bubbleContainer);
    }

    private Box createInfoBox() {
        final Box serviceBox = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder()
                                .text("Service")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(2)
                                .build(),
                        Text.builder()
                                .text(serviceName)
                                .wrap(true)
                                .color("#666666")
                                .flex(7)
                                .build()
                )).build();
        final Box osDetailBox = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("OS")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(2)
                                .build(),
                        Text.builder()
                                .text(osName + "\nv." + osVersion)
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(7)
                                .build()
                )).build();
        final Box osArchBox = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("OS Arch")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(2)
                                .build(),
                        Text.builder()
                                .text(osArch + " " + availableCore + " cores")
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(7)
                                .build()
                )).build();
        final Box cpuUsageBox = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("CPU")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(2)
                                .build(),
                        Text.builder()
                                .text("[" + textCpuBlock + "], (" + sysCpuLoad + "%)")
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(7)
                                .build()
                )).build();
        final Box memUageBox = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("Mem")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(2)
                                .build(),
                        Text.builder()
                                .text("[" + textMemBlock + "], (" + currentMemUse + "%)")
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(7)
                                .build()
                )).build();
        Box aStorageBox = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("Storage")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(2)
                                .build(),
                        Text.builder()
                                .text(textFullStorage)
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(7)
                                .build()
                )).build();

        return Box.builder()
                .layout(FlexLayout.VERTICAL)
                .margin(FlexMarginSize.LG)
                .contents(asList(serviceBox, osDetailBox, osArchBox, cpuUsageBox, memUageBox, aStorageBox))
                .build();
    }

    private Box createFooterBlock() {
        final Separator separator = Separator.builder().build();
        final Text detailText = Text.builder()
                .align(FlexAlign.CENTER)
                .text("Login by : [staff,123456]")
                .size(FlexFontSize.SM)
                .flex(4)
                .build();
        final Button websiteAction = Button.builder()
                .style(Button.ButtonStyle.PRIMARY)
                .height(Button.ButtonHeight.SMALL)
                .action(new URIAction("More Detail... ", "http://103.253.72.79:3000/d/takecare/host-overview?orgId=1"))
                .build();

        return Box.builder()
                .layout(FlexLayout.VERTICAL)
                .spacing(FlexMarginSize.SM)
                .contents(asList(separator, detailText, websiteAction))
                .build();
    }
}
