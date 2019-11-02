package edu.kaiseran.structuregrader;

import lombok.NonNull;

/**
 * An interface that requires that implementors have a name which can be accessed with getName().
 */
public interface Named {
	/**
	 * @return the name of the implementor.
	 */
	@NonNull String getName();
}
