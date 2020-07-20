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

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class FakeClientPlayNetworkHandler extends ClientPlayNetworkHandler {
	private final DimensionType dimensionType;
	private final RegistryTracker registryTracker;

	public FakeClientPlayNetworkHandler(GameProfile profile) {
		super(MinecraftClient.getInstance(), null, new ClientConnection(NetworkSide.CLIENTBOUND), profile);
		final RegistryTracker.Modifiable registryTracker = RegistryTracker.create();
		this.registryTracker = registryTracker;
		this.dimensionType = registryTracker.getDimensionTypeRegistry().get(DimensionType.OVERWORLD_REGISTRY_KEY.getValue());
	}

	public DimensionType getFakeDimensionType() {
		return this.dimensionType;
	}

	public RegistryTracker getRegistryTracker() {
		return this.registryTracker;
	}
}
