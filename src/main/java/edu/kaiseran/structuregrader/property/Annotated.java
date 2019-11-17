package edu.kaiseran.structuregrader.property;

import lombok.NonNull;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * Interface for wrappers to provide the annotations on their source elements. This interface is related to
 * AnnotatedElement.
 */
public interface Annotated extends Named {
	/**
	 * @return an array of the annotations on the source element
	 */
	@NonNull
	Annotation[] getDeclaredAnnotations();

	/**
	 * Searches for an annotation of the provided type/class on the source element
	 *
	 * @param annotationClass The class of the annotation to search for
	 * @param <A>             The type of the annotation to search for
	 * @return the annotation of that type, or null if no such annotation is present.
	 */
	@Nullable
	<A extends Annotation> A getAnnotation(final Class<A> annotationClass);
}
