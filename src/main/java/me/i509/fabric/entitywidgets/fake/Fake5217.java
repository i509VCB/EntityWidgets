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

import com.mojang.datafixers.Dynamic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5217;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.level.LevelGeneratorOptions;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallbackSerializer;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class Fake5217 implements class_5217 {
	@Override
	public long getSeed() {
		return 0;
	}

	@Override
	public int getSpawnX() {
		return 0;
	}

	@Override
	public void method_27416(int i) {
	}

	@Override
	public int getSpawnY() {
		return 0;
	}

	@Override
	public void method_27417(int i) {
	}

	@Override
	public int getSpawnZ() {
		return 0;
	}

	@Override
	public void method_27419(int i) {
	}

	@Override
	public long getTime() {
		return 0;
	}

	@Override
	public void setTime(long l) {
	}

	@Override
	public long getTimeOfDay() {
		return 0;
	}

	@Override
	public void setTimeOfDay(long l) {
	}

	@Override
	public String getLevelName() {
		return "Fake";
	}

	@Override
	public int getClearWeatherTime() {
		return 0;
	}

	@Override
	public void setClearWeatherTime(int i) {
	}

	@Override
	public boolean isThundering() {
		return false;
	}

	@Override
	public void setThundering(boolean bl) {
	}

	@Override
	public int getThunderTime() {
		return 0;
	}

	@Override
	public void setThunderTime(int i) {
	}

	@Override
	public boolean isRaining() {
		return false;
	}

	@Override
	public void setRaining(boolean bl) {
	}

	@Override
	public int getRainTime() {
		return 0;
	}

	@Override
	public void setRainTime(int i) {
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.NOT_SET;
	}

	@Override
	public boolean method_27420() {
		return false;
	}

	@Override
	public void setGameMode(GameMode gameMode) {
	}

	@Override
	public boolean isHardcore() {
		return false;
	}

	@Override
	public LevelGeneratorType getGeneratorType() {
		return LevelGeneratorType.DEFAULT;
	}

	@Override
	public LevelGeneratorOptions method_27421() {
		return LevelGeneratorOptions.createDefault(LevelGeneratorType.DEFAULT, new Dynamic<>(NbtOps.INSTANCE));
	}

	@Override
	public boolean areCommandsAllowed() {
		return false;
	}

	@Override
	public boolean isInitialized() {
		return false;
	}

	@Override
	public void setInitialized(boolean bl) {
	}

	@Override
	public GameRules getGameRules() {
		return new GameRules();
	}

	@Override
	public WorldBorder.class_5200 method_27422() {
		return null;
	}

	@Override
	public void method_27415(WorldBorder.class_5200 arg) {
	}

	@Override
	public Difficulty getDifficulty() {
		return Difficulty.HARD;
	}

	@Override
	public boolean isDifficultyLocked() {
		return false;
	}

	@Override
	public Timer<MinecraftServer> getScheduledEvents() {
		return new Timer<>(new TimerCallbackSerializer<>());
	}

	@Override
	public CompoundTag getWorldData() {
		return new CompoundTag();
	}

	@Override
	public void setWorldData(CompoundTag compoundTag) {
	}

	@Override
	public int getWanderingTraderSpawnDelay() {
		return 0;
	}

	@Override
	public void setWanderingTraderSpawnDelay(int i) {
	}

	@Override
	public int getWanderingTraderSpawnChance() {
		return 0;
	}

	@Override
	public void setWanderingTraderSpawnChance(int i) {
	}

	@Override
	public void setWanderingTraderId(UUID uUID) {
	}
}
