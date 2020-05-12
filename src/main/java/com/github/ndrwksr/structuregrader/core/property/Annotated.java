package com.github.ndrwksr.structuregrader.core.property;

import com.github.ndrwksr.structuregrader.core.NamedMap;
import com.github.ndrwksr.structuregrader.core.wrapper.AnnotationWrapper;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Interface for wrappers to provide the annotations on their source elements. Related to AnnotatedElement.
 */
public interface Annotated extends Named {
	/**
	 * @return an array of the annotations on the source element.
	 */
	@NonNull
	Annotation[] getDeclaredAnnotations();

	/**
	 * Searches for an annotation of the provided type/class on the source element.
	 *
	 * @param annotationClass The class of the annotation to search for.
	 * @param <A>             The type of the annotation to search for.
	 * @return the annotation of that type, or null if no such annotation is present.
	 */
	@Nullable
	<A extends Annotation> A getAnnotation(final Class<A> annotationClass);

	@NonNull
	default NamedMap<AnnotationWrapper> getAnnotationWrappers() {
		return NamedMap.<AnnotationWrapper>builder()
				.items(Arrays.stream(getDeclaredAnnotations())
						.map(AnnotationWrapper::new)
						.collect(Collectors.toMap(AnnotationWrapper::getName, Function.identity()))
				)
				.name(this.getName())
				.build();
	}
}
