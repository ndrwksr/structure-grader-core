package com.github.ndrwksr.structuregrader.core.specification.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ndrwksr.structuregrader.core.HasChildSet;
import com.github.ndrwksr.structuregrader.core.NamedMap;
import com.github.ndrwksr.structuregrader.core.NamedSpecSet;
import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.specification.collection.NoMissingMapSpec;
import com.github.ndrwksr.structuregrader.core.visitor.ItemVisitor;
import com.github.ndrwksr.structuregrader.core.visitor.ItemVisitorFactory;
import com.github.ndrwksr.structuregrader.core.visitor.MapVisitorFactory;
import com.github.ndrwksr.structuregrader.core.wrapper.AnnotationWrapper;
import com.google.common.collect.ImmutableSet;
import com.github.ndrwksr.structuregrader.core.property.Annotated;
import com.github.ndrwksr.structuregrader.core.specification.base.MapSpec;
import com.github.ndrwksr.structuregrader.core.specification.collection.NoExtraMapSpec;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Checks for missing or extra annotations on an AnnotatedElement.
 *
 * @param <ITEM> The type of the AnnotatedElement being specified.
 */
@EqualsAndHashCode
@Data
public class AnnotatedSuite<ITEM extends Annotated> implements ItemVisitor<ITEM>, HasChildSet {
	/**
	 * Specifications for the collection of annotations on the AnnotatedElement.
	 */
	@NonNull
	private final Set<MapSpec<AnnotationWrapper, String>> collectionSpecs;

	/**
	 * The name of the parent of the specified element.
	 */
	@NonNull
	private final String parentName;

	/**
	 * @param collectionSpecs       The set of specifications for the map as a whole.
	 * @param parentName            The name of the parent of the specified object.
	 */
	@Builder
	@JsonCreator
	public AnnotatedSuite(
			@NonNull @JsonProperty("collectionSpecs") final Set<MapSpec<AnnotationWrapper, String>> collectionSpecs,
			@NonNull @JsonProperty("parentName") final String parentName
	) {
		this.collectionSpecs = collectionSpecs;
		this.parentName = parentName;
	}

	/**
	 * Extracts the Annotations from the provided Annotated item, maps said annotations into AnnotationWrappers, and
	 * visits those AnnotationWrappers with all specifications in collectionSpecs.
	 *
	 * @param annotated The specified Annotated item.
	 */
	@Override
	public void visit(@CheckForNull final ITEM annotated) {
		if (annotated != null) {
			final Annotation[] annotations = annotated.getDeclaredAnnotations();
			final NamedMap<AnnotationWrapper> namedAnnotations = AnnotationWrapper.buildCollectionFrom(
					parentName,
					annotations
			);

			collectionSpecs.forEach(spec -> spec.visit(namedAnnotations));
		}
	}

	@Override
	public NamedSpecSet getChildSet() {
		return NamedSpecSet.<MapSpec<AnnotationWrapper, String>>builder()
				.items(collectionSpecs)
				.name(".collectionSpecs")
				.build();
	}

	@Override
	public String getSpecDescription() {
		return "A specification for an element which can be annotated.";
	}

	/**
	 * Factory for AnnotatedSuites. Ensures that there are no missing/extra annotations.
	 *
	 * @param <ITEM> The type of the annotated element.
	 */
	public static class AnnotatedSuiteFactory<ITEM extends Annotated>
			implements ItemVisitorFactory<ITEM, ItemVisitor<ITEM>> {

		public static final String ITEM_TYPE_PLURAL = "annotations";
		private final ImmutableSet<MapVisitorFactory<AnnotationWrapper,
								? extends MapSpec<AnnotationWrapper, String>>> mapVisitorFactories;

		/**
		 * @param <ITEM> The type of the item for the annotated suites the returned factory will produce.
		 * @return a pre-configured instance for consumers of AnnotatedSuiteFactory to use.
		 */
		public static <ITEM extends Annotated> AnnotatedSuiteFactory<ITEM> getDefaultInst() {
			return new AnnotatedSuiteFactory<>(null);
		}

		/**
		 * @return a constant set of default MapVisitorFactories to be used to populate AnnotationSuite.collectionSpecs.
		 */
		private static ImmutableSet<MapVisitorFactory<AnnotationWrapper,
				? extends MapSpec<AnnotationWrapper, String>>> getDefaultMapVisitorFactories() {
			return ImmutableSet.of(
					NoExtraMapSpec.NoExtraSpecFactory.getDefaultInst(ITEM_TYPE_PLURAL),
					NoMissingMapSpec.NoMissingMapSpecFactory.getDefaultInst(ITEM_TYPE_PLURAL)
			);
		}

		/**
		 * @param mapVisitorFactories The set of factories to be used to populate AnnotationSuite.collectionSpecs. If null,
		 *                            the defaults (NoExtraMapSec & NoMissingMapSpec) will be used.
		 */
		public AnnotatedSuiteFactory(
				@Nullable final ImmutableSet<MapVisitorFactory<AnnotationWrapper,
						? extends MapSpec<AnnotationWrapper, String>>> mapVisitorFactories
		) {
			this.mapVisitorFactories = mapVisitorFactories != null ?
					mapVisitorFactories :
					getDefaultMapVisitorFactories();
		}

		@Override
		public AnnotatedSuite<ITEM> buildFromItem(
				@NonNull final ITEM annotatedElement,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Set<MapSpec<AnnotationWrapper, String>> collectionSpecs = mapVisitorFactories.stream()
					.<MapSpec<AnnotationWrapper, String>>map(factory -> factory.buildFromCollection(
							annotatedElement.getAnnotationWrappers(),
							parentName,
							noncomplianceConsumer
					))
					.collect(Collectors.toSet());

			return AnnotatedSuite.<ITEM>builder()
					.collectionSpecs(collectionSpecs)
					.parentName(parentName)
					.build();
		}
	}
}
