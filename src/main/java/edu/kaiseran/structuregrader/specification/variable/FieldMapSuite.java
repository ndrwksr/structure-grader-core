package edu.kaiseran.structuregrader.specification.variable;

import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specification.base.MapSuite;
import edu.kaiseran.structuregrader.visitor.*;
import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import edu.kaiseran.structuregrader.wrapper.FieldWrapper;
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
	public FieldMapSuite(
			@NonNull final Set<MapVisitor<FieldWrapper>> collectionSpecs,
			@NonNull final Map<String, VariableSuite<FieldWrapper>> itemSuites,
			@NonNull final String parentName
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