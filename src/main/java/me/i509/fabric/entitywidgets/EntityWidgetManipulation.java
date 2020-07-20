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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Quaternion;

/**
 * Used to manipulate the rotation and components of an entity.
 *
 * @param <E> the type of entity.
 */
@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface EntityWidgetManipulation<E extends LivingEntity> {
	/**
	 * A manipulation which does nothing.
	 * @param <E> the entity type
	 * @return an entity manipulation which does nothing.
	 */
	static <E extends LivingEntity> EntityWidgetManipulation<E> none() {
		return (entity, matrices, mouseX, mouseY, tickDelta) -> Quaternion.IDENTITY;
	}

	/**
	 * A manipulation which flips the entity.
	 *
	 * <p>By default the rendering of entities will result in the entity being upside down.
	 *
	 * @param <E> the entity type
	 * @return an entity manipulation which flips the entity
	 */
	static <E extends LivingEntity> EntityWidgetManipulation<E> flip() {
		return (entity, matrices, mouseX, mouseY, tickDelta) -> {
			Quaternion quaternion = Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
			matrices.multiply(quaternion);
			return quaternion;
		};
	}



	/**
	 * A manipulation which rotates the entity.
	 *
	 * @param rotation the rotation
	 * @param <E> the entity type
	 * @return an entity manipulation which rotates the entity
	 */
	static <E extends LivingEntity> EntityWidgetManipulation<E> rotate(Quaternion rotation) {
		return (entity, matrices, mouseX, mouseY, tickDelta) -> {
			matrices.multiply(rotation);
			return rotation;
		};
	}

	/**
	 * A manipulation which makes an entity look towards the mouse cursor.
	 *
	 * @param <E> the entity type.
	 * @return an entity manipulation which makes an entity look towards the mouse cursor
	 */
	static <E extends LivingEntity> EntityWidgetManipulation<E> followCursor() {
		return (entity, matrices, mouseX, mouseY, tickDelta) -> {
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
		};
	}

	/**
	 * A manipulation which combines two manipulations.
	 *
	 * @param <E> the entity type.
	 * @return an entity manipulation which is a union of 2 entity manipulations
	 */
	static <E extends LivingEntity> EntityWidgetManipulation<E> union(EntityWidgetManipulation<E> manipulation,
			EntityWidgetManipulation<E> otherManipulation) {
		return (entity, matrices, mouseX, mouseY, tickDelta) -> {
			Quaternion result = manipulation.manipulate(entity, matrices, mouseX, mouseY, tickDelta);
			Quaternion otherResult = manipulation.manipulate(entity, matrices, mouseX, mouseY, tickDelta);
			result.hamiltonProduct(otherResult);

			return result;
		};
	}

	/**
	 * Manipulates an entity before rendering on screen.
	 *
	 * @param entity the entity, used for context
	 * @param matrices the matrices
	 * @param mouseX the x position of the mouse cursor
	 * @param mouseY the y position of the mouse cursor
	 * @param tickDelta the delta between the previous and next tick, for interpolation
	 * @return A {@link Quaternion} which the entity render is rotated by
	 */
	Quaternion manipulate(E entity, MatrixStack matrices, int mouseX, int mouseY, float tickDelta);
}
