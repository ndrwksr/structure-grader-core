package com.github.ndrwksr.structuregrader.core.property;

import java.lang.reflect.Modifier;

/**
 * Interface for wrappers to provide the modifiers on their source elements, and whether or not the source element is
 * synthetic. Related to Member.
 */
public interface Modified extends Named {
	/**
	 * Returns the Java language modifiers for the member or
	 * constructor represented by this Member, as an integer.  The
	 * Modifier class should be used to decode the modifiers in
	 * the integer.
	 *
	 * @return the Java language modifiers for the underlying member
	 * @see Modifier
	 */
	int getModifiers();

	/**
	 * Returns {@code true} if this member was introduced by
	 * the compiler; returns {@code false} otherwise.
	 *
	 * @return true if and only if this member was introduced by
	 * the compiler.
	 * @jls 13.1 The Form of a Binary
	 * @since 1.5
	 */
	boolean isSynthetic();
}
