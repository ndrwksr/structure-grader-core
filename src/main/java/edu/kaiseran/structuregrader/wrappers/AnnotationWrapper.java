package edu.kaiseran.structuregrader.wrappers;

import edu.kaiseran.structuregrader.Named;
import edu.kaiseran.structuregrader.NamedCollection;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
public class AnnotationWrapper implements Named {
	@NonNull
	final Annotation annotation;

	@Override
	@NonNull
	public String getName() {
		return annotation.annotationType().getSimpleName();
	}

	@NonNull
	public static AnnotationWrapper buildFrom(
			@NonNull final Annotation annotation
	) {
		return AnnotationWrapper.builder().annotation(annotation).build();
	}

	@NonNull
	public static NamedCollection<AnnotationWrapper> buildCollectionFrom(
			@NonNull final String parentName,
			@NonNull final Annotation[] annotations
	) {
		final Map<String, AnnotationWrapper> annotationWrappers = Stream.of(annotations)
				.map(AnnotationWrapper::buildFrom)
				.collect(Collectors.toMap(AnnotationWrapper::getName, wrapper -> wrapper));
		return NamedCollection.<AnnotationWrapper>builder()
				.items(annotationWrappers)
				.name(parentName + " annotations")
				.build();
	}
}
