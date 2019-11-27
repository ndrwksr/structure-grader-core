package edu.kaiseran.structuregrader.specification;

import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.property.Named;
import edu.kaiseran.structuregrader.specification.collection.NoExtraSpec;
import edu.kaiseran.structuregrader.specification.collection.NoMissingSpec;
import edu.kaiseran.structuregrader.visitor.ItemVisitor;
import edu.kaiseran.structuregrader.visitor.ItemVisitorFactory;
import edu.kaiseran.structuregrader.visitor.MapVisitor;
import edu.kaiseran.structuregrader.visitor.MapVisitorFactory;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A suite specifying a collection of items. Has both specifications for the map as a whole (such as NoExtraSpec and
 * NoMissingSpec) and suites for each of the items in the map.
 *
 * @param <ITEM> The type of the item in the map.
 * @param <SUITE> The type of the suite for each individual value.
 */

@Data
public class MapSuite<ITEM, SUITE extends ItemVisitor<ITEM>> implements MapVisitor<ITEM> {

	/**
	 * The specifications to be applied to the collection as a whole.
	 */
	@NonNull
	protected final Set<MapVisitor<ITEM>> collectionSpecs;

	/**
	 * The suites for the individual items in the map.
	 */
	@NonNull
	protected final Map<String, SUITE> itemSuites;

	/**
	 * The name of the parent of the specified element.
	 */
	@NonNull
	protected final String parentName;

	/**
	 * @param collectionSpecs The specifications for the map as a whole.
	 * @param itemSuites The suites for the individual values in the map.
	 * @param parentName The name of the parent of the element being specified.
	 */
	public MapSuite(
			@NonNull final Set<MapVisitor<ITEM>> collectionSpecs,
			@NonNull final Map<String, SUITE> itemSuites,
			@NonNull final String parentName
	) {
		this.collectionSpecs = collectionSpecs;
		this.itemSuites = itemSuites;
		this.parentName = parentName;
	}

	@Override
	public void visit(@CheckForNull final NamedMap<ITEM> collection) {
		if (collection != null) {
			collectionSpecs.forEach(spec -> spec.visit(collection));
			collection.getItems().forEach((name, item) -> {
				if (itemSuites.containsKey(name)) {
					itemSuites.get(name).visit(item);
				}
			});
		}
	}


	/**
	 * Factory for MapSuites. Has a set of default visitor factories that will be used to populate collectionSpecs.
	 *
	 * @param <ITEM> The type of the values in the specified map.
	 * @param <SUITE> The type of the suite specifying each value.
	 */
	public static abstract class MapSuiteFactory<ITEM extends Named, SUITE extends ItemVisitor<ITEM>>
			implements MapVisitorFactory<ITEM, MapSuite<ITEM, SUITE>> {

		/**
		 * @return an immutable list of the default MapVisitorFactories.
		 */
		private static <ITEM extends Named> ImmutableSet<MapVisitorFactory<ITEM, ?>> getDefaultVisitorFactories() {
			return ImmutableSet.of(
					NoExtraSpec.NoExtraSpecFactory.getDefaultInst(),
					NoMissingSpec.NoMissingSpecFactory.getDefaultInst()
			);
		}

		/**
		 * The MapVisitorFactories that this factory uses to populate collectionSpecs with specs.
		 */
		@NonNull
		protected final ImmutableSet<MapVisitorFactory<ITEM, ?>> collectionVisitorFactories;

		/**
		 * The ItemVisitorFactory used to populate itemSuites with specs.
		 */
		@NonNull
		protected final ItemVisitorFactory<ITEM, SUITE> itemSuiteFactory;

		/**
		 * @param collectionVisitorFactories The MapVisitorFactories that this factory uses to populate
		 *                                   collectionVisitors with specs.
		 * @param itemSuiteFactory          The ItemVisitorFactory used to populate itemSuites with specs.
		 */
		public MapSuiteFactory(
				@CheckForNull final ImmutableSet<MapVisitorFactory<ITEM, ?>> collectionVisitorFactories,
				@CheckForNull final ItemVisitorFactory<ITEM, SUITE> itemSuiteFactory
		) {
			this.collectionVisitorFactories = collectionVisitorFactories != null && !collectionVisitorFactories.isEmpty() ?
					ImmutableSet.copyOf(collectionVisitorFactories) :
					getDefaultVisitorFactories();
			this.itemSuiteFactory = itemSuiteFactory != null ?
					itemSuiteFactory :
					getDefaultSuiteFactory();
		}

		/**
		 * @return the default factory to be used to make suites for the values in the specified map.
		 */
		protected abstract ItemVisitorFactory<ITEM,SUITE> getDefaultSuiteFactory();

		@Override
		public MapSuite<ITEM, SUITE> buildFromCollection(
				@NonNull final NamedMap<ITEM> namedMap,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			// Make a map of class names to their respective suites
			final Map<String, SUITE> itemSuites = namedMap.getItems().entrySet().stream()
					.collect(Collectors.toMap(
							Map.Entry::getKey,
							entry -> itemSuiteFactory.buildFromItem(
									entry.getValue(),
									namedMap.getName(),
									noncomplianceConsumer
							)
					));

			// Make a list of specs for the map of class names to their respective wrappers
			final Set<MapVisitor<ITEM>> collectionSpecs = collectionVisitorFactories.stream()
					.map(factory -> factory.buildFromCollection(
							namedMap,
							namedMap.getName(),
							noncomplianceConsumer)
					)
					.collect(Collectors.toSet());

			return new MapSuite<>(collectionSpecs, itemSuites, parentName);
		}
	}
}
