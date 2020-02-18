package edu.kaiseran.structuregrader.core.wrapper;

import edu.kaiseran.structuregrader.core.property.Variable;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * A wrapper for Field, so that Field can implement Variable.
 */
@Data
public class FieldWrapper implements Variable {
	/**
	 * The Field this wrapper represents.
	 */
	private final Field sourceField;

	@Override
	public int getModifiers() {
		return sourceField.getModifiers();
	}

	@Override
	public @NonNull String getName() {
		return sourceField.getName();
	}

	@Override
	public boolean isSynthetic() {
		return sourceField.isSynthetic();
	}

	@Override
	public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
		return sourceField.getAnnotation(annotationClass);
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return sourceField.getDeclaredAnnotations();
	}

	@Override
	public @NonNull Type getType() {
		return sourceField.getType();
	}

	@Nullable
	@Override
	public Type getGenericType() {
		return sourceField.getGenericType();
	}
}
