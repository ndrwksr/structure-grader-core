package edu.kaiseran.structuregrader.specifications;

import com.google.common.collect.ImmutableList;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specifications.ClassCollectionSuite.ClassCollectionSuiteFactory;
import edu.kaiseran.structuregrader.visitors.ClassVisitor;
import edu.kaiseran.structuregrader.visitors.ClassVisitorFactory;
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

/**
 * Contains specifications for a single class (the "specified" class), optionally including a ClassCollectionSuite to
 * test said class's inner classes.
 */
@Builder
public class ClassSuite implements ClassVisitor {
	/**
	 * The specifications and suites to visit the specified class.
	 */
	@NonNull
	private final List<ClassVisitor> specifiedClassVisitors;

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		specifiedClassVisitors.forEach(visitor -> visitor.visit(classWrapper));
	}

	/**
	 * Factory class for ClassSuite. Has a list of ClassVisitorFactories provided by default to populate
	 * specifiedClassVisitors, and uses the default CollectionVisitorFactory to create collectionSpecSuite.
	 */
	public static class ClassSuiteFactory implements ClassVisitorFactory<ClassSuite> {

		private ClassCollectionSuiteFactory classCollectionSuiteFactory;

		/**
		 * The ClassVisitorFactories provided by default for populating declaringClassSpecs from classStructure.
		 */
		private static ImmutableList<ClassVisitorFactory<?>> getDefaultVisitorFactories() {
			return ImmutableList.of(
					new NoExtraClassesSpec.NoExtraClassesSpecFactory(),
					new NoMissingClassesSpec.NoMissingClassesSpecFactory(),
					new SuperclassSpec.SuperclassSpecFactory(),
					new AnnotatedClassSuite.AnnotatedClassSuiteFactory()
			);
		}

		/**
		 * @return a pre-made, default instance for consumers of ClassSpecSuiteFactory to use.
		 */
		public static ClassSuiteFactory getDefaultInst(
				@NonNull final ClassCollectionSuiteFactory classCollectionSuiteFactory
		) {
			return new ClassSuiteFactory(null, classCollectionSuiteFactory);
		}

		/**
		 * The ClassVisitorFactories that this factory uses to populate declaringClassSpecs with specs.
		 */
		private final ImmutableList<ClassVisitorFactory<?>> classVisitorFactories;

		/**
		 * @param classVisitorFactories The ClassVisitorFactory instances this factory will use to populate
		 *                              specifiedClassVisitors with specs and suites.
		 */
		public ClassSuiteFactory(
				@CheckForNull final ImmutableList<ClassVisitorFactory<?>> classVisitorFactories,
				@CheckForNull final ClassCollectionSuiteFactory classCollectionSuiteFactory
		) {
			this.classVisitorFactories = classVisitorFactories == null || classVisitorFactories.isEmpty() ?
					ImmutableList.copyOf(getDefaultVisitorFactories()) :
					ImmutableList.copyOf(classVisitorFactories);
			this.classCollectionSuiteFactory = classCollectionSuiteFactory == null ?
					ClassCollectionSuiteFactory.getDefaultInst() :
					classCollectionSuiteFactory;
		}

		@Override
		public ClassSuite buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			// Make the default specs for the visited class
			final List<ClassVisitor> specifiedClassVisitors = classVisitorFactories.stream()
					.map(factory -> (ClassVisitor) factory.buildFromItem(classWrapper, parentName, noncomplianceConsumer))
					.collect(Collectors.toList());


			// Make the default spec suite for the declared classes of the visited class
			final ClassCollectionSuite classCollectionSuite = classCollectionSuiteFactory.buildFromCollection(
					classWrapper.getClassCollection(),
					classWrapper.getName(),
					noncomplianceConsumer
			);

			specifiedClassVisitors.add(classCollectionSuite);

			return ClassSuite.builder().specifiedClassVisitors(specifiedClassVisitors).build();
		}
	}
}
