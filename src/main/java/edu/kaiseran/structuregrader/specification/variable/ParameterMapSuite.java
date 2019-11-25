package edu.kaiseran.structuregrader.specification.variable;

import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specification.MapSuite;
<<<<<<< HEAD
import edu.kaiseran.structuregrader.specification.variable.VariableSuite.VariableSuiteFactory;
=======
>>>>>>> a4d1db17f6f2a31706932a6803bdb764a5b87e15
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

<<<<<<< HEAD
/**
 * A suite for a map of parameters. Contains specifications for the map of parameters as a whole, and suites for the
 * individual parameters in the map.
 */
public class ParameterMapSuite extends VariableMapSuite<ParameterWrapper> implements ItemVisitor<MethodWrapper> {
=======
public class ParameterMapSuite extends VariableMapSuite<ParameterWrapper> implements ItemVisitor<MethodWrapper> {


>>>>>>> a4d1db17f6f2a31706932a6803bdb764a5b87e15
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

<<<<<<< HEAD
	/**
	 * A factory for ParameterMapSuites. Has a default VariableSuite factory for making ParameterWrapper suites and has
	 * a default OrderedListFactory for ensuring that the order of parameters is correct.
	 */
=======
>>>>>>> a4d1db17f6f2a31706932a6803bdb764a5b87e15
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

<<<<<<< HEAD
		/**
		 * @return an immutable set of MapVisitorFactories containing one factory which specifies that the elements must be
		 * in a certain order.
		 */
=======
>>>>>>> a4d1db17f6f2a31706932a6803bdb764a5b87e15
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
<<<<<<< HEAD
			return VariableSuiteFactory.getDefaultInst();
=======
			return VariableSuite.VariableSuiteFactory.getDefaultInst();
>>>>>>> a4d1db17f6f2a31706932a6803bdb764a5b87e15
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
