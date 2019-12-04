package edu.kaiseran.structuregrader.specification.executable;

import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.NamedList;
import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specification.base.ListSuite;
import edu.kaiseran.structuregrader.specification.collection.NoExtraSpec.NoExtraSpecFactory;
import edu.kaiseran.structuregrader.specification.collection.NoMissingSpec.NoMissingSpecFactory;
import edu.kaiseran.structuregrader.visitor.ListVisitorFactory;
import edu.kaiseran.structuregrader.visitor.MapVisitor;
import edu.kaiseran.structuregrader.visitor.MapVisitorFactory;
import edu.kaiseran.structuregrader.wrapper.ConstructorWrapper;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Specifies a list of constructors using MapVisitors and a function which maps the items in said list to strings.
 */
@SuperBuilder
public class ConstructorListSuite extends ListSuite<ConstructorWrapper> {
	/**
	 * Creates new ConstructorListSuites from NamedLists of ConstructorWrappers.
	 */
	public static class ConstructorListSuiteFactory
			implements ListVisitorFactory<ConstructorWrapper, ConstructorListSuite> {

		/**
		 * The factories used to make MapVisitors to visit the map that results from applying the item value function to all
		 * constructors in visited lists.
		 */
		private final ImmutableSet<MapVisitorFactory<ConstructorWrapper, ?>> mapVisitorFactories;

		/**
		 * @param mapVisitorFactories The factories to populate ConstructorWrapper.mapVisitors. If null, the defaults will
		 *                            be used.
		 */
		@Builder
		public ConstructorListSuiteFactory(
				@Nullable final ImmutableSet<MapVisitorFactory<ConstructorWrapper, ?>> mapVisitorFactories
		) {
			this.mapVisitorFactories = mapVisitorFactories != null ?
					mapVisitorFactories :
					getDefaultMapVisitorFactories();
		}

		/**
		 * @return a pre-configured instance for consumers of ConstructorListSuiteFactory to use.
		 */
		public static ConstructorListSuiteFactory getDefaultInst() {
			return new ConstructorListSuiteFactory(null);
		}

		/**
		 * @return the default MapVisitorFactories for populating ConstructorWrapper.mapVisitors.
		 */
		private ImmutableSet<MapVisitorFactory<ConstructorWrapper, ?>> getDefaultMapVisitorFactories() {
			return ImmutableSet.of(
					NoExtraSpecFactory.getDefaultInst(),
					NoMissingSpecFactory.getDefaultInst()
			);
		}

		@Override
		public ConstructorListSuite buildFromCollection(
				@NonNull final NamedList<ConstructorWrapper> namedList,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			// Make a map where the keys are the signatures of the constructors,
			// and the values are the constructors themselves.
			final Map<String, ConstructorWrapper> valueToConstructorMap = namedList.getItems().stream()
					.collect(Collectors.toMap(ConstructorWrapper::getSignature, Function.identity()));
			final NamedMap<ConstructorWrapper> namedMap = NamedMap.<ConstructorWrapper>builder()
					.name(namedList.getName())
					.items(valueToConstructorMap)
					.build();

			final Set<MapVisitor<ConstructorWrapper>> mapVisitorSet = mapVisitorFactories.stream()
					.map(factory -> factory.buildFromCollection(namedMap, parentName, noncomplianceConsumer))
					.collect(Collectors.toSet());

			return ConstructorListSuite.builder()
					.mapVisitors(mapVisitorSet)
					.itemValueFunction(ConstructorWrapper::getSignature)
					.build();
		}
	}
}
