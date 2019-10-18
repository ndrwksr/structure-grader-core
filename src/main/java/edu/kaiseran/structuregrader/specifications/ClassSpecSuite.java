package edu.kaiseran.structuregrader.specifications;

import com.google.common.collect.ImmutableList;
import edu.kaiseran.structuregrader.ClassStructure;
import edu.kaiseran.structuregrader.ClassStructure.ClassVisitor;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specifications.CollectionSpecSuite.CollectionSpecSuiteFactory;
import lombok.*;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * ClassSpecSuite is a composite of a set of specifications for a class, and a
 * ClassCollectionSpecSuite for said class's inner classes. When a ClassSpecSuite visits a class, it
 * applies all of its class visitors to said class, and applies its ClassCollectionSpecSuite to the
 * class's inner classes.
 */
@Data
@Builder(access = AccessLevel.PRIVATE)
public class ClassSpecSuite implements ClassVisitor {

	/**
	 * Stores specs for the class being visited.
	 */
	@NonNull
	private final List<ClassVisitor> declaringClassSpecs;

	/**
	 * Stores specs for the visited class's declared classes.
	 */
	@NonNull
	private final CollectionSpecSuite collectionSpecSuite;

	@Override
	public void visitClass(@NonNull final ClassStructure classStructure) {
		declaringClassSpecs.forEach(spec -> spec.visitClass(classStructure));
		collectionSpecSuite.visitCollection(classStructure.getClassCollection());
	}

	/**
	 * Factory class for ClassSpecSuite. Has a list of ClassVisitorFactories provided by default to populate
	 * declaringClassSpecs, and uses the default CollectionVisitorFactory to create collectionSpecSuite.
	 */
	public static class ClassSpecSuiteFactory implements ClassVisitorFactory<ClassSpecSuite> {
		/**
		 * The ClassVisitorFactories provided by default for populating declaringClassSpecs from classStructure.
		 */
		private static final ClassVisitorFactory[] DEFAULT_VISITOR_FACTORIES = {
				new NoExtraClassesSpec.NoExtraClassesSpecFactory(),
				new NoMissingClassesSpec.NoMissingClassesSpecFactory(),
				new SuperclassSpec.SuperclassSpecFactory()
		};

		/**
		 * A pre-made, default instance for consumers of ClassSpecSuiteFactory to use.
		 */
		@Getter
		private static final ClassSpecSuiteFactory defaultInst = new ClassSpecSuiteFactory(DEFAULT_VISITOR_FACTORIES);

		/**
		 * The ClassVisitorFactory instances that this factory uses to populate declaringClassSpecs with specs.
		 */
		private final ImmutableList<ClassVisitorFactory> classVisitorFactories;

		/**
		 * @param classVisitorFactories The ClassVisitorFactory instances this factory will use to populate
		 *                              declaringClassSpecs with specs.
		 */
		public ClassSpecSuiteFactory(@NonNull final ClassVisitorFactory[] classVisitorFactories) {
			this.classVisitorFactories = classVisitorFactories == null || classVisitorFactories.length == 0 ?
					ImmutableList.copyOf(DEFAULT_VISITOR_FACTORIES) :
					ImmutableList.copyOf(classVisitorFactories);
		}

		/**
		 * Returns a new ClassSpecSuite from the provided ClassStructure and with the provided Noncompliance consumer.
		 * Uses classVisitorFactories to make specs for classStructure.
		 *
		 * @param classStructure        The ClassStructure to base the resulting ClassSpecSuite off of.
		 * @param noncomplianceConsumer The Noncompliance consumer to pass to all created ClassSpecSuites.
		 * @return a new ClassSpecSuite from the provided ClassStructure and with the provided Noncompliance consumer.
		 */
		@Override
		public ClassSpecSuite buildFromClass(
				@NonNull final ClassStructure classStructure,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final ClassSpecSuiteBuilder builder = ClassSpecSuite.builder();

			// Make the default specs for the visited class
			final List<ClassVisitor> visitedClassSpecs = classVisitorFactories.stream()
					.map(factory -> factory.buildFromClass(classStructure, noncomplianceConsumer))
					.collect(Collectors.toList());
			builder.declaringClassSpecs(visitedClassSpecs);

			final CollectionSpecSuiteFactory collectionSpecSuiteFactory = CollectionSpecSuiteFactory.getDefaultInst();
			// Make the default spec suite for the declared classes of the visited class
			final CollectionSpecSuite collectionSpecSuite = collectionSpecSuiteFactory.buildFromClass(
					classStructure,
					noncomplianceConsumer
			);
			builder.collectionSpecSuite(collectionSpecSuite);

			return builder.build();
		}
	}
}
