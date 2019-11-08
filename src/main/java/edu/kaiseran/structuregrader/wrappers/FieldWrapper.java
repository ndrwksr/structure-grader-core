package edu.kaiseran.structuregrader.wrappers;

import edu.kaiseran.structuregrader.Named;
import edu.kaiseran.structuregrader.Typed;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;

/**
 * A wrapper for a Field which implements the required interfaces for specifications to visit it.
 */
@Data
@Builder
public class FieldWrapper implements Typed, AnnotatedElement, Member {
	/**
	 * The original field that this wrapper represents.
	 */
	@NonNull
	private final Field sourceField;

	/**
	 * The modifiers of the original field.
	 */
	@NonNull
	private final Integer modifiers;

	/**
	 * The type of the field <b>without</b> generic parameters.
	 */
	@NonNull
	private final Type type;

	/**
	 * The type of the field <b>with</b> generic parameters.
	 */
	@Nullable
	private final Type genericType;

	/**
	 * Returns a new FieldWrapper from the provided Field.
	 *
	 * @param field The field to build the new wrapper instance from.
	 * @return a new FieldWrapper from the provided Field.
	 */
	public static FieldWrapper buildFrom(@NonNull final Field field) {
		return FieldWrapper.builder()
				.sourceField(field)
				.modifiers(field.getModifiers())
				.type(field.getType())
				.genericType(field.getGenericType())
				.build();
	}

	@Override
	public int getModifiers() {
		return modifiers;
	}

	@Override
	public Class<?> getDeclaringClass() {
		return sourceField.getDeclaringClass();
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
	public Annotation[] getAnnotations() {
		return sourceField.getAnnotations();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return sourceField.getDeclaredAnnotations();
	}
}
