package com.iphayao.linebot.flex;

import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.flex.component.*;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.unit.FlexFontSize;
import com.linecorp.bot.model.message.flex.unit.FlexLayout;
import com.linecorp.bot.model.message.flex.unit.FlexMarginSize;

import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class MonitorFlexMessageSupplier implements Supplier<FlexMessage> {

    @Override
    public FlexMessage get() {
        Image heroBlock = Image.builder()
                .url("https://assetsds.cdnedge.bluemix.net/sites/default/files/styles/big_2/public/feature/images/potato_pc.jpg")
                .size(Image.ImageSize.FULL_WIDTH)
                .aspectRatio(Image.ImageAspectRatio.R20TO13)
                .aspectMode(Image.ImageAspectMode.Cover)
                .build();
        Separator separator = Separator.builder().build();
        Text bodyBlockTextTitle = Text.builder()
                .text("Monitor VPS")
                .weight(Text.TextWeight.BOLD)
                .size(FlexFontSize.XL)
                .build();
        Box bodyBlockDetail = createInfoBox();
        Box bodyBlock = Box.builder()
                .layout(FlexLayout.VERTICAL)
                .contents(asList(bodyBlockTextTitle,bodyBlockDetail))
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
        final Box serviceName = Box.builder()
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
                                .text("testServiceName")
                                .wrap(true)
                                .color("#666666")
                                .flex(5)
                                .build()
                )).build();
        final Box osDetail = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("OS")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(1)
                                .build(),
                        Text.builder()
                                .text("win10 10.0")
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(5)
                                .build()
                )).build();
        final Box osArch = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("OS Arch")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(1)
                                .build(),
                        Text.builder()
                                .text("amd64 , 8 core")
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(5)
                                .build()
                )).build();
        final Box cpuUsage = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("CPU Usage")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(1)
                                .build(),
                        Text.builder()
                                .text("████████▒▒ , 80%")
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(5)
                                .build()
                )).build();
        final Box memUage = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("Mem Usage")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(1)
                                .build(),
                        Text.builder()
                                .text("██▒▒▒▒▒▒▒▒ , 20%")
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(5)
                                .build()
                )).build();

        return Box.builder()
                .layout(FlexLayout.VERTICAL)
                .margin(FlexMarginSize.LG)
                .spacing(FlexMarginSize.SM)
                .contents(asList(serviceName, osDetail, osArch, cpuUsage, memUage))
                .build();
    }

    private Box createFooterBlock(){
        final Spacer spacer = Spacer.builder().size(FlexMarginSize.SM).build();
        final Button websiteAction = Button.builder()
                .style(Button.ButtonStyle.LINK)
                .height(Button.ButtonHeight.SMALL)
                .action(new URIAction("More Detail...", "https://example.com"))
                .build();

        return Box.builder()
                .layout(FlexLayout.VERTICAL)
                .spacing(FlexMarginSize.SM)
                .contents(asList(spacer, websiteAction))
                .build();
    }
}
