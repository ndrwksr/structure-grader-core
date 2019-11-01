package edu.kaiseran.structuregrader.specifications;

import com.google.common.collect.ImmutableList;
import edu.kaiseran.structuregrader.*;
import edu.kaiseran.structuregrader.visitors.*;
import edu.kaiseran.structuregrader.wrappers.ClassWrapper;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Data
@Builder
public class ClassSuite implements ClassVisitor {
	@NonNull
	private final List<ClassVisitor> declaringClassVisitors;

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		declaringClassVisitors.forEach(visitor -> visitor.visit(classWrapper));
	}

	/**
	 * Factory class for ClassSpecSuite. Has a list of ClassVisitorFactories provided by default to populate
	 * declaringClassSpecs, and uses the default CollectionVisitorFactory to create collectionSpecSuite.
	 */
	public static class ClassSuiteFactory implements ClassVisitorFactory<ClassSuite> {
		/**
		 * The ClassVisitorFactories provided by default for populating declaringClassSpecs from classStructure.
		 */
		private static ImmutableList<ClassVisitorFactory<? extends ClassVisitor>> getDefaultVisitorFactories() {
			return ImmutableList.of(
					new NoExtraClassesSpec.NoExtraClassesSpecFactory(),
					new NoMissingClassesSpec.NoMissingClassesSpecFactory(),
					new SuperclassSpec.SuperclassSpecFactory(),
					new AnnotatedClassSuite.AnnotatedClassSuiteFactory()
			);
		}

		/**
		 * A pre-made, default instance for consumers of ClassSpecSuiteFactory to use.
		 */
		@Getter
		private static final ClassSuiteFactory defaultInst = new ClassSuiteFactory(getDefaultVisitorFactories());

		/**
		 * The ClassVisitorFactory instances that this factory uses to populate declaringClassSpecs with specs.
		 */
		private final ImmutableList<ClassVisitorFactory<? extends ClassVisitor>> classVisitorFactories;

		/**
		 * @param classVisitorFactories The ClassVisitorFactory instances this factory will use to populate
		 *                              declaringClassSpecs with specs.
		 */
		public ClassSuiteFactory(
				@CheckForNull final ImmutableList<ClassVisitorFactory<? extends ClassVisitor>> classVisitorFactories
		) {
			this.classVisitorFactories = classVisitorFactories == null || classVisitorFactories.isEmpty() ?
					ImmutableList.copyOf(getDefaultVisitorFactories()) :
					ImmutableList.copyOf(classVisitorFactories);
		}

		/**
		 * Returns a new ClassSpecSuite from the provided ClassStructure and with the provided Noncompliance consumer.
		 * Uses classVisitorFactories to make specs for classStructure.
		 *
		 * @param classWrapper        The ClassStructure to base the resulting ClassSpecSuite off of.
		 * @param parentName
		 * @param noncomplianceConsumer The Noncompliance consumer to pass to all created ClassSpecSuites.
		 * @return a new ClassSpecSuite from the provided ClassStructure and with the provided Noncompliance consumer.
		 */
		@Override
		public ClassSuite buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final ClassSuiteBuilder builder = ClassSuite.builder();

			// Make the default specs for the visited class
			final List<ClassVisitor> declaringClassVisitors = classVisitorFactories.stream()
					.map(factory -> (ClassVisitor) factory.buildFromItem(classWrapper, parentName, noncomplianceConsumer))
					.collect(Collectors.toList());

			final ClassCollectionSuite.ClassCollectionSuiteFactory classCollectionSuiteFactory =
					ClassCollectionSuite.ClassCollectionSuiteFactory.getDefaultInst();

			// Make the default spec suite for the declared classes of the visited class
			final ClassCollectionSuite classCollectionSuite = classCollectionSuiteFactory.buildFromCollection(
					classWrapper.getClassCollection(),
					classWrapper.getName(),
					noncomplianceConsumer
			);

			declaringClassVisitors.add(classCollectionSuite);

			builder.declaringClassVisitors(declaringClassVisitors);

			return builder.build();
		}
	}
}
