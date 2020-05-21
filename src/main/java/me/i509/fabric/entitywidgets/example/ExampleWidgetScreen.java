/*
 * The MIT License (MIT)
 *
 * Copyright (c) i509VCB<git@i509.me>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.i509.fabric.entitywidgets.example;

import me.i509.fabric.entitywidgets.EntityWidgetManipulation;
import me.i509.fabric.entitywidgets.PlayerWidget;
import me.i509.fabric.entitywidgets.TypedEntityWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.UUID;

public class ExampleWidgetScreen extends Screen {
	private final Screen parent;
	private PlayerWidget author;
	private PlayerWidget currentAccount;
	private TypedEntityWidget<CatEntity> cat;

	public ExampleWidgetScreen(final Screen parent) {
		super(new TranslatableText("entitywidget.example.screen"));
		this.parent = parent;
	}

	@Override
	public void init(MinecraftClient client, int width, int height) {
		super.init(client, width, height);
		this.author = new PlayerWidget(120, 120, 40, builder -> {
			builder.allParts().gameMode(GameMode.CREATIVE);
		}, (fakeClientPlayer, tickDelta) -> {
			fakeClientPlayer.setCustomNameVisible(true);
		}, EntityWidgetManipulation.followCursor(), UUID.fromString("765e5d33-c991-454f-8775-b6a7a394c097"), "The_1_gamers", false);
		this.currentAccount = new PlayerWidget(300, 200, 40, builder -> {
			builder.allParts().gameMode(GameMode.CREATIVE);
		}, (fakeClientPlayer, tickDelta) -> {
			fakeClientPlayer.setCustomNameVisible(true);
		}, EntityWidgetManipulation.followCursor(), UUID.fromString(this.client.getSession().getUuid()), this.client.getSession().getUsername());
		this.cat = new TypedEntityWidget<>(120, 200, 40, EntityType.CAT, (catEntity, tickDelta) -> {
			catEntity.setCatType(5); // Calico
			catEntity.setCustomName(new LiteralText("Coco").styled(style -> style.withColor(TextColor.parse("#C46210")).withBold(true)));
			catEntity.setCustomNameVisible(true);
		}, EntityWidgetManipulation.followCursor());
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		super.render(matrices, mouseX, mouseY, tickDelta);
		this.renderBackground(matrices);
		//noinspection ConstantConditions
		this.drawCenteredText(matrices, this.textRenderer, new TranslatableText("example.entity.widget"), this.width / 2, 10, Formatting.YELLOW.getColorValue());
		this.author.render(matrices, 120 - mouseX, 120 - mouseY, tickDelta);
		this.currentAccount.render(matrices, 300 - mouseX, 200 - mouseY, tickDelta);
		this.cat.render(matrices, 120 - mouseX, 200 - mouseY, tickDelta);
	}

	@Override
	public void onClose() {
		//noinspection ConstantConditions
		this.client.openScreen(this.parent);
	}
}
