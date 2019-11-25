package edu.kaiseran.structuregrader.specification.variable;

import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specification.MapSuite;
import edu.kaiseran.structuregrader.visitor.ItemVisitor;
import edu.kaiseran.structuregrader.visitor.ItemVisitorFactory;
import edu.kaiseran.structuregrader.visitor.MapVisitor;
import edu.kaiseran.structuregrader.visitor.MapVisitorFactory;
import edu.kaiseran.structuregrader.wrapper.MethodWrapper;
import edu.kaiseran.structuregrader.wrapper.ParameterWrapper;
import lombok.Builder;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static edu.kaiseran.structuregrader.specification.quantity.OrderedListSpec.OrderedListSpecFactory;

public class ParameterMapSuite extends VariableMapSuite<ParameterWrapper> implements ItemVisitor<MethodWrapper> {


	/**
	 * @param collectionSpecs The specifications for the collection as a whole.
	 * @param itemSuites      The suites for each individual Variable.
	 * @param parentName      The name of the parent of the specified element.
	 */
	protected ParameterMapSuite(
			@NonNull final Set<MapVisitor<ParameterWrapper>> collectionSpecs,
			@NonNull final Map<String, VariableSuite<ParameterWrapper>> itemSuites,
			@NonNull final String parentName
	) {
		super(collectionSpecs, itemSuites, parentName);
	}

	@Override
	public void visit(@CheckForNull final MethodWrapper methodWrapper) {
		if (methodWrapper != null) {
			super.visit(methodWrapper.getParameters());
		}
	}

	public static class ParameterMapSuiteFactory extends VariableMapSuiteFactory<ParameterWrapper>
			implements ItemVisitorFactory<MethodWrapper, ParameterMapSuite> {

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

		private static ImmutableSet<MapVisitorFactory<ParameterWrapper, ?>> getDefaultCollectionVisitorFactories() {
			return ImmutableSet.of(
					OrderedListSpecFactory.getDefaultInst()
			);
		}

		/**
		 * @return a pre-configured instance to be used by consumers of ParameterMapSuiteFactory.
		 */
		public static ParameterMapSuiteFactory getDefaultInst() {
			return new ParameterMapSuiteFactory(null, null);
		}

		@Override
		protected ItemVisitorFactory<ParameterWrapper, VariableSuite<ParameterWrapper>> getDefaultSuiteFactory() {
			return VariableSuite.VariableSuiteFactory.getDefaultInst();
		}

		@Override
		public ParameterMapSuite buildFromItem(
				@NonNull final MethodWrapper methodWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final MapSuite<ParameterWrapper, VariableSuite<ParameterWrapper>> superSuite =
					super.buildFromCollection(methodWrapper.getParameters(), parentName, noncomplianceConsumer);

			return new ParameterMapSuite(superSuite.getCollectionSpecs(), superSuite.getItemSuites(), parentName);
		}
	}
}
