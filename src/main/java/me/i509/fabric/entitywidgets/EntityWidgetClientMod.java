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

import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import me.i509.fabric.entitywidgets.mixin.SkullBlockEntityAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.UserCache;

import java.io.File;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class EntityWidgetClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Initialize the SkullBlockEntity user cache now so we don't need to initialize when we create a player widget.
		if (SkullBlockEntityAccessor.accessor$getUserCache() == null) {
			MinecraftClient.getInstance().execute(() -> {
				AuthenticationService authenticationService = new YggdrasilAuthenticationService(
						MinecraftClient.getInstance().getNetworkProxy(),
						UUID.randomUUID().toString()
				);
				MinecraftSessionService minecraftSessionService = authenticationService.createMinecraftSessionService();
				GameProfileRepository gameProfileRepository = authenticationService.createProfileRepository();
				UserCache userCache = new UserCache(
						gameProfileRepository,
						new File(
								MinecraftClient.getInstance().runDirectory,
								MinecraftServer.USER_CACHE_FILE.getName()
						)
				);
				SkullBlockEntity.setUserCache(userCache);
				SkullBlockEntity.setSessionService(minecraftSessionService);
				UserCache.setUseRemote(false);
			});
		}
	}
}
