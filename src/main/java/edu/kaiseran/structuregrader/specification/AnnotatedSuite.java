package edu.kaiseran.structuregrader.specification;

import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.property.Annotated;
import edu.kaiseran.structuregrader.specification.quantity.NoExtraSpec;
import edu.kaiseran.structuregrader.specification.quantity.NoMissingSpec;
import edu.kaiseran.structuregrader.visitor.ItemVisitor;
import edu.kaiseran.structuregrader.visitor.ItemVisitorFactory;
import edu.kaiseran.structuregrader.wrapper.AnnotationWrapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;


/**
 * Checks for missing or extra annotations on an AnnotatedElement.
 *
 * @param <ITEM> The type of the AnnotatedElement being specified.
 */
@Builder
@EqualsAndHashCode
public class AnnotatedSuite<ITEM extends Annotated> implements ItemVisitor<ITEM> {
	/**
	 * Specifications for the collection of annotations on the AnnotatedElement.
	 */
	@NonNull
	private final Set<MapSpec<AnnotationWrapper, String>> collectionSpecs;

	/**
	 * The name of the parent of the specified element.
	 */
	@NonNull
	@Getter
	private final String parentName;

	/**
	 * Accepts any generated noncompliances, decoupling the consumption of noncompliances from their
	 * creation.
	 */
	@NonNull
	private final Consumer<Noncompliance> noncomplianceConsumer;

	/**
	 * Extracts the Annotations from the provided AnnotatedElement, maps said annotations into AnnotationWrappers, and
	 * visits those AnnotationWrappers with noMissingSpec and noExtraSpec.
	 *
	 * @param annotated The specified AnnotatedElement.
	 * @param parentName       The name of the AnnotatedElement, used to provide context in Noncompliances.
	 * @param collectionSpecs  The specifications for the collection of AnnotationWrappers on the AnnotatedElement.
	 */
	public static void visitAnnotatedElement(
			@CheckForNull final Annotated annotated,
			@NonNull final String parentName,
			@NonNull final Set<MapSpec<AnnotationWrapper, String>> collectionSpecs
	) {
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
	public void visit(@CheckForNull final ITEM annotatedElement) {
		visitAnnotatedElement(
				annotatedElement,
				parentName,
				collectionSpecs
		);
	}

	/**
	 * Factory for AnnotatedSuites. Ensures that there are no missing/extra annotations.
	 *
	 * @param <ITEM> The type of the annotated element.
	 */
	public static class AnnotatedSuiteFactory<ITEM extends Annotated>
			implements ItemVisitorFactory<ITEM, ItemVisitor<ITEM>> {

		public static <ITEM extends Annotated> AnnotatedSuiteFactory<ITEM> getDefaultInst() {
			return new AnnotatedSuiteFactory<>();
		}

		// TODO [ndrwksr | 11/17/19]: Relocate/consolidate?
		/**
		 * Builds and returns a NoMissingSpec which checks for all of the annotations present on AnnotatedElement.
		 *
		 * @param annotated      The annotated element whose annotations should be used as reference.
		 * @param parentName            The name of the parent of the annotated element.
		 * @param noncomplianceConsumer A Consumer for any noncompliances generated.
		 * @return a NoMissingSpec which checks for all of the annotations present on AnnotatedElement.
		 */
		public static NoMissingSpec<AnnotationWrapper> buildNoMissingSpecFromElement(
				@NonNull final Annotated annotated,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final NamedMap<AnnotationWrapper> namedAnnotations = AnnotationWrapper.buildCollectionFrom(
					parentName,
					annotated.getDeclaredAnnotations()
			);
			final Set<String> expectedNames = new HashSet<>(namedAnnotations.getItems().keySet());

			return NoMissingSpec.<AnnotationWrapper>builder()
					.expected(expectedNames)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}

		// TODO [ndrwksr | 11/17/19]: Relocate/consolidate?
		/**
		 * Builds and returns a NoExtraSpec which checks for all of the annotations present on AnnotatedElement.
		 *
		 * @param annotated      The annotated element whose annotations should be used as reference.
		 * @param parentName            The name of the parent of the annotated element.
		 * @param noncomplianceConsumer A Consumer for any noncompliances generated.
		 * @return a NoExtraSpec which checks for all of the annotations present on AnnotatedElement.
		 */
		public static NoExtraSpec<AnnotationWrapper> buildNoExtraSpecFromElement(
				@NonNull final Annotated annotated,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final NamedMap<AnnotationWrapper> namedAnnotations = AnnotationWrapper.buildCollectionFrom(
					parentName,
					annotated.getDeclaredAnnotations()
			);
			final Set<String> expectedNames = new HashSet<>(namedAnnotations.getItems().keySet());

			return NoExtraSpec.<AnnotationWrapper>builder()
					.expected(expectedNames)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}

		@Override
		public AnnotatedSuite<ITEM> buildFromItem(
				@NonNull final ITEM annotatedElement,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final NoMissingSpec<AnnotationWrapper> noMissingSpec = buildNoMissingSpecFromElement(
					annotatedElement,
					parentName,
					noncomplianceConsumer
			);

			final NoExtraSpec<AnnotationWrapper> noExtraSpec = buildNoExtraSpecFromElement(
					annotatedElement,
					parentName,
					noncomplianceConsumer
			);

			return AnnotatedSuite.<ITEM>builder()
					.collectionSpecs(new HashSet<>(Arrays.asList(noMissingSpec, noExtraSpec)))
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}
	}
}
