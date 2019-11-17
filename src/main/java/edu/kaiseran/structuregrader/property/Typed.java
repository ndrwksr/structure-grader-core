package edu.kaiseran.structuregrader.property;

import lombok.NonNull;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * An interface for Named implementors to provide their type to specifications.
 */
public interface Typed extends Named {
	/**
	 * @return the type of the implementor <b>without</b> generic parameters.
	 */
	@NonNull
	Type getType();

	/**
	 * @return the type of the implementor <b>with</b> generic parameters.
	 */
	@Nullable
	Type getGenericType();
}
