package edu.kaiseran.structuregrader.specification.clazz;

import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specification.MapSuite;
import edu.kaiseran.structuregrader.visitor.ClassHierarchyVisitor;
import edu.kaiseran.structuregrader.visitor.ItemVisitorFactory;
import edu.kaiseran.structuregrader.visitor.MapVisitor;
import edu.kaiseran.structuregrader.visitor.MapVisitorFactory;
import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Contains specifications for a hierarchy of classes, including checking for missing/extra classes and specifications
 * for each class at the specified level in the hierarchy.
 */
@EqualsAndHashCode(callSuper = true)
public class ClassMapSuite extends MapSuite<ClassWrapper, ClassSuite> implements ClassHierarchyVisitor {

	/**
	 * @param collectionSpecs The specifications for the map of classes as a whole.
	 * @param itemSuites The suites for the individual classes in the map.
	 * @param parentName The name of the package or class to which these the specified map of classes belongs.
	 */
	@Builder
	public ClassMapSuite(
			@NonNull final Set<MapVisitor<ClassWrapper>> collectionSpecs,
			@NonNull final Map<String, ClassSuite> itemSuites,
			@NonNull final String parentName
	) {
		super(collectionSpecs, itemSuites, parentName);
	}

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		super.visit(classWrapper != null ? classWrapper.getDeclaredClasses() : null);
	}

	/**
	 * Factory class for ClassMapSuite. Has a list of ClassMapVisitorFactories provided by default to populate
	 * collectionSpecs, and uses the default ClassSuiteFactory to populate itemSuites.
	 */
	public static class ClassMapSuiteFactory extends MapSuiteFactory<ClassWrapper, ClassSuite> {

		/**
		 * @param collectionVisitorFactories The factories to be used to populate ClassMapSuite.collectionSpecs.
		 * @param itemSuiteFactory The factories to be used to populate ClassMapSuite.itemSuites.
		 */
		@Builder
		public ClassMapSuiteFactory(
				@CheckForNull final ImmutableSet<MapVisitorFactory<ClassWrapper, ?>> collectionVisitorFactories,
				@CheckForNull final ItemVisitorFactory<ClassWrapper, ClassSuite> itemSuiteFactory
		){
			super(collectionVisitorFactories,itemSuiteFactory);
		}

		/**
		 * A pre-made, default instance for consumers of ClassMapSuiteFactory to use.
		 */
		@NonNull
		public static ClassMapSuiteFactory getDefaultInst() {
			return ClassMapSuiteFactory.builder().build();
		}

		@Override
		protected ItemVisitorFactory<ClassWrapper, ClassSuite> getDefaultSuiteFactory() {
			return ClassSuite.ClassSuiteFactory.getDefaultInst(this);
		}

		@Override
		public ClassMapSuite buildFromCollection(
				@NonNull final NamedMap<ClassWrapper> classCollection,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final MapSuite<ClassWrapper, ClassSuite> superSuite = super.buildFromCollection(
					classCollection,
					parentName,
					noncomplianceConsumer
			);

			return new ClassMapSuite(
					superSuite.getCollectionSpecs(),
					superSuite.getItemSuites(),
					parentName
			);
		}
	}

}
