package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.NamedCollection;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.visitors.ClassVisitor;
import edu.kaiseran.structuregrader.visitors.ClassVisitorFactory;
import edu.kaiseran.structuregrader.wrappers.AnnotationWrapper;
import edu.kaiseran.structuregrader.wrappers.ClassWrapper;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static edu.kaiseran.structuregrader.specifications.AnnotatedElementSuite.*;

@Data
@Builder
public class AnnotatedClassSuite implements ClassVisitor {
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


	@Override
	public void visit(@CheckForNull final ClassWrapper classWrapper) {
		visitAnnotatedElement(
				classWrapper,
				parentName,
				noncomplianceConsumer,
				noMissingSpec,
				noExtraSpec
		);
	}

	public static class AnnotatedClassSuiteFactory implements ClassVisitorFactory<AnnotatedClassSuite> {
		@Override
		public AnnotatedClassSuite buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final NoMissingSpec<AnnotationWrapper> noMissingSpec = AnnotatedElementSuiteFactory.buildNoMissingSpecFromElement(
					classWrapper,
					parentName,
					noncomplianceConsumer
			);

			final NoExtraSpec<AnnotationWrapper> noExtraSpec = AnnotatedElementSuiteFactory.buildNoExtraSpecFromElement(
					classWrapper,
					parentName,
					noncomplianceConsumer
			);

			return AnnotatedClassSuite.builder()
					.noMissingSpec(noMissingSpec)
					.noExtraSpec(noExtraSpec)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}
	}
}
