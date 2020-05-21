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

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.Difficulty;

import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * A widget which allows an entity to be rendered on a screen.
 *
 * <p>Note this should not be used in game.
 *
 * @param <E> The entity type
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractEntityWidget<E extends LivingEntity> implements Drawable, Element {
	private final int x;
	private final int y;
	private final int size;
	private final BiConsumer<E, Float> entityManipulator;
	private final EntityWidgetManipulation<E> manipulation;
	protected final FakeClientPlayNetworkHandler fakeClientPlayNetworkHandler = new FakeClientPlayNetworkHandler(
			// We have to coerce the game into thinking our player's profile exists here
			new GameProfile(UUID.randomUUID(), "FakeCamera")
	);
	protected final FakeClientWorld fakeClientWorld;

	private boolean visible = true;

	protected AbstractEntityWidget(int x, int y, int size,
			BiConsumer<E, Float> entityManipulator, EntityWidgetManipulation<E> manipulation) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.entityManipulator = entityManipulator;
		this.manipulation = manipulation;
		this.fakeClientWorld = new FakeClientWorld(this.fakeClientPlayNetworkHandler, Difficulty.EASY);
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
	 * @param delta the delta between the previous and next tick, for interpolation
	 */
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (!this.isVisible()) {
			return;
		}

		EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderManager();
		E entity = this.createEntity();
		this.entityManipulator.accept(entity, delta);

		dispatcher.configure(this.fakeClientWorld, new Camera(), entity);

		matrices.push();

		RenderSystem.pushMatrix();
		RenderSystem.translatef(this.getX(), this.getY(), -50.0F);
		RenderSystem.scalef(1.0F, 1.0F, -1.0F);

		matrices.scale(this.getSize(), this.getSize(), this.getSize());

		Quaternion manipulationQuaternion = this.manipulation.manipulate(entity, matrices, mouseX, mouseY, delta);
		dispatcher.setRotation(manipulationQuaternion);
		dispatcher.setRenderShadows(false);

		// If we are not in game, we MUST have a ClientPlayerEntity in order to render, so we create a fake one here.
		boolean inGame = false;

		if (MinecraftClient.getInstance().player == null) {
			MinecraftClient.getInstance().player = new ClientPlayerEntity(
					MinecraftClient.getInstance(),
					this.fakeClientWorld,
					this.fakeClientPlayNetworkHandler,
					new StatHandler(),
					new ClientRecipeBook(
							new RecipeManager()
					)
			);
		} else {
			inGame = true;
		}

		VertexConsumerProvider.Immediate immediate = MinecraftClient
				.getInstance()
				.getBufferBuilders()
				.getEntityVertexConsumers();
		dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrices, immediate, 15728880);
		immediate.draw();
		//this.postRender(entity, matrices);

		matrices.pop();
		RenderSystem.popMatrix();

		if (!inGame) { // Clear the fake ClientPlayerEntity so the MusicTracker doesn't NPE next tick
			MinecraftClient.getInstance().player = null;
		}
	}

	protected abstract E createEntity();

	static <E extends LivingEntity> Quaternion noManipulation(E entity, MatrixStack matrices, int mouseX, int mouseY,
			float tickDelta) {
		return Quaternion.IDENTITY;
	}

	static <E extends LivingEntity> Quaternion followCursor(E entity, MatrixStack matrices, int mouseX, int mouseY,
															float tickDelta) {
		float mouseXFacing = (float) Math.atan(mouseX / 40.0F);
		float mouseYFacing = (float) Math.atan(mouseY / 40.0F);
		Quaternion flipQuaternion = Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
		Quaternion tiltQuaternion = Vector3f.POSITIVE_X.getDegreesQuaternion(mouseYFacing * 20.0F);

		flipQuaternion.hamiltonProduct(tiltQuaternion);
		matrices.multiply(flipQuaternion);

		entity.bodyYaw = 180.0F + mouseXFacing * 20.0F;
		entity.yaw = 180.0F + mouseXFacing * 40.0F;
		entity.pitch = -mouseYFacing * 20.0F;
		entity.headYaw = entity.yaw;
		entity.prevHeadYaw = entity.yaw;

		tiltQuaternion.conjugate();
		return tiltQuaternion;
	}

	static <E extends LivingEntity> Quaternion flip(E entity, MatrixStack matrices, int mouseX, int mouseY,
													float tickDelta) {
		Quaternion quaternion = Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
		matrices.multiply(quaternion);
		return quaternion;
	}
}
