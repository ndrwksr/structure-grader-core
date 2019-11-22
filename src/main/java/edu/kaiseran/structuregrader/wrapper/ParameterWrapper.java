package edu.kaiseran.structuregrader.wrapper;

import edu.kaiseran.structuregrader.property.Ordinal;
import edu.kaiseran.structuregrader.property.Variable;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * A wrapper for Parameter, so that Parameter can be made to implement Variable and Ordinal.
 */
@Data
public class ParameterWrapper implements Variable, Ordinal {
	/**
	 * The original field that this wrapper represents.
	 */
	@NonNull
	private final Parameter sourceParam;

	/**
	 * The index of the Parameter in its owning Method.
	 */
	private final int index;

	@Override
	public int getModifiers() {
		return sourceParam.getModifiers();
	}

	@Override
	public @NonNull String getName() {
		return sourceParam.getName();
	}

	@Override
	public boolean isSynthetic() {
		return sourceParam.isSynthetic();
	}

	@Override
	public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
		return sourceParam.getAnnotation(annotationClass);
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return sourceParam.getDeclaredAnnotations();
	}


	@Override
	public @NonNull Type getType() {
		return sourceParam.getType();
	}

	@Nullable
	@Override
	public Type getGenericType() {
		return sourceParam.getParameterizedType();
	}
}
