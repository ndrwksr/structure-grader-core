package edu.kaiseran.structuregrader.wrapper;

import edu.kaiseran.structuregrader.property.Variable;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * A wrapper for Method so that Method can implement Variable, and Method can be extended (see getParameters()).
 */
public class MethodWrapper extends ExecutableWrapper<Method> implements Variable {
	/**
	 * @param sourceMethod The Method this wrapper will represent.
	 */
	public MethodWrapper(@NonNull final Method sourceMethod) {
		super(sourceMethod);
	}

	@Override
	public @NonNull Type getType() {
		return sourceExecutable.getReturnType();
	}

	@Nullable
	@Override
	public Type getGenericType() {
		return sourceExecutable.getGenericReturnType();
	}
}
