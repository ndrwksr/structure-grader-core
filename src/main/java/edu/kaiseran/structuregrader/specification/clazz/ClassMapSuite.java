package edu.kaiseran.structuregrader.specification.clazz;

import com.google.common.collect.ImmutableList;
import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specification.MapSuite;
import edu.kaiseran.structuregrader.specification.clazz.ClassSuite.ClassSuiteFactory;
import edu.kaiseran.structuregrader.specification.quantity.NoExtraClassesSpec;
import edu.kaiseran.structuregrader.specification.quantity.NoMissingClassesSpec;
import edu.kaiseran.structuregrader.visitor.ClassHierarchyVisitor;
import edu.kaiseran.structuregrader.visitor.ClassMapVisitorFactory;
import edu.kaiseran.structuregrader.visitor.MapVisitor;
import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Contains specifications for a hierarchy of classes, including checking for missing/extra classes and specifications
 * for each class at the specified level in the hierarchy.
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ClassMapSuite extends MapSuite<ClassWrapper, ClassSuite> implements ClassHierarchyVisitor {

	@Override
	public void visit(@Nullable final NamedMap<ClassWrapper> collection) {
		this.collectionSpecs.forEach(spec -> spec.visit(collection));
	}

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		visit(classWrapper != null ? classWrapper.getDeclaredClasses() : null);
	}

	/**
	 * Factory class for ClassMapSuite. Has a list of ClassMapVisitorFactories provided by default to populate
	 * collectionSpecs, and uses the default ClassSuiteFactory to populate itemSuites.
	 */
	public static class ClassMapSuiteFactory implements ClassMapVisitorFactory<ClassMapSuite> {
		/**
		 * @return an immutable list of the default ClassMapVisitorFactories.
		 */
		private static ImmutableList<ClassMapVisitorFactory<?>> getDefaultVisitorFactories() {
			return ImmutableList.of(
					new NoExtraClassesSpec.NoExtraClassesSpecFactory(),
					new NoMissingClassesSpec.NoMissingClassesSpecFactory()
			);
		}

		/**
		 * A pre-made, default instance for consumers of ClassMapSuiteFactory to use.
		 */
		@NonNull
		public static ClassMapSuiteFactory getDefaultInst() {
			return new ClassMapSuiteFactory(null, null);
		}

		/**
		 * The ClassMapVisitorFactory instances that this factory uses to populate collectionVisitors with specs.
		 */
		@NonNull
		private final ImmutableList<ClassMapVisitorFactory<?>> collectionVisitorFactories;

		/**
		 * The ClassSuiteFactory used to populate itemSuites with specs.
		 */
		@NonNull
		private final ClassSuiteFactory classSuiteFactory;

		/**
		 * Constructor which takes an array of ClassMapVisitorFactory and a ClassSuiteFactory which will
		 * be used to create specifications to fill collectionSpecs and itemSuites respectively.
		 *
		 * @param collectionVisitorFactories The ClassMapVisitorFactory instances that this factory uses to populate
		 *                                   collectionVisitors with specs.
		 * @param classSuiteFactory          The ClassSuiteFactory used to populate itemSuites with specs.
		 */
		public ClassMapSuiteFactory(
				@CheckForNull final ImmutableList<ClassMapVisitorFactory<?>> collectionVisitorFactories,
				@CheckForNull final ClassSuiteFactory classSuiteFactory
		) {
			this.collectionVisitorFactories = collectionVisitorFactories != null && !collectionVisitorFactories.isEmpty() ?
					ImmutableList.copyOf(collectionVisitorFactories) :
					getDefaultVisitorFactories();
			this.classSuiteFactory = classSuiteFactory != null ?
					classSuiteFactory :
					ClassSuiteFactory.getDefaultInst(this);
		}

		@Override
		public ClassMapSuite buildFromCollection(
				@NonNull final NamedMap<ClassWrapper> classCollection,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Map<String, ClassWrapper> declaredClasses = classCollection.getItems();

			// Make a map of class names to their respective suites
			final Map<String, ClassSuite> classSpecSuites = declaredClasses.entrySet().stream()
					.collect(Collectors.toMap(
							Map.Entry::getKey,
							entry -> classSuiteFactory.buildFromItem(
									entry.getValue(),
									classCollection.getName(),
									noncomplianceConsumer
							)
					));

			// Make a list of specs for the map of class names to their respective wrappers
			final Set<MapVisitor<ClassWrapper>> mapVisitors = collectionVisitorFactories.stream()
					.map(factory -> factory.buildFromCollection(
							classCollection,
							classCollection.getName(),
							noncomplianceConsumer)
					)
					.collect(Collectors.toSet());

			return ClassMapSuite.builder()
					.itemSuites(classSpecSuites)
					.collectionSpecs(mapVisitors)
					.nameMapper(ClassWrapper::getName)
					.parentName(parentName)
					.build();
		}
	}
}
