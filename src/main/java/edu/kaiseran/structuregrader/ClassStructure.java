package edu.kaiseran.structuregrader;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString.Exclude;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
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
@Builder
public class ClassStructure {

	// -------------------------------------------------------------------------------------------------------------------
	// TRANSIENT FIELDS
	// -------------------------------------------------------------------------------------------------------------------

	/**
	 * The Class that this ClassStructure instance represents. Only available if the ClassStructure
	 * object was generated from a Class with {@link ClassStructure#buildFrom(Class)}, and is excluded
	 * from serialization.
	 */
	@Nullable
	@Exclude
	private final Class sourceClass;

	/**
	 * The Class that declared the Class which this ClassStructure instance represents. Only available
	 * if the ClassStructure object was generated from a Class with {@link
	 * ClassStructure#buildFrom(Class)}, and is excluded from serialization. Null if this class is
	 * top-level.
	 */
	@Nullable
	@Exclude
	private final Class declaringClass;

	/**
	 * The super-Class of the Class which this instance represents. Only available if the
	 * ClassStructure object was generated from a Class with {@link ClassStructure#buildFrom(Class)},
	 * and is excluded from serialization. Null if this class is does not have a super class.
	 */
	@Nullable
	@Exclude
	private final Class superclass;


	// -------------------------------------------------------------------------------------------------------------------
	// NON-TRANSIENT FIELDS
	// -------------------------------------------------------------------------------------------------------------------

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
	 * The name of the Class that declared the Class which this instance represents. Null if this
	 * class is top-level.
	 */
	@Nullable
	private final String declaringClassName;

	/**
	 * The name of the super-Class for the Class which this instance represents.
	 */
	@Nullable
	private final String superclassName;

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
	public static ClassStructure buildFrom(@NonNull final Class clazz) {
		final ClassStructureBuilder builder = new ClassStructureBuilder();

		builder.sourceClass(clazz);
		builder.superclass(clazz.getSuperclass());
		builder.declaringClass(clazz.getDeclaringClass());

		builder.name(clazz.getSimpleName());

		final Map<String, ClassStructure> declaredClasses = Arrays.stream(clazz.getDeclaredClasses())
				.map(ClassStructure::buildFrom)
				.collect(Collectors.toMap(ClassStructure::getName, Function.identity()));

		final ClassCollection classCollection = ClassCollection.builder()
				.declaredClasses(declaredClasses)
				.name(clazz.getSimpleName())
				.build();

		builder.classCollection(classCollection);

		final Class declaringClass = clazz.getDeclaringClass();

		builder.declaringClassName(declaringClass != null ? declaringClass.getSimpleName() : null);
		builder.superclassName(clazz.getSuperclass() != null ? clazz.getSuperclass().getSimpleName() : null);
		builder.annotations(clazz.getDeclaredAnnotations());
		builder.constructors(clazz.getDeclaredConstructors());
		builder.fields(clazz.getDeclaredFields());
		builder.interfaces(clazz.getInterfaces());
		builder.methods(clazz.getDeclaredMethods());

		return builder.build();
	}

	/**
	 * Provides a way for specifications to gather information about the a ClassStructure under
	 * evaluation.
	 */
	public interface ClassVisitor {

		/**
		 * Visits the provided class.
		 *
		 * @param classStructure The ClassStructure to visit.
		 */
		void visitClass(@NonNull final ClassStructure classStructure);
	}
}
