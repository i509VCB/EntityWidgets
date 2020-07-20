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

package me.i509.fabric.entitywidgets.fake;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.Map;

public class FakePlayerBuilder {
	private final FakeClientWorld world;
	private final GameProfile profile;
	private final Map<MinecraftProfileTexture.Type, Identifier> textures;
	private final String model;

	// Builder fields
	private final ImmutableList.Builder<PlayerModelPart> visibleParts = ImmutableList.builder();
	private GameMode gameMode = GameMode.NOT_SET;
	private boolean sneaking;

	public FakePlayerBuilder(
			FakeClientWorld world,
			GameProfile profile,
			@Nullable String model,
			Map<MinecraftProfileTexture.Type, Identifier> textures) {
		this.world = world;
		this.profile = profile;
		this.model = model;
		this.textures = textures;
	}

	public FakePlayerBuilder gameMode(GameMode gameMode) {
		this.gameMode = gameMode;
		return this;
	}

	public FakePlayerBuilder withPart(PlayerModelPart playerModelPart) {
		this.visibleParts.add(playerModelPart);
		return this;
	}

	public FakePlayerBuilder allParts() {
		this.visibleParts.addAll(Arrays.asList(PlayerModelPart.values()));
		return this;
	}

	public FakeClientPlayer build() {
		return new FakeClientPlayer(
				this.world,
				this.profile,
				this.gameMode,
				this.visibleParts.build(),
				this.textures,
				this.model
		);
	}
}
