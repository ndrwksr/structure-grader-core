package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.Named;
import edu.kaiseran.structuregrader.NamedCollection;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.visitors.ClassVisitor;
import edu.kaiseran.structuregrader.visitors.ClassVisitorFactory;
import edu.kaiseran.structuregrader.visitors.ItemVisitor;
import edu.kaiseran.structuregrader.visitors.ItemVisitorFactory;
import edu.kaiseran.structuregrader.wrappers.AnnotationWrapper;
import edu.kaiseran.structuregrader.wrappers.ClassWrapper;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Data
@Builder
public class AnnotatedElementSuite<ITEM extends AnnotatedElement> implements ItemVisitor<ITEM> {
	@Nullable
	private final NoMissingSpec<AnnotationWrapper> noMissingSpec;
	@Nullable
	private final NoExtraSpec<AnnotationWrapper> noExtraSpec;

	@NonNull
	private final String parentName;

	/**
	 * Accepts any generated noncompliances, decoupling the consumption of noncompliances from their
	 * creation.
	 */
	@NonNull
	private final Consumer<Noncompliance> noncomplianceConsumer;
	
	static void visitAnnotatedElement(
			@CheckForNull final AnnotatedElement annotatedElement,
			@NonNull final String parentName,
			@NonNull final Consumer<Noncompliance> noncomplianceConsumer,
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
				noncomplianceConsumer, 
				noMissingSpec, 
				noExtraSpec
		);
	}

	public static class AnnotatedElementSuiteFactory<ITEM extends AnnotatedElement>
			implements ItemVisitorFactory<ITEM, ItemVisitor<ITEM>> {

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
