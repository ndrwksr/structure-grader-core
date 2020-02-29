package edu.kaiseran.structuregrader.core.specification.executable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.specification.base.MapSuite;
import edu.kaiseran.structuregrader.core.specification.variable.VariableMapSuite;
import edu.kaiseran.structuregrader.core.specification.variable.VariableSuite;
import edu.kaiseran.structuregrader.core.visitor.*;
import edu.kaiseran.structuregrader.core.wrapper.ClassWrapper;
import edu.kaiseran.structuregrader.core.wrapper.MethodWrapper;
import lombok.Builder;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;


/**
 * A suite for a map of methods. Contains specifications for the map of methods as a whole, and suites for the
 * individual methods in the map.
 */
public class MethodMapSuite extends VariableMapSuite<MethodWrapper> implements ClassVisitor {
	// TODO: Handle overloading
	/**
	 * @param collectionSpecs The specifications for the collection as a whole.
	 * @param itemSuites      The suites for each individual Variable.
	 * @param parentName      The name of the parent of the specified element.
	 */
	@JsonCreator
	protected MethodMapSuite(
			@NonNull @JsonProperty("collectionSpecs") final Set<MapVisitor<MethodWrapper>> collectionSpecs,
			@NonNull @JsonProperty("itemSuites") final Map<String, VariableSuite<MethodWrapper>> itemSuites,
			@NonNull @JsonProperty("parentName") final String parentName
	) {
		super(collectionSpecs, itemSuites, parentName);
	}

	@Override
	public void visit(@CheckForNull final ClassWrapper classWrapper) {
		if (classWrapper != null) {
			super.visit(classWrapper.getMethods());
		}
	}

	/**
	 * A factory for MethodMapSuites. Has a default suite factory for making VariableSuites for MethodWrappers.
	 */
	public static class MethodMapSuiteFactory extends VariableMapSuiteFactory<MethodWrapper>
			implements ClassVisitorFactory<MethodMapSuite> {

		public static final String ITEM_TYPE_PLURAL = "methods";

		/**
		 * @param collectionVisitorFactories The factories to be used to populate MethodMapSuite.collectionSpecs.
		 * @param itemSuiteFactory           The factories to be used to populate MethodMapSuite.itemSpecs.
		 */
		@Builder
		public MethodMapSuiteFactory(
				@CheckForNull final ImmutableSet<MapVisitorFactory<MethodWrapper, ?>> collectionVisitorFactories,
				@CheckForNull final ItemVisitorFactory<MethodWrapper, VariableSuite<MethodWrapper>> itemSuiteFactory
		) {
			super(collectionVisitorFactories, itemSuiteFactory);
		}

		/**
		 * @return a pre-configured instance for consumers of MethodMapSuiteFactory to use.
		 */
		public static MethodMapSuite.MethodMapSuiteFactory getDefaultInst() {
			return MethodMapSuite.MethodMapSuiteFactory.builder().build();
		}

		@Override
		protected VariableSuite.VariableSuiteFactory<MethodWrapper> getDefaultSuiteFactory() {
			return VariableSuite.VariableSuiteFactory.getDefaultMethodSuiteFactoryInst();
		}

		@Override
		public MethodMapSuite buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final MapSuite<MethodWrapper, VariableSuite<MethodWrapper>> superSuite =
					super.buildFromCollection(classWrapper.getMethods(), parentName, noncomplianceConsumer);

			return new MethodMapSuite(superSuite.getCollectionSpecs(), superSuite.getItemSuites(), parentName);
		}
	}
}
