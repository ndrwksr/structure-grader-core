package edu.kaiseran.structuregrader.specifications;

import com.google.common.collect.ImmutableList;
import edu.kaiseran.structuregrader.NamedCollection;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specifications.ClassSuite.ClassSuiteFactory;
import edu.kaiseran.structuregrader.visitors.ClassCollectionVisitor;
import edu.kaiseran.structuregrader.visitors.ClassCollectionVisitorFactory;
import edu.kaiseran.structuregrader.visitors.ClassHierarchyVisitor;
import edu.kaiseran.structuregrader.visitors.CollectionVisitor;
import edu.kaiseran.structuregrader.wrappers.ClassWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Contains specifications for a hierarchy of classes, including checking for missing/extra classes and specifications
 * for each class at the specified level in the hierarchy.
 */
@SuperBuilder
public class ClassCollectionSuite extends CollectionSuite<ClassWrapper, ClassSuite> implements ClassHierarchyVisitor {

	@Override
	public void visit(@Nullable final NamedCollection<ClassWrapper> collection) {
		this.collectionSpecs.forEach(spec -> spec.visit(collection));
	}

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		visit(classWrapper != null ? classWrapper.getClassCollection() : null);
	}

	/**
	 * Factory class for ClassCollectionSuite. Has a list of CollectionVisitorFactories provided by default to populate
	 * collectionSpecs, and uses the default ClassVisitorFactory to populate itemSuites.
	 */
	public static class ClassCollectionSuiteFactory implements ClassCollectionVisitorFactory<ClassCollectionSuite> {
		/**
		 * @return an immutable list of the default ClassCollectionVisitorFactories.
		 */
		private static ImmutableList<ClassCollectionVisitorFactory<?>> getDefaultVisitorFactories() {
			return ImmutableList.of(
					new NoExtraClassesSpec.NoExtraClassesSpecFactory(),
					new NoMissingClassesSpec.NoMissingClassesSpecFactory()
			);
		}

		/**
		 * A pre-made, default instance for consumers of ClassCollectionVisitorFactory to use.
		 */
		@Getter
		private static final ClassCollectionSuiteFactory defaultInst = new ClassCollectionSuiteFactory(null, null);

		/**
		 * The ClassCollectionVisitorFactory instances that this factory uses to populate collectionVisitors with specs.
		 */
		@NonNull
		private final ImmutableList<ClassCollectionVisitorFactory<?>> collectionVisitorFactories;

		/**
		 * The ClassSuiteFactory used to populate classSpecSuites with specs.
		 */
		@NonNull
		private final ClassSuiteFactory classSuiteFactory;

		/**
		 * Constructor which takes an array of ClassCollectionVisitorFactories and a ClassSuiteFactory which will
		 * be used to create specifications to fill collectionSpecs and itemSuites.
		 *
		 * @param collectionVisitorFactories The ClassVisitorFactory instances that this factory uses to populate
		 *                                   collectionVisitors with specs.
		 * @param classSuiteFactory          The ClassSpecSuiteFactory used to populate classSpecSuites with specs.
		 */
		public ClassCollectionSuiteFactory(
				@CheckForNull final ImmutableList<ClassCollectionVisitorFactory<?>> collectionVisitorFactories,
				@CheckForNull final ClassSuiteFactory classSuiteFactory
		) {
			this.collectionVisitorFactories = collectionVisitorFactories == null || collectionVisitorFactories.isEmpty() ?
					getDefaultVisitorFactories() :
					ImmutableList.copyOf(collectionVisitorFactories);
			this.classSuiteFactory = classSuiteFactory == null ?
					ClassSuiteFactory.getDefaultInst(this) :
					classSuiteFactory;
		}

		@Override
		public ClassCollectionSuite buildFromCollection(
				@NonNull final NamedCollection<ClassWrapper> classCollection,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Map<String, ClassWrapper> declaredClasses = classCollection.getItems();

			// Make the default spec suites for each class in the collection
			final Map<String, ClassSuite> classSpecSuites = declaredClasses.entrySet().stream()
					.map(entry -> new AbstractMap.SimpleEntry<>(
							entry.getKey(),
							classSuiteFactory.buildFromItem(entry.getValue(), classCollection.getName(), noncomplianceConsumer)
					))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


			// Make sure that there are no missing/extra classes
			final List<CollectionVisitor<ClassWrapper>> collectionVisitors = collectionVisitorFactories.stream()
					.map(factory -> factory.buildFromCollection(classCollection, classCollection.getName(), noncomplianceConsumer))
					.collect(Collectors.toList());

			final ClassCollectionSuiteBuilder classCollectionSuiteBuilder = ClassCollectionSuite.builder()
					.itemSuites(classSpecSuites)
					.collectionSpecs(collectionVisitors)
					.nameMapper(ClassWrapper::getName)
					.parentName(parentName);

			return classCollectionSuiteBuilder.build();
		}
	}
}
