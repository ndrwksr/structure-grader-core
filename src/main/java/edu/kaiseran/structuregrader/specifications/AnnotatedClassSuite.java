package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specifications.AnnotatedElementSuite.AnnotatedElementSuiteFactory;
import edu.kaiseran.structuregrader.visitors.ClassVisitor;
import edu.kaiseran.structuregrader.visitors.ClassVisitorFactory;
import edu.kaiseran.structuregrader.wrappers.AnnotationWrapper;
import edu.kaiseran.structuregrader.wrappers.ClassWrapper;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.function.Consumer;


/**
 * Checks for missing and extra annotations on a class.
 */
@Data
@Builder
public class AnnotatedClassSuite implements ClassVisitor {
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
	 * The name of the class this suite specifies.
	 */
	@NonNull
	private final String parentName;

	/**
	 * Accepts any generated noncompliances, decoupling the consumption of noncompliances from their
	 * creation.
	 */
	@NonNull
	private final Consumer<Noncompliance> noncomplianceConsumer;


	@Override
	public void visit(@CheckForNull final ClassWrapper classWrapper) {
		AnnotatedElementSuite.visitAnnotatedElement(
				classWrapper,
				parentName,
				noMissingSpec,
				noExtraSpec
		);
	}

	/**
	 * Creates AnnotatedClassSuites which expect an exact match in the annotations present on the specified ClassWrapper
	 * and the visited ClassWrapper.
	 */
	public static class AnnotatedClassSuiteFactory implements ClassVisitorFactory<AnnotatedClassSuite> {
		@Override
		public AnnotatedClassSuite buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			// Build the NoMissingSpec
			final NoMissingSpec<AnnotationWrapper> noMissingSpec = AnnotatedElementSuiteFactory.buildNoMissingSpecFromElement(
					classWrapper,
					parentName,
					noncomplianceConsumer
			);

			// Build the NoExtraSpec
			final NoExtraSpec<AnnotationWrapper> noExtraSpec = AnnotatedElementSuiteFactory.buildNoExtraSpecFromElement(
					classWrapper,
					parentName,
					noncomplianceConsumer
			);

			// Build and return the AnnotatedClassSuite
			return AnnotatedClassSuite.builder()
					.noMissingSpec(noMissingSpec)
					.noExtraSpec(noExtraSpec)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}
	}
}
