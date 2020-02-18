package edu.kaiseran.structuregrader.core.specification.variable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.core.NamedMap;
import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.property.Variable;
import edu.kaiseran.structuregrader.core.specification.base.MapSuite;
import edu.kaiseran.structuregrader.core.visitor.ItemVisitorFactory;
import edu.kaiseran.structuregrader.core.visitor.MapVisitor;
import edu.kaiseran.structuregrader.core.visitor.MapVisitorFactory;
import lombok.Builder;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A MapSuite for Variables.
 *
 * @param <ITEM> The type of the Variable.
 */
public class VariableMapSuite<ITEM extends Variable> extends MapSuite<ITEM, VariableSuite<ITEM>> {
	/**
	 * @param collectionSpecs The specifications for the collection as a whole.
	 * @param itemSuites The suites for each individual Variable.
	 * @param parentName The name of the parent of the specified element.
	 */
	@Builder
	@JsonCreator
	protected VariableMapSuite(
			@NonNull @JsonProperty("collectionSpecs") final Set<MapVisitor<ITEM>> collectionSpecs,
			@NonNull @JsonProperty("itemSuites") final Map<String, VariableSuite<ITEM>> itemSuites,
			@NonNull @JsonProperty("parentName") final String parentName
	) {
		super(collectionSpecs, itemSuites, parentName);
	}

	/**
	 * A factory for VariableMapSuites.
	 *
	 * @param <ITEM> The type of the Variable that the MapSuite is for.
	 */
	public static abstract class VariableMapSuiteFactory<ITEM extends Variable>
			extends MapSuiteFactory<ITEM, VariableSuite<ITEM>> {
		/**
		 * @param collectionVisitorFactories The factories to be used to populate VariableMapSuite.collectionSpecs.
		 * @param itemSuiteFactory The factories to be used to populate VariableMapSuite.itemSpecs.
		 */
		public VariableMapSuiteFactory(
				@CheckForNull final ImmutableSet<MapVisitorFactory<ITEM, ?>> collectionVisitorFactories,
				@CheckForNull final ItemVisitorFactory<ITEM, VariableSuite<ITEM>> itemSuiteFactory
		) {
			super(collectionVisitorFactories, itemSuiteFactory, "variables");
		}

		@Override
		public VariableMapSuite<ITEM> buildFromCollection(
				@NonNull final NamedMap<ITEM> namedMap,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final MapSuite<ITEM, VariableSuite<ITEM>> superSuite =
					super.buildFromCollection(namedMap, parentName, noncomplianceConsumer);

			return new VariableMapSuite<>(superSuite.getCollectionSpecs(), superSuite.getItemSuites(), parentName);
		}
	}
}

