package edu.kaiseran.structuregrader.wrapper;

import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.property.Variable;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper for Method so that Method can implement Variable, and Method can be extended (see getParameters()).
 */
@Data
public class MethodWrapper implements Variable {

	/**
	 * The Method this wrapper represents.
	 */
	private final Method sourceMethod;

	@Override
	public @NonNull Annotation[] getDeclaredAnnotations() {
		return sourceMethod.getDeclaredAnnotations();
	}

	@Nullable
	@Override
	public <A extends Annotation> A getAnnotation(final Class<A> annotationClass) {
		return sourceMethod.getAnnotation(annotationClass);
	}

	@Override
	public int getModifiers() {
		return sourceMethod.getModifiers();
	}

	@Override
	public boolean isSynthetic() {
		return sourceMethod.isSynthetic();
	}

	@Override
	public @NonNull Type getType() {
		return sourceMethod.getReturnType();
	}

	@Nullable
	@Override
	public Type getGenericType() {
		return sourceMethod.getGenericReturnType();
	}

	@Override
	public @NonNull String getName() {
		return sourceMethod.getName();
	}

	public NamedMap<ParameterWrapper> getParameters() {
		final Parameter[] parameters = sourceMethod.getParameters();
		final Map<String, ParameterWrapper> wrappers = new HashMap<>();
		for (int i = 0; i < parameters.length; i++) {
			wrappers.put(parameters[i].getName(), new ParameterWrapper(parameters[i], i));
		}
		return NamedMap.<ParameterWrapper>builder()
				.items(wrappers)
				.name(sourceMethod.getName() + "#fields")
				.build();
	}
}
