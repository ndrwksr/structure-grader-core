package com.github.ndrwksr.structuregrader.core.specification.executable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ndrwksr.structuregrader.core.NamedMap;
import com.github.ndrwksr.structuregrader.core.NamedSet;
import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.specification.base.SetSuite;
import com.github.ndrwksr.structuregrader.core.specification.collection.NoExtraMapSpec.NoExtraSpecFactory;
import com.github.ndrwksr.structuregrader.core.specification.collection.NoMissingMapSpec;
import com.github.ndrwksr.structuregrader.core.visitor.*;
import com.github.ndrwksr.structuregrader.core.wrapper.ClassWrapper;
import com.github.ndrwksr.structuregrader.core.wrapper.ConstructorWrapper;
import com.google.common.collect.ImmutableSet;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.HashSet;
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
	@NonNull
	private final String parentName;

	@JsonCreator
	public static ConstructorSetSuite jsonCreate(
			@NonNull @JsonProperty("parentName") final String parentName,
			@NonNull @JsonProperty("mapVisitors") final Set<MapVisitor<ConstructorWrapper>> mapVisitors
	) {
		return ConstructorSetSuite.builder()
				.parentName(parentName)
				.mapVisitors(mapVisitors)
				.itemValueFunction(ConstructorWrapper::getSignature)
				.build();
	}

	@Override
	public void visit(@CheckForNull final ClassWrapper classWrapper) {
		if (classWrapper != null) {
			super.visit(
					NamedSet.<ConstructorWrapper>builder()
							.items(new HashSet<>(classWrapper.getConstructors().getItems()))
							.name(classWrapper.getConstructors().getName())
							.build()
			);
		}
	}

	@Override
	public String getParentName() {
		return parentName;
	}

	@Override
	public String getSpecDescription() {
		return "A suite that stipulates the set of constructors a class must have.";
	}

	/**
	 * Creates new ConstructorListSuites from NamedLists of ConstructorWrappers.
	 */
	public static class ConstructorSetSuiteFactory
			implements SetVisitorFactory<ConstructorWrapper, ConstructorSetSuite>, ClassVisitorFactory<ConstructorSetSuite> {

		public static final String ITEM_TYPE_PLURAL = "constructors";
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
					NoExtraSpecFactory.getDefaultInst(ITEM_TYPE_PLURAL),
					NoMissingMapSpec.NoMissingMapSpecFactory.getDefaultInst(ITEM_TYPE_PLURAL)
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
					//.filter(ExecutableWrapper::isSynthetic)
					.collect(Collectors.toMap(ConstructorWrapper::getSignature, Function.identity()));
			final NamedMap<ConstructorWrapper> namedMap = NamedMap.<ConstructorWrapper>builder()
					.name(namedSet.getName())
					.items(valueToConstructorMap)
					.build();

			final Set<MapVisitor<ConstructorWrapper>> mapVisitorSet = mapVisitorFactories.stream()
					.map(factory -> factory.buildFromCollection(namedMap, parentName, noncomplianceConsumer))
					.collect(Collectors.toSet());

			return ConstructorSetSuite.builder()
					.parentName(namedSet.getName())
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
