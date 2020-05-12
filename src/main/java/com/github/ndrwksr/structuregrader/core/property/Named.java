package com.github.ndrwksr.structuregrader.core.property;

import lombok.NonNull;

/**
 * Interface for wrappers and collections to provide their name.
 */
public interface Named {
	/**
	 * @return the name of the source element.
	 */
	@NonNull
	String getName();
}
