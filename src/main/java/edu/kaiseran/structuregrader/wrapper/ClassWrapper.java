package edu.kaiseran.structuregrader.wrapper;

import edu.kaiseran.structuregrader.ClassMap;
import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.property.Annotated;
import edu.kaiseran.structuregrader.property.Modified;
import edu.kaiseran.structuregrader.property.Named;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString.Exclude;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Stores all of the information about a class which can be accessed via reflection at runtime.
 */
@Data
@AllArgsConstructor
public class ClassWrapper implements Named, Annotated, Modified {

	/**
	 * The Class that this ClassStructure instance represents.
	 */
	@NonNull
	@Exclude
	private final Class sourceClass;

	@Override
	public <A extends Annotation> A getAnnotation(final Class<A> annotationClass) {
		return (A) sourceClass.getAnnotation(annotationClass);
	}

	public NamedMap<ClassWrapper> getDeclaredClasses() {
		final Map<String, ClassWrapper> declaredClasses = Arrays.stream(sourceClass.getDeclaredClasses())
				.map(ClassWrapper::new)
				.collect(Collectors.toMap(ClassWrapper::getName, Function.identity()));

		return ClassMap.builder()
				.items(declaredClasses)
				.name(this.getName())
				.build();
	}

	/**
	 * @return the fields array as a NamedCollection of FieldWrappers.
	 */
	@NonNull
	public NamedMap<FieldWrapper> getFields() {
		final Field[] fields = sourceClass.getDeclaredFields();
		final Map<String, FieldWrapper> fieldWrappers = Arrays.stream(fields)
				.map(FieldWrapper::new)
				.collect(Collectors.toMap(FieldWrapper::getName, Function.identity()));

		final String collectionName = getName() + "%fields";

		return NamedMap.<FieldWrapper>builder()
				.items(fieldWrappers)
				.name(collectionName)
				.build();
	}

	public Class getSuperclass() {
		return sourceClass.getSuperclass();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return sourceClass.getDeclaredAnnotations();
	}

	@Override
	public int getModifiers() {
		return sourceClass.getModifiers();
	}

	@Override
	public boolean isSynthetic() {
		return sourceClass.isSynthetic();
	}

	@Override
	public @NonNull String getName() {
		return sourceClass.getSimpleName();
	}
}
