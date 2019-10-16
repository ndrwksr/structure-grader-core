package edu.kaiseran.structuregrader.specifications;

import com.google.common.collect.ImmutableList;
import edu.kaiseran.structuregrader.ClassCollection;
import edu.kaiseran.structuregrader.ClassCollection.CollectionVisitor;
import edu.kaiseran.structuregrader.ClassStructure;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specifications.ClassSpecSuite.ClassSpecSuiteFactory;
import lombok.*;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * ClassCollectionSpecSuite is a composite of two sets of visitors, those for the visited collection
 * itself (classCollectionVisitors) and those for the classes that the collection contains
 * (classSpecSuites). When ClassCollectionSpecSuite visits a ClassCollection, it applies all of its
 * visitors to said ClassCollection and its composing members.
 */
@Data
@Builder(access = AccessLevel.PRIVATE)
public class CollectionSpecSuite implements CollectionVisitor {

	/**
	 * Stores specs for the entire collection of classes, such as NoExtraClassesSpec at the top level,
	 * which would ensure that there are no extra top-level classes
	 */
	@NonNull
	private final List<CollectionVisitor> collectionVisitors;

	/**
	 * Stores spec suites for the individual classes within the collection
	 */
	@NonNull
	private final Map<String, ClassSpecSuite> classSpecSuites;

	@Override
	public void visit(@NonNull final ClassCollection classCollection) {
		collectionVisitors.forEach(spec -> spec.visit(classCollection));
	}

	@Override
	public void visit(@NonNull final ClassStructure classStructure) {
		visit(classStructure.getClassCollection());
	}

	/**
	 * Factory class for ClassSpecSuite. Has a list of CollectionVisitorFactories provided by default to populate
	 * collectionVisitors, and uses the default ClassVisitorFactory to populate classSpecSuites.
	 */
	public static class CollectionSpecSuiteFactory implements CollectionVisitorFactory<CollectionSpecSuite> {
		private static final CollectionVisitorFactory[] DEFAULT_VISITOR_FACTORIES = {
				new NoExtraClassesSpec.NoExtraClassesSpecFactory(),
				new NoMissingClassesSpec.NoMissingClassesSpecFactory()
		};

		/**
		 * A pre-made, default instance for consumers of CollectionSpecSuiteFactory to use.
		 */
		@Getter
		private static final CollectionSpecSuiteFactory defaultInst = new CollectionSpecSuiteFactory(
				DEFAULT_VISITOR_FACTORIES,
				ClassSpecSuiteFactory.getDefaultInst()
		);

		/**
		 * The ClassVisitorFactory instances that this factory uses to populate collectionVisitors with specs.
		 */
		private final ImmutableList<CollectionVisitorFactory> collectionVisitorFactories;

		/**
		 * The ClassSpecSuiteFactory used to populate classSpecSuites with specs.
		 */
		private final ClassSpecSuiteFactory classSpecSuiteFactory;

		/**
		 * Constructor which takes an array of CollectionVisitorFactory instances and a ClassSpecSuiteFactory which will
		 * be used to create specifications to fill the instances of CollectionSpecSuite that this class builds.
		 *
		 * @param collectionVisitorFactories The ClassVisitorFactory instances that this factory uses to populate
		 *                                   collectionVisitors with specs.
		 * @param classSpecSuiteFactory      The ClassSpecSuiteFactory used to populate classSpecSuites with specs.
		 */
		public CollectionSpecSuiteFactory(
				@NonNull final CollectionVisitorFactory[] collectionVisitorFactories,
				@NonNull final ClassSpecSuiteFactory classSpecSuiteFactory
		) {
			this.collectionVisitorFactories = collectionVisitorFactories == null || collectionVisitorFactories.length == 0 ?
					ImmutableList.copyOf(DEFAULT_VISITOR_FACTORIES) :
					ImmutableList.copyOf(collectionVisitorFactories);
			this.classSpecSuiteFactory = classSpecSuiteFactory == null ?
					ClassSpecSuiteFactory.getDefaultInst() :
					classSpecSuiteFactory;
		}

		@Override
		public CollectionSpecSuite buildVisitorFrom(
				@NonNull final ClassCollection classCollection,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final CollectionSpecSuiteBuilder builder = CollectionSpecSuite.builder();
			final Map<String, ClassStructure> declaredClasses = classCollection.getDeclaredClasses();

			// Make the default spec suites for each class in the collection
			final Map<String, ClassSpecSuite> classSpecSuites = declaredClasses.entrySet().stream()
					.map(entry -> new AbstractMap.SimpleEntry<>(
							entry.getKey(),
							classSpecSuiteFactory.buildVisitorFrom(entry.getValue(), noncomplianceConsumer)
					))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
			builder.classSpecSuites(classSpecSuites);

			// Make sure that there are no missing/extra classes
			final List<CollectionVisitor> collectionVisitors = collectionVisitorFactories.stream()
					.map(factory -> factory.buildVisitorFrom(classCollection, noncomplianceConsumer))
					.collect(Collectors.toList());

			builder.collectionVisitors(collectionVisitors);

			return builder.build();
		}

		@Override
		public CollectionSpecSuite buildVisitorFrom(
				@NonNull final ClassStructure classStructure,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return buildVisitorFrom(classStructure.getClassCollection(), noncomplianceConsumer);
		}
	}
}
