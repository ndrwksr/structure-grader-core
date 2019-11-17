package edu.kaiseran.structuregrader.wrapper;

import edu.kaiseran.structuregrader.property.Named;
import edu.kaiseran.structuregrader.NamedMap;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A data type which represents an Annotation and implements Named.
 */
@Data
@Builder
public class AnnotationWrapper implements Named {
	/**
	 * The annotation this wrapper represents.
	 */
	@NonNull
	final Annotation annotation;

	@Override
	@NonNull
	public String getName() {
		return annotation.annotationType().getSimpleName();
	}

	/**
	 * Builds and returns a new AnnotationWrapper from a source Annotation.
	 *
	 * @param annotation The source annotation to build a wrapper from.
	 * @return a new AnnotationWrapper from a source Annotation.
	 */
	@NonNull
	public static AnnotationWrapper buildFrom(
			@NonNull final Annotation annotation
	) {
		return AnnotationWrapper.builder().annotation(annotation).build();
	}

	/**
	 * Builds and returns a new collection of AnnotationWrappers from an array of Annotations.
	 *
	 * @param parentName  The name of the parent of the annotations, such as the name of the AnnotatedElement
	 *                    they came from.
	 * @param annotations The array of Annotations to build the collection from.
	 * @return a new collection of AnnotationWrappers from an array of Annotations.
	 */
	@NonNull
	public static NamedMap<AnnotationWrapper> buildCollectionFrom(
			@NonNull final String parentName,
			@NonNull final Annotation[] annotations
	) {
		final Map<String, AnnotationWrapper> annotationWrappers = Stream.of(annotations)
				.map(AnnotationWrapper::buildFrom)
				.collect(Collectors.toMap(AnnotationWrapper::getName, wrapper -> wrapper));
		return NamedMap.<AnnotationWrapper>builder()
				.items(annotationWrappers)
				.name(parentName + " annotations")
				.build();
	}
}
