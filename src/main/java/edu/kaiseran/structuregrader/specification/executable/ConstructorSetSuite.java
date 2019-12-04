package edu.kaiseran.structuregrader.specification.executable;

import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.NamedSet;
import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specification.base.SetSuite;
import edu.kaiseran.structuregrader.specification.collection.NoExtraMapSpec.NoExtraSpecFactory;
import edu.kaiseran.structuregrader.specification.collection.NoMissingMapSpec.NoMissingSpecFactory;
import edu.kaiseran.structuregrader.visitor.*;
import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import edu.kaiseran.structuregrader.wrapper.ConstructorWrapper;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Specifies a list of constructors using MapVisitors and a function which maps the items in said list to strings.
 */
// TODO [ndrwksr | 12/4/19]: Refactor to use NamedSet and Set instead of maps
@SuperBuilder
public class ConstructorSetSuite extends SetSuite<ConstructorWrapper> implements ClassVisitor {
	private final String parentName;

	@Override
	public void visit(@CheckForNull final ClassWrapper classWrapper) {
		if (classWrapper != null) {
			super.visit(classWrapper.getConstructors());
		}
	}

	@Override
	public String getParentName() {
		return parentName;
	}

	/**
	 * Creates new ConstructorListSuites from NamedLists of ConstructorWrappers.
	 */
	public static class ConstructorSetSuiteFactory
			implements SetVisitorFactory<ConstructorWrapper, ConstructorSetSuite>, ClassVisitorFactory<ConstructorSetSuite> {

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
		public ConstructorSetSuiteFactory(
				@Nullable final ImmutableSet<MapVisitorFactory<ConstructorWrapper, ?>> mapVisitorFactories
		) {
			this.mapVisitorFactories = mapVisitorFactories != null ?
					mapVisitorFactories :
					getDefaultMapVisitorFactories();
		}

		/**
		 * @return a pre-configured instance for consumers of ConstructorListSuiteFactory to use.
		 */
		public static ConstructorSetSuiteFactory getDefaultInst() {
			return new ConstructorSetSuiteFactory(null);
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
		public ConstructorSetSuite buildFromCollection(
				@NonNull final NamedSet<ConstructorWrapper> namedSet,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			// Make a map where the keys are the signatures of the constructors,
			// and the values are the constructors themselves.
			final Map<String, ConstructorWrapper> valueToConstructorMap = namedSet.getItems().stream()
					.collect(Collectors.toMap(ConstructorWrapper::getSignature, Function.identity()));
			final NamedMap<ConstructorWrapper> namedMap = NamedMap.<ConstructorWrapper>builder()
					.name(namedSet.getName())
					.items(valueToConstructorMap)
					.build();

			final Set<MapVisitor<ConstructorWrapper>> mapVisitorSet = mapVisitorFactories.stream()
					.map(factory -> factory.buildFromCollection(namedMap, parentName, noncomplianceConsumer))
					.collect(Collectors.toSet());

			return ConstructorSetSuite.builder()
					.mapVisitors(mapVisitorSet)
					.itemValueFunction(ConstructorWrapper::getSignature)
					.build();
		}

		@Override
		public ConstructorSetSuite buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return buildFromCollection(classWrapper.getConstructors(), parentName, noncomplianceConsumer);
		}
	}
}
