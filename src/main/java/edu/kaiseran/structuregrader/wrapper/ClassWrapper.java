package edu.kaiseran.structuregrader.wrapper;

import edu.kaiseran.structuregrader.ClassMap;
import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.NamedSet;
import edu.kaiseran.structuregrader.property.Annotated;
import edu.kaiseran.structuregrader.property.Modified;
import edu.kaiseran.structuregrader.property.Named;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString.Exclude;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
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

	/**
	 * @return a NamedMap of the classes within this class.
	 */
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
	 * @return the fields of this class as a NamedMap of FieldWrappers.
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

	/**
	 * @return the methods of this class as a NamedMap of MethodWrappers.
	 */
	@NonNull
	public NamedMap<MethodWrapper> getMethods() {
		final Method[] methods = sourceClass.getDeclaredMethods();
		final Map<String, MethodWrapper> methodWrappers = Arrays.stream(methods)
				.map(MethodWrapper::new)
				.collect(Collectors.toMap(MethodWrapper::getName, Function.identity()));

		final String collectionName = getName();

		return NamedMap.<MethodWrapper>builder()
				.items(methodWrappers)
				.name(collectionName)
				.build();
	}

	/**
	 * @return the constructors of this class as a NamedSet of ConstructorWrappers.
	 */
	@NonNull
	public NamedSet<ConstructorWrapper> getConstructors() {
		final Constructor[] constructors = sourceClass.getDeclaredConstructors();
		final Set<ConstructorWrapper> wrappers = Arrays.stream(constructors)
				.map(ConstructorWrapper::new)
				.collect(Collectors.toSet());

		final String collectionName = getName();

		return NamedSet.<ConstructorWrapper>builder()
				.items(wrappers)
				.name(collectionName)
				.build();
	}

	/**
	 * @return the interfaces this class implements as a NamedSet of ClassWrapper.
	 */
	@NonNull
	public NamedSet<ClassWrapper> getInterfaces() {
		final Class[] interfaces = sourceClass.getInterfaces();
		final Set<ClassWrapper> wrappers = Arrays.stream(interfaces)
				.map(ClassWrapper::new)
				.collect(Collectors.toSet());

		final String collectionName = getName();

		return NamedSet.<ClassWrapper>builder()
				.items(wrappers)
				.name(collectionName)
				.build();
	}

	/**
	 * @return this class's superclass.
	 */
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
