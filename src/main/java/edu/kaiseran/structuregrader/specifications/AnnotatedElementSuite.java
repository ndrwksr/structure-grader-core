package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.NamedCollection;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.visitors.ItemVisitor;
import edu.kaiseran.structuregrader.visitors.ItemVisitorFactory;
import edu.kaiseran.structuregrader.wrappers.AnnotationWrapper;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;


/**
 * Checks for missing or extra annotations on an AnnotatedElement.
 *
 * @param <ITEM> The type of the AnnotatedElement being specified.
 */
@Builder
public class AnnotatedElementSuite<ITEM extends AnnotatedElement> implements ItemVisitor<ITEM> {
	/**
	 * Checks for any missing AnnotationWrapper instances on the class.
	 */
	@Nullable
	private final NoMissingSpec<AnnotationWrapper> noMissingSpec;

	/**
	 * Checks for any extra AnnotationWrapper instances on the class.
	 */
	@Nullable
	private final NoExtraSpec<AnnotationWrapper> noExtraSpec;

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
	 * @param annotatedElement The specified AnnotatedElement.
	 * @param parentName       The name of the AnnotatedElement, used to provide context in Noncompliances.
	 * @param noMissingSpec    The NoMissingSpec to apply to annotatedElement's annotations.
	 * @param noExtraSpec      The NoExtraSpec to apply to annotatedElement's annotations.
	 */
	static void visitAnnotatedElement(
			@CheckForNull final AnnotatedElement annotatedElement,
			@NonNull final String parentName,
			@NonNull final NoMissingSpec<AnnotationWrapper> noMissingSpec,
			@NonNull final NoExtraSpec<AnnotationWrapper> noExtraSpec
	) {
		if (annotatedElement != null) {
			final Annotation[] annotations = annotatedElement.getDeclaredAnnotations();
			final NamedCollection<AnnotationWrapper> namedAnnotations = AnnotationWrapper.buildCollectionFrom(
					parentName,
					annotations
			);

			if (noMissingSpec != null) {
				noMissingSpec.visit(namedAnnotations);
			}
			if (noExtraSpec != null) {
				noExtraSpec.visit(namedAnnotations);
			}
		}
	}

	@Override
	public void visit(@CheckForNull final ITEM annotatedElement) {
		visitAnnotatedElement(
				annotatedElement,
				parentName,
				noMissingSpec,
				noExtraSpec
		);
	}

	/**
	 * Factory for AnnotatedElementSuites. Ensures that there are no missing/extra annotations.
	 *
	 * @param <ITEM> The type of the annotated element.
	 */
	public static class AnnotatedElementSuiteFactory<ITEM extends AnnotatedElement>
			implements ItemVisitorFactory<ITEM, ItemVisitor<ITEM>> {

		public static <ITEM extends AnnotatedElement> AnnotatedElementSuiteFactory<ITEM> getDefaultInst() {
			return new AnnotatedElementSuiteFactory<>();
		}

		/**
		 * Builds and returns a NoMissingSpec which checks for all of the annotations present on AnnotatedElement.
		 *
		 * @param annotatedElement      The annotated element whose annotations should be used as reference.
		 * @param parentName            The name of the parent of the annotated element.
		 * @param noncomplianceConsumer A Consumer for any noncompliances generated.
		 * @return a NoMissingSpec which checks for all of the annotations present on AnnotatedElement.
		 */
		static NoMissingSpec<AnnotationWrapper> buildNoMissingSpecFromElement(
				@NonNull final AnnotatedElement annotatedElement,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final NamedCollection<AnnotationWrapper> namedAnnotations = AnnotationWrapper.buildCollectionFrom(
					parentName,
					annotatedElement.getDeclaredAnnotations()
			);
			final Set<String> expectedNames = new HashSet<>(namedAnnotations.getItems().keySet());

			return NoMissingSpec.<AnnotationWrapper>builder()
					.expected(expectedNames)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}

		/**
		 * Builds and returns a NoExtraSpec which checks for all of the annotations present on AnnotatedElement.
		 *
		 * @param annotatedElement      The annotated element whose annotations should be used as reference.
		 * @param parentName            The name of the parent of the annotated element.
		 * @param noncomplianceConsumer A Consumer for any noncompliances generated.
		 * @return a NoExtraSpec which checks for all of the annotations present on AnnotatedElement.
		 */
		static NoExtraSpec<AnnotationWrapper> buildNoExtraSpecFromElement(
				@NonNull final AnnotatedElement annotatedElement,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final NamedCollection<AnnotationWrapper> namedAnnotations = AnnotationWrapper.buildCollectionFrom(
					parentName,
					annotatedElement.getDeclaredAnnotations()
			);
			final Set<String> expectedNames = new HashSet<>(namedAnnotations.getItems().keySet());

			return NoExtraSpec.<AnnotationWrapper>builder()
					.expected(expectedNames)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}

		@Override
		public AnnotatedElementSuite<ITEM> buildFromItem(
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

			return AnnotatedElementSuite.<ITEM>builder()
					.noMissingSpec(noMissingSpec)
					.noExtraSpec(noExtraSpec)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}
	}
}
