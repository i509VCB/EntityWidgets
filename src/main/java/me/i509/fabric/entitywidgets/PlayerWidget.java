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

package me.i509.fabric.entitywidgets;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import me.i509.fabric.entitywidgets.fake.FakeClientPlayer;
import me.i509.fabric.entitywidgets.fake.FakePlayerBuilder;
import me.i509.fabric.entitywidgets.mixin.SkullBlockEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.UserCache;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A widget which allows a {@link EntityType#PLAYER} to be rendered on a screen.
 *
 * <p>Note this should not be used in game.
 */
@Environment(EnvType.CLIENT)
public class PlayerWidget extends AbstractEntityWidget<FakeClientPlayer> {
	private static final AtomicInteger RETRIEVER_COUNTER = new AtomicInteger(1);
	private final Map<MinecraftProfileTexture.Type, Identifier> textures = Maps.newEnumMap(
			MinecraftProfileTexture.Type.class
	);
	private final Consumer<FakePlayerBuilder> playerBuilder;
	@MonotonicNonNull
	private GameProfile fullProfile;
	private GameProfile fallbackProfile;
	private boolean showName = false;
	@Nullable
	private String model;

	public PlayerWidget(
			int x,
			int y,
			int size,
			Consumer<FakePlayerBuilder> playerBuilder,
			BiConsumer<FakeClientPlayer, Float> entityManipulator,
			EntityWidgetManipulation<FakeClientPlayer> manipulation,
			UUID playerUuid,
			String fallbackUsername
	) {
		this(x, y, size, playerBuilder, entityManipulator, manipulation, playerUuid, fallbackUsername,
				DefaultSkinHelper.getModel(playerUuid).equals("slim"));
	}

	public PlayerWidget(
			int x,
			int y,
			int size,
			Consumer<FakePlayerBuilder> playerBuilder,
			BiConsumer<FakeClientPlayer, Float> entityManipulator,
			EntityWidgetManipulation<FakeClientPlayer> manipulation,
			UUID playerUuid,
			String fallbackUsername,
			boolean thinArms
	) {
		super(x, y, size, entityManipulator, manipulation);
		this.fallbackProfile = new GameProfile(playerUuid, fallbackUsername);
		this.playerBuilder = playerBuilder;
		this.model = thinArms ? "default" : "slim";
		// Now the futures nightmare
		GameProfile profile = new GameProfile(playerUuid, null);

		SkinRetrieverThread thread = new SkinRetrieverThread(profile);
		thread.start();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	protected FakeClientPlayer createEntity() {
		GameProfile profile = this.fullProfile;

		if (profile == null) {
			profile = this.fallbackProfile;
		}

		FakePlayerBuilder builder = new FakePlayerBuilder(this.fakeClientWorld, profile, this.model, this.textures);
		this.playerBuilder.accept(builder);

		return builder.build();
	}

	public boolean shouldDisplayName() {
		return this.showName;
	}

	public void displaysName(boolean display) {
		this.showName = display;
	}

	private class SkinRetrieverThread extends Thread {
		private final GameProfile profile;

		private SkinRetrieverThread(GameProfile profile) {
			super("Skin Retriever thread #" + PlayerWidget.RETRIEVER_COUNTER.getAndIncrement());
			this.profile = profile;
		}

		@Override
		public void run() {
			GameProfile profile = this.profile;
			MinecraftSessionService sessionService = SkullBlockEntityAccessor.accessor$getSessionService();
			UserCache cache = SkullBlockEntityAccessor.accessor$getUserCache();
			GameProfile namedProfile = cache.getByUuid(profile.getId());
			GameProfile fullProfile = SkullBlockEntity.loadProperties(namedProfile);

			MinecraftClient client = MinecraftClient.getInstance();

			client.execute(() -> {
				if (fullProfile != null) {
					PlayerWidget.this.fullProfile = fullProfile;
				}
			});

			Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textureMap = client.getSkinProvider()
					.getTextures(fullProfile);

			// Texture map is unchecked
			if (textureMap.isEmpty()) {
				textureMap = sessionService.getTextures(fullProfile, true);
			}

			Map<MinecraftProfileTexture.Type, Identifier> loadedTextures = new EnumMap<>(
					MinecraftProfileTexture.Type.class
			);

			for (MinecraftProfileTexture.Type type : textureMap.keySet()) {
				Identifier texture = client.getSkinProvider().loadSkin(textureMap.get(type), type);
				loadedTextures.put(type, texture);
			}

			client.execute(() -> {
				PlayerWidget.this.textures.putAll(loadedTextures);
			});
		}
	}
}
