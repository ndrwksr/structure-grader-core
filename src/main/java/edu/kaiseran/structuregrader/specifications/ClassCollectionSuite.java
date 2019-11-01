package edu.kaiseran.structuregrader.specifications;

import com.google.common.collect.ImmutableList;
import edu.kaiseran.structuregrader.NamedCollection;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specifications.ClassSuite.ClassSuiteFactory;
import edu.kaiseran.structuregrader.visitors.ClassCollectionVisitor;
import edu.kaiseran.structuregrader.visitors.ClassCollectionVisitorFactory;
import edu.kaiseran.structuregrader.visitors.ClassVisitor;
import edu.kaiseran.structuregrader.visitors.CollectionVisitor;
import edu.kaiseran.structuregrader.wrappers.ClassWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ClassCollectionSuite extends CollectionSuite<ClassWrapper, ClassSuite> implements ClassCollectionVisitor, ClassVisitor {

	@Override
	public void visit(@Nullable final NamedCollection<ClassWrapper> collection) {
		this.collectionSpecs.forEach(spec -> spec.visit(collection));
	}

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		visit(classWrapper != null ? classWrapper.getClassCollection() : null);
	}

	/**
	 * Factory class for ClassSpecSuite. Has a list of CollectionVisitorFactories provided by default to populate
	 * collectionVisitors, and uses the default ClassVisitorFactory to populate classSpecSuites.
	 */
	public static class ClassCollectionSuiteFactory implements ClassCollectionVisitorFactory<ClassCollectionSuite> {
		private static ImmutableList<ClassCollectionVisitorFactory<? extends ClassCollectionVisitor>>
		getDefaultVisitorFactories() {
			return ImmutableList.of(
					NoExtraClassesSpec.NoExtraClassesSpecFactory.getDefaultInst(),
					NoMissingClassesSpec.NoMissingClassesSpecFactory.getDefaultInst()
			);
		}

		/**
		 * A pre-made, default instance for consumers of CollectionSpecSuiteFactory to use.
		 */
		@Getter
		private static final ClassCollectionSuiteFactory defaultInst = new ClassCollectionSuiteFactory(
				getDefaultVisitorFactories(),
				ClassSuiteFactory.getDefaultInst()
		);

		/**
		 * The ClassVisitorFactory instances that this factory uses to populate collectionVisitors with specs.
		 */
		private final ImmutableList<ClassCollectionVisitorFactory<? extends ClassCollectionVisitor>> collectionVisitorFactories;

		/**
		 * The ClassSpecSuiteFactory used to populate classSpecSuites with specs.
		 */
		private final ClassSuiteFactory classSpecSuiteFactory;

		/**
		 * Constructor which takes an array of CollectionVisitorFactory instances and a ClassSpecSuiteFactory which will
		 * be used to create specifications to fill the instances of CollectionSpecSuite that this class builds.
		 *
		 * @param collectionVisitorFactories The ClassVisitorFactory instances that this factory uses to populate
		 *                                   collectionVisitors with specs.
		 * @param classSpecSuiteFactory      The ClassSpecSuiteFactory used to populate classSpecSuites with specs.
		 */
		public ClassCollectionSuiteFactory(
				@NonNull final ImmutableList<ClassCollectionVisitorFactory<? extends ClassCollectionVisitor>> collectionVisitorFactories,
				@NonNull final ClassSuiteFactory classSpecSuiteFactory
		) {
			this.collectionVisitorFactories = collectionVisitorFactories == null || collectionVisitorFactories.isEmpty() ?
					getDefaultVisitorFactories() :
					ImmutableList.copyOf(collectionVisitorFactories);
			this.classSpecSuiteFactory = classSpecSuiteFactory == null ?
					ClassSuiteFactory.getDefaultInst() :
					classSpecSuiteFactory;
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
							classSpecSuiteFactory.buildFromItem(entry.getValue(), classCollection.getName(), noncomplianceConsumer)
					))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


			// Make sure that there are no missing/extra classes
			final List<CollectionVisitor<ClassWrapper>> collectionVisitors = collectionVisitorFactories.stream()
					.map(factory -> factory.buildFromCollection(classCollection, classCollection.getName(), noncomplianceConsumer))
					.collect(Collectors.toList());

			return ClassCollectionSuite.builder()
					.itemSuites(classSpecSuites)
					.collectionSpecs(collectionVisitors)
					.nameMapper(ClassWrapper::getName)
					.build();
		}
	}
}
