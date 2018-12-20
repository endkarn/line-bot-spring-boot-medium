package com.iphayao.linebot.flex;

import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.flex.component.*;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.unit.FlexFontSize;
import com.linecorp.bot.model.message.flex.unit.FlexLayout;
import com.linecorp.bot.model.message.flex.unit.FlexMarginSize;


import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class TakeCareFlexMessageSupplier implements Supplier<FlexMessage> {

    @Override
    public FlexMessage get() {
        Image heroBlock = Image.builder()
                .url("https://takecarebeauty.com/assets/images/branch/s_19.jpg")
                .size(Image.ImageSize.FULL_WIDTH)
                .aspectRatio(Image.ImageAspectRatio.R20TO13)
                .aspectMode(Image.ImageAspectMode.Cover)
                .action(new URIAction("label", "http://takecarebeauty.com/"))
                .build();

        Separator separator = Separator.builder().build();

        Text bodyBlockTextTitle = Text.builder()
                .text("Take Care Beauty")
                .weight(Text.TextWeight.BOLD)
                .size(FlexFontSize.XL)
                .build();
        Box bodyBlockInfo = createInfoBox();
        Text bodyBlockTextDetail = Text.builder()
                .text("Take Care is dedicated to the pursuit of offering extraordinary experience with exceptional service. We believe that creating a beautiful appearance is the quintessential standard for every salon's philosophy, to this point, our mission is not only that, but inclusively to strive and commit to the higher level of all beauty aspects than that - to develop optimistically confidence and happiness in self, which leads to the ultimate goal in building up the third area in life to be exquisitely confident.")
                .wrap(true).size(FlexFontSize.XS).margin(FlexMarginSize.XL)
                .build();

        Box bodyBlock = Box.builder()
                .layout(FlexLayout.VERTICAL)
                .contents(asList(bodyBlockTextTitle,bodyBlockInfo,separator,bodyBlockTextDetail))
                .build();

        Box footerBlock = createFooterBlock();



        Bubble bubbleContainer = Bubble.builder()
                .hero(heroBlock)
                .body(bodyBlock)
                .footer(footerBlock)
                .build();

        return new FlexMessage("TakeCareFlexMessage" , bubbleContainer);
    }

    private Box createInfoBox() {
        final Box place = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder()
                                .text("Place")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(1)
                                .build(),
                        Text.builder()
                                .text("Wattana , Bangkok")
                                .wrap(true)
                                .color("#666666")
                                .flex(5)
                                .build()
                )).build();
        final Box time = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("Time")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(1)
                                .build(),
                        Text.builder()
                                .text("10:00 - 23:00")
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(5)
                                .build()
                )).build();
        final Box tel = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("Tel")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(1)
                                .build(),
                        Text.builder()
                                .text("02-254-4900 - 06")
                                .wrap(true)
                                .color("#666666")
                                .size(FlexFontSize.SM)
                                .flex(5)
                                .build()
                )).build();
        final Box email = Box.builder()
                .layout(FlexLayout.BASELINE)
                .spacing(FlexMarginSize.SM)
                .contents(asList(
                        Text.builder().text("Email")
                                .color("#aaaaaa")
                                .size(FlexFontSize.SM)
                                .flex(1)
                                .build(),
                        Text.builder()
                                .text("info@takecarebeauty.com")
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
                .contents(asList(place, time,tel ,email))
                .build();
    }

    private Box createFooterBlock() {
        final Spacer spacer = Spacer.builder().size(FlexMarginSize.SM).build();

        final Button websiteAction = Button.builder()
                .style(Button.ButtonStyle.LINK)
                .height(Button.ButtonHeight.SMALL)
                .action(new URIAction("WEBSITE", "https://example.com"))
                .build();

        return Box.builder()
                .layout(FlexLayout.VERTICAL)
                .spacing(FlexMarginSize.SM)
                .contents(asList(spacer, websiteAction))
                .build();

    }
}
