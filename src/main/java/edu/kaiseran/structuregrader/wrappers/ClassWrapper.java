package edu.kaiseran.structuregrader.wrappers;

import edu.kaiseran.structuregrader.ClassCollection;
import edu.kaiseran.structuregrader.Named;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString.Exclude;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Stores all of the information about a class which can be accessed via reflection at runtime.
 */
@SuppressWarnings("UnstableApiUsage") // ClassPath is marked as @Beta, so it's fine for applications
@Data
@SuperBuilder
public class ClassWrapper<T> implements Named, AnnotatedElement {

	/**
	 * The Class that this ClassStructure instance represents.
	 */
	@NonNull
	@Exclude
	private final Class<T> sourceClass;

	/**
	 * The Class that declared the Class which this ClassStructure instance represents. Null if this class is top-level.
	 */
	@Nullable
	@Exclude
	private final Class declaringClass;

	/**
	 * The super-Class of the Class which this instance represents. Null if this class is does not have a super class.
	 */
	@Nullable
	@Exclude
	private final Class superclass;

	/**
	 * The name of the Class this instance represents.
	 */
	@NonNull
	private final String name;

	/**
	 * A ClassCollection containing all of the ClassStructures which this ClassStructure declares.
	 */
	@NonNull
	private final ClassCollection classCollection;

	/**
	 * The annotations on the Class which this instance represents.
	 */
	@Nullable
	private final Annotation[] annotations;

	/**
	 * The constructors for the Class which this instance represents.
	 */
	@Nullable
	private final Constructor[] constructors;

	/**
	 * The fields for the Class which this instance represents.
	 */
	@Nullable
	private final Field[] fields;

	/**
	 * The interfaces which the Class this instance represents implements.
	 */
	@Nullable
	private final Class[] interfaces;

	/**
	 * The methods for the Class this instance represents.
	 */
	@Nullable
	private final Method[] methods;

	/**
	 * Factory method. Returns a new ClassStructure instance representing the provided Class.
	 *
	 * @param clazz The Class to build the ClassStructure instance from.
	 * @return a new ClassStructure instance representing the provided Class.
	 */
	@NonNull
	public static <T> ClassWrapper<T> buildFrom(@NonNull final Class<T> clazz) {
		final Map<String, ClassWrapper> declaredClasses = Arrays.stream(clazz.getDeclaredClasses())
				.map(ClassWrapper::buildFrom)
				.collect(Collectors.toMap(ClassWrapper::getName, Function.identity()));

		final ClassCollection classCollection = ClassCollection.builder()
				.items(declaredClasses)
				.name(clazz.getSimpleName())
				.build();
		final Class declaringClass = clazz.getDeclaringClass();

		return ClassWrapper.<T>builder()
				.sourceClass(clazz)
				.superclass(clazz.getSuperclass())
				.declaringClass(clazz.getDeclaringClass())
				.name(clazz.getSimpleName())
				.classCollection(classCollection)
				.annotations(clazz.getDeclaredAnnotations())
				.constructors(clazz.getDeclaredConstructors())
				.fields(clazz.getDeclaredFields())
				.interfaces(clazz.getInterfaces())
				.methods(clazz.getDeclaredMethods())
				.build();
	}


	@Override
	public <A extends Annotation> A getAnnotation(final Class<A> annotationClass) {
		return sourceClass.getAnnotation(annotationClass);
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return sourceClass.getDeclaredAnnotations();
	}
}
