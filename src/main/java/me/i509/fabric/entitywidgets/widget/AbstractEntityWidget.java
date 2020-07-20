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

package me.i509.fabric.entitywidgets.widget;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import me.i509.fabric.entitywidgets.EntityWidgetManipulation;
import me.i509.fabric.entitywidgets.fake.FakeClientPlayNetworkHandler;
import me.i509.fabric.entitywidgets.fake.FakeClientWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.Formatting;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.Difficulty;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A widget which allows an entity to be rendered on a screen.
 *
 * <p>Note this should not be used in game.
 *
 * @param <E> The entity type
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractEntityWidget<E extends LivingEntity> implements Drawable, Element {
	private static final Camera FAKE_CAMERA = new Camera();
	private static final FakeClientPlayNetworkHandler FAKE_NETWORK_HANDLER = new FakeClientPlayNetworkHandler(
			// We have to coerce the game into thinking our player's profile exists here
			new GameProfile(UUID.randomUUID(), "FakeCamera")
	);
	protected static final FakeClientWorld FAKE_CLIENT_WORLD = new FakeClientWorld(FAKE_NETWORK_HANDLER, Difficulty.EASY);
	protected static final Lazy<ClientPlayerEntity> FAKE_CLIENT_PLAYER = new Lazy<>(() -> {
		return new ClientPlayerEntity(
				MinecraftClient.getInstance(),
				FAKE_CLIENT_WORLD,
				FAKE_CLIENT_WORLD.getNetworkHandler(),
				new StatHandler(),
				new ClientRecipeBook(
						new RecipeManager()
				),
				false,
				false
		);
	});
	private final int x;
	private final int y;
	private final int size;
	private final Consumer<E> entityManipulator;
	private final EntityWidgetManipulation<E> manipulation;

	private boolean visible = true;
	private boolean useDelta = false;

	protected AbstractEntityWidget(int x, int y, int size, Consumer<E> entityManipulator, EntityWidgetManipulation<E> manipulation) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.entityManipulator = entityManipulator;
		this.manipulation = manipulation;
	}

	/**
	 * Gets the x position of this widget.
	 *
	 * @return the x position of the widget
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Gets the y position of this widget.
	 *
	 * @return the y position of the widget
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Gets the amount the rendered entity will be scaled by.
	 *
	 * @return the scaled size of the entity
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Checks if this widget is visible.
	 *
	 * @return true if this widget is visible
	 */
	public boolean isVisible() {
		return this.visible;
	}

	/**
	 * Sets this widget's visibility.
	 *
	 * <p>If this widget is not visible, no entity will be rendered.
	 *
	 * @param visibility the visibility
	 */
	public void setVisible(boolean visibility) {
		this.visible = visibility;
	}

	/**
	 * Renders this widget.
	 *
	 * @param mouseX the x position of the mouse
	 * @param mouseY the y position of the mouse
	 * @param tickDelta the delta between the previous and next tick, for interpolation
	 */
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		if (!this.isVisible()) {
			return;
		}

		final EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderManager();
		final E entity = this.createEntity();
		this.entityManipulator.accept(entity);

		dispatcher.configure(FAKE_CLIENT_WORLD, FAKE_CAMERA, entity);
		matrices.push();

		// TODO: Figure out why I still need to call render system
		RenderSystem.pushMatrix();
		RenderSystem.translatef(this.getX(), this.getY(), -50.0F);
		RenderSystem.scalef(1.0F, 1.0F, -1.0F);

		matrices.scale(this.getSize(), this.getSize(), this.getSize());

		// Apply widget manipulations
		final Quaternion manipulationQuaternion = this.manipulation.manipulate(entity, matrices, mouseX, mouseY, tickDelta);
		dispatcher.setRotation(manipulationQuaternion);
		dispatcher.setRenderShadows(false);

		// If we are not in game, we MUST have a ClientPlayerEntity in order to render, so we create a fake one here.
		boolean inGame = MinecraftClient.getInstance().player != null;

		if (MinecraftClient.getInstance().player == null) {
			MinecraftClient.getInstance().player = FAKE_CLIENT_PLAYER.get();
		}

		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance()
				.getBufferBuilders()
				.getEntityVertexConsumers();
		this.render(dispatcher, matrices, immediate, mouseX, mouseY, entity, 15728880);

		if (!inGame) { // Clear the fake ClientPlayerEntity so the MusicTracker doesn't NPE next tick
			MinecraftClient.getInstance().player = null;
		}

		matrices.pop();
		RenderSystem.popMatrix();
	}

	private void render(EntityRenderDispatcher dispatcher, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, int mouseX, int mouseY, E entity, int light) {
		matrices.push();

		dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrices, vertexConsumers, light);
		vertexConsumers.draw();

		matrices.pop();
	}

	protected abstract E createEntity();
}
