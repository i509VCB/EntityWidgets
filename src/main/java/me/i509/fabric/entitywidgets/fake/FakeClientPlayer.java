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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

/**
 * A fake player entity which can be used in rendering.
 */
@Environment(EnvType.CLIENT)
public class FakeClientPlayer extends AbstractClientPlayerEntity {
	private final GameMode gameMode;
	private final ImmutableList<PlayerModelPart> visibleModelParts;
	private final Map<MinecraftProfileTexture.Type, Identifier> textures;
	@Nullable private final String model;
	private boolean sneaking;
	private EntityPose pose = EntityPose.STANDING;

	FakeClientPlayer(
			ClientWorld world,
			GameProfile profile,
			GameMode gameMode,
			ImmutableList<PlayerModelPart> visibleModelParts,
			Map<MinecraftProfileTexture.Type, Identifier> textures,
			@Nullable String model) {
		super(world, profile);
		this.gameMode = gameMode;
		this.visibleModelParts = visibleModelParts;
		this.textures = textures;
		this.model = model;
	}

	@Override
	public boolean isSpectator() {
		return this.gameMode == GameMode.SPECTATOR;
	}

	@Override
	public boolean isCreative() {
		return this.gameMode == GameMode.CREATIVE;
	}

	@Override
	public String getModel() {
		if (this.model != null) {
			return this.model;
		}

		return "default";
	}

	@Override
	public Identifier getSkinTexture() {
		Identifier texture = this.textures.get(MinecraftProfileTexture.Type.SKIN);
		return texture == null ? DefaultSkinHelper.getTexture(this.getUuid()) : texture;
	}

	@Override
	public boolean hasSkinTexture() {
		return true;
	}

	@Nullable
	@Override
	public Identifier getCapeTexture() {
		return this.textures.get(MinecraftProfileTexture.Type.CAPE);
	}

	public boolean canRenderCapeTexture() {
		return true;
	}

	@Override
	public boolean canRenderElytraTexture() {
		return true;
	}

	@Nullable
	@Override
	public Identifier getElytraTexture() {
		return this.textures.get(MinecraftProfileTexture.Type.ELYTRA);
	}

	@Override
	public boolean isPartVisible(PlayerModelPart modelPart) {
		return this.visibleModelParts.contains(modelPart);
	}

	@Override
	public boolean isSneaking() {
		return this.sneaking;
	}

	@Override
	public void setSneaking(boolean sneaking) {
		this.sneaking = sneaking;
	}

	@Override
	public boolean isSneaky() {
		return this.isSneaking();
	}

	@Override
	public void setPose(EntityPose pose) {
		this.pose = pose;
	}

	@Override
	public EntityPose getPose() {
		return this.pose;
	}
}
