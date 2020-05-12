package com.github.ndrwksr.structuregrader.core.specification.variable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.specification.base.MapSuite;
import com.github.ndrwksr.structuregrader.core.visitor.*;
import com.github.ndrwksr.structuregrader.core.wrapper.ClassWrapper;
import com.github.ndrwksr.structuregrader.core.wrapper.FieldWrapper;
import com.google.common.collect.ImmutableSet;
import lombok.Builder;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;


/**
 * A suite for a map of fields. Contains specifications for the map of fields as a whole, and suites for the individual
 * fields in the map.
 */
public class FieldMapSuite extends VariableMapSuite<FieldWrapper> implements ClassVisitor {
	/**
	 * @param collectionSpecs The specifications for the map of fields as a whole.
	 * @param itemSuites The suites for the individual fields in the map.
	 * @param parentName The name of the class to which these fields belong.
	 */
	@JsonCreator
	public FieldMapSuite(
			@NonNull @JsonProperty("collectionSpecs") final Set<MapVisitor<FieldWrapper>> collectionSpecs,
			@NonNull @JsonProperty("itemSuites") final Map<String, VariableSuite<FieldWrapper>> itemSuites,
			@NonNull @JsonProperty("parentName") final String parentName
	) {
		super(collectionSpecs, itemSuites, parentName);
	}

	@Override
	public void visit(@CheckForNull final ClassWrapper classWrapper) {
		if (classWrapper != null) {
			super.visit(classWrapper.getFields());
		}
	}

	/**
	 * A factory for FieldMapSuites. Has a default suite factory for making VariableSuites for FieldWrappers.
	 */
	public static class FieldMapSuiteFactory extends VariableMapSuiteFactory<FieldWrapper>
			implements ClassVisitorFactory<FieldMapSuite> {

		public static final String ITEM_TYPE_PLURAL = "fields";

		/**
		 * @param collectionVisitorFactories The factories to be used to populate FieldMapSuite.collectionSpecs.
		 * @param itemSuiteFactory The factories to be used to populate FieldMapSuite.itemSpecs.
		 */
		@Builder
		public FieldMapSuiteFactory(
				@CheckForNull final ImmutableSet<MapVisitorFactory<FieldWrapper, ?>> collectionVisitorFactories,
				@CheckForNull final ItemVisitorFactory<FieldWrapper, VariableSuite<FieldWrapper>> itemSuiteFactory
		) {
			super(collectionVisitorFactories, itemSuiteFactory);
		}

		/**
		 * @return a pre-configured instance for consumers of FieldMapSuiteFactory to use.
		 */
		public static FieldMapSuiteFactory getDefaultInst() {
			return FieldMapSuiteFactory.builder().build();
		}

		@Override
		protected VariableSuite.VariableSuiteFactory<FieldWrapper> getDefaultSuiteFactory() {
			return VariableSuite.VariableSuiteFactory.<FieldWrapper>builder().build();
		}

		@Override
		public FieldMapSuite buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final MapSuite<FieldWrapper, VariableSuite<FieldWrapper>> superSuite =
					super.buildFromCollection(classWrapper.getFields(), parentName, noncomplianceConsumer);

			return new FieldMapSuite(superSuite.getCollectionSpecs(), superSuite.getItemSuites(), parentName);
		}
	}
}