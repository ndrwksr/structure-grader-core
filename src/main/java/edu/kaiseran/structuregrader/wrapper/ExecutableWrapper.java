package edu.kaiseran.structuregrader.wrapper;

import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.property.Annotated;
import edu.kaiseran.structuregrader.property.Modified;
import edu.kaiseran.structuregrader.property.Parameterized;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps and Executable so that it can implement the required interfaces.
 *
 * @param <EXEC> The type of the Executable being wrapped.
 */
@RequiredArgsConstructor
public class ExecutableWrapper<EXEC extends Executable> implements Annotated, Modified, Parameterized {
	/**
	 * The Executable that this wrapper represents.
	 */
	protected final EXEC sourceExecutable;

	@Override
	public @NonNull Annotation[] getDeclaredAnnotations() {
		return sourceExecutable.getDeclaredAnnotations();
	}

	@Nullable
	@Override
	public <A extends Annotation> A getAnnotation(final Class<A> annotationClass) {
		return sourceExecutable.getAnnotation(annotationClass);
	}

	@Override
	public int getModifiers() {
		return sourceExecutable.getModifiers();
	}

	@Override
	public boolean isSynthetic() {
		return sourceExecutable.isSynthetic();
	}

	@Override
	public @NonNull String getName() {
		return sourceExecutable.getName();
	}

	@Override
	public NamedMap<ParameterWrapper> getParameters() {
		final Parameter[] parameters = sourceExecutable.getParameters();
		final Map<String, ParameterWrapper> wrappers = new HashMap<>();
		for (int i = 0; i < parameters.length; i++) {
			wrappers.put(parameters[i].getName(), new ParameterWrapper(parameters[i], i));
		}
		return NamedMap.<ParameterWrapper>builder()
				.items(wrappers)
				.name(sourceExecutable.getName() + "#fields")
				.build();
	}
}
