package edu.kaiseran.structuregrader.specification.clazz;

import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specification.AnnotatedSuite;
import edu.kaiseran.structuregrader.specification.AnnotatedSuite.AnnotatedSuiteFactory;
import edu.kaiseran.structuregrader.specification.MapSpec;
import edu.kaiseran.structuregrader.visitor.ClassVisitor;
import edu.kaiseran.structuregrader.visitor.ClassVisitorFactory;
import edu.kaiseran.structuregrader.wrapper.AnnotationWrapper;
import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import lombok.*;

import javax.annotation.CheckForNull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

// TODO [ndrwksr | 11/17/19]: Take another pass at combining w/ AnnotatedSuite (should be easier now that the ITEM
//  interface extends Annotated and not AnnotatedElement.
/**
 * Checks for missing and extra annotations on a class.
 */
@Data
@Builder
@EqualsAndHashCode
public class AnnotatedClassSuite implements ClassVisitor {
	/**
	 * The specifications to check for missing/extra annotations.
	 */
	@NonNull
	private final Set<MapSpec<AnnotationWrapper, String>> mapSpecs;

	/**
	 * The name of the parent of the specified element.
	 */
	@NonNull
	@Getter
	private final String parentName;

	@Override
	public void visit(@CheckForNull final ClassWrapper classWrapper) {
		AnnotatedSuite.visitAnnotatedElement(
				classWrapper,
				parentName,
				mapSpecs
		);
	}

	/**
	 * Creates AnnotatedClassSuites which expect an exact match in the annotations present on the specified ClassWrapper
	 * and the visited ClassWrapper.
	 */
	@AllArgsConstructor
	public static class AnnotatedClassSuiteFactory implements ClassVisitorFactory<AnnotatedClassSuite> {
		/**
		 * True if the product specifications should check for missing annotations.
		 */
		private boolean checkForMissing;

		/**
		 * True if the product specifications should check for extra annotations.
		 */
		private boolean checkForExtra;

		/**
		 * @return a pre-configured instance for consumers of AnnotatedClassSuiteFactory to use.
		 */
		public static AnnotatedClassSuiteFactory getDefaultInst() {
			return new AnnotatedClassSuiteFactory(
					true,
					true
			);
		}

		@Override
		public AnnotatedClassSuite buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Set<MapSpec<AnnotationWrapper, String>> mapSpecs = new HashSet<>();

			// Build the NoMissingSpec
			if (checkForMissing) {
				mapSpecs.add(AnnotatedSuiteFactory.buildNoMissingSpecFromElement(
						classWrapper,
						parentName,
						noncomplianceConsumer
				));
			}

			// Build the NoExtraSpec
			if (checkForExtra) {
				mapSpecs.add(AnnotatedSuiteFactory.buildNoExtraSpecFromElement(
						classWrapper,
						parentName,
						noncomplianceConsumer
				));
			}

			// Build and return the AnnotatedClassSuite
			return AnnotatedClassSuite.builder()
					.mapSpecs(mapSpecs)
					.parentName(parentName)
					.build();
		}
	}
}
