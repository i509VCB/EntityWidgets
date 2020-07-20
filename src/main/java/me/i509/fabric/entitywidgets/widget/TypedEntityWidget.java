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

import me.i509.fabric.entitywidgets.EntityWidgetManipulation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A widget which allows an entity to be rendered on a screen.
 *
 * <p>For rendering a {@link EntityType#PLAYER}, please use {@link PlayerWidget}.
 *
 * <p>Note this should not be used in game.
 *
 * @param <E> The entity type
 */
@Environment(EnvType.CLIENT)
public class TypedEntityWidget<E extends LivingEntity> extends AbstractEntityWidget<E> {
	private final EntityType<E> entityType;

	/**
	 * Creates an entity widget which uses an entity type as a template for rendering.
	 *
	 * @param x the x position of the entity on screen
	 * @param y the y position of the entity on screen
	 * @param size the size of the entity
	 * @param entityType the entity type to use
	 * @param entityManipulator the manipulator used to modify the entity before rendering.
	 * @apiNote The entity manipulator uses both the created entity and the tickDelta to allow for interpolation
	 * @throws IllegalArgumentException if the {@link EntityType} is {@link EntityType#PLAYER}.
	 * @see PlayerWidget PlayerWidget for rendering players.
	 */
	public TypedEntityWidget(int x, int y, int size, EntityType<E> entityType, Consumer<E> entityManipulator,
			EntityWidgetManipulation<E> manipulation) {
		super(x, y, size, entityManipulator, manipulation);

		if (entityType == EntityType.PLAYER) {
			throw new IllegalArgumentException(
					"Cannot render a player in a normal entity widget. Please use the PlayerWidget instead."
			);
		}

		this.entityType = entityType;
	}

	@Override
	protected E createEntity() {
		return this.entityType.create(FAKE_CLIENT_WORLD);
	}
}
