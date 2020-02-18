package edu.kaiseran.structuregrader.core.specification.variable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.property.Parameterized;
import edu.kaiseran.structuregrader.core.specification.base.MapSuite;
import edu.kaiseran.structuregrader.core.specification.collection.NoExtraMapSpec.NoExtraSpecFactory;
import edu.kaiseran.structuregrader.core.specification.collection.NoMissingMapSpec.NoMissingMapSpecFactory;
import edu.kaiseran.structuregrader.core.specification.variable.VariableSuite.VariableSuiteFactory;
import edu.kaiseran.structuregrader.core.visitor.ItemVisitor;
import edu.kaiseran.structuregrader.core.visitor.ItemVisitorFactory;
import edu.kaiseran.structuregrader.core.visitor.MapVisitor;
import edu.kaiseran.structuregrader.core.visitor.MapVisitorFactory;
import edu.kaiseran.structuregrader.core.wrapper.ParameterWrapper;
import lombok.Builder;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static edu.kaiseran.structuregrader.core.specification.collection.OrderedListSpec.OrderedListSpecFactory;

/**
 * A suite for a map of parameters. Contains specifications for the map of parameters as a whole, and suites for the
 * individual parameters in the map.
 */
public class ParameterMapSuite<PARAM extends Parameterized> extends VariableMapSuite<ParameterWrapper> implements ItemVisitor<PARAM> {
	/**
	 * @param collectionSpecs The specifications for the collection as a whole.
	 * @param itemSuites      The suites for each individual Variable.
	 * @param parentName      The name of the parent of the specified element.
	 */
	@JsonCreator
	protected ParameterMapSuite(
			@NonNull @JsonProperty("collectionSpecs") final Set<MapVisitor<ParameterWrapper>> collectionSpecs,
			@NonNull @JsonProperty("itemSuites") final Map<String, VariableSuite<ParameterWrapper>> itemSuites,
			@NonNull @JsonProperty("parentName") final String parentName
	) {
		super(collectionSpecs, itemSuites, parentName);
	}

	@Override
	public void visit(@CheckForNull final Parameterized parameterized) {
		if (parameterized != null) {
			super.visit(parameterized.getParameters());
		}
	}

	/**
	 * A factory for ParameterMapSuites. Has a default VariableSuite factory for making ParameterWrapper suites and has
	 * a default OrderedListFactory for ensuring that the order of parameters is correct.
	 */
	public static class ParameterMapSuiteFactory<PARAM extends Parameterized> extends VariableMapSuiteFactory<ParameterWrapper>
			implements ItemVisitorFactory<PARAM, ParameterMapSuite<PARAM>> {

		public static final String ITEM_TYPE_PLURAL = "parameters";

		/**
		 * @param collectionVisitorFactories The factories to be used to populate VariableMapSuite.collectionSpecs.
		 * @param itemSuiteFactory           The factories to be used to populate VariableMapSuite.itemSpecs.
		 */
		@Builder
		public ParameterMapSuiteFactory(
				@CheckForNull final ImmutableSet<MapVisitorFactory<ParameterWrapper, ?>> collectionVisitorFactories,
				@CheckForNull final ItemVisitorFactory<ParameterWrapper, VariableSuite<ParameterWrapper>> itemSuiteFactory
		) {
			super(collectionVisitorFactories != null ?
							collectionVisitorFactories :
							getDefaultCollectionVisitorFactories(),
					itemSuiteFactory
			);
		}

		/**
		 * @return an immutable set of MapVisitorFactories containing one factory which specifies that the elements must be
		 * in a certain order.
		 */
		private static ImmutableSet<MapVisitorFactory<ParameterWrapper, ?>> getDefaultCollectionVisitorFactories() {
			return ImmutableSet.of(
					OrderedListSpecFactory.getDefaultInst(),
					NoMissingMapSpecFactory.getDefaultInst(ITEM_TYPE_PLURAL),
					NoExtraSpecFactory.getDefaultInst(ITEM_TYPE_PLURAL)
			);
		}

		/**
		 * @return a pre-configured instance to be used by consumers of ParameterMapSuiteFactory.
		 */
		public static <PARAM extends Parameterized> ParameterMapSuiteFactory<PARAM> getDefaultInst() {
			return new ParameterMapSuiteFactory<>(null, null);
		}

		@Override
		protected ItemVisitorFactory<ParameterWrapper, VariableSuite<ParameterWrapper>> getDefaultSuiteFactory() {
			return VariableSuiteFactory.getDefaultInst();
		}

		@Override
		public ParameterMapSuite<PARAM> buildFromItem(
				@NonNull final PARAM parameterized,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final MapSuite<ParameterWrapper, VariableSuite<ParameterWrapper>> superSuite =
					super.buildFromCollection(parameterized.getParameters(), parentName, noncomplianceConsumer);

			return new ParameterMapSuite<>(superSuite.getCollectionSpecs(), superSuite.getItemSuites(), parentName);
		}
	}
}
