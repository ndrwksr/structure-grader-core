package edu.kaiseran.structuregrader.specification.variable;

import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.visitor.ItemVisitor;
import edu.kaiseran.structuregrader.visitor.ItemVisitorFactory;
import edu.kaiseran.structuregrader.wrapper.FieldWrapper;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A type alias for a VariableSuite that specifies a FieldWrapper.
 */
@SuperBuilder
public class FieldSuite extends VariableSuite<FieldWrapper> {

	/**
	 * A factory for producing FieldSuites.
	 */
	public static class FieldSuiteFactory extends VariableSuiteFactory<FieldWrapper> {

		/**
		 * The set of factories to be used for making the specifications that make up the product FieldSuite.
		 */
		private final VariableSuiteFactory<FieldWrapper> variableSuiteFactory;

		/**
		 * Default constructor. If fieldVisitorFactories is null, getDefaultVisitorFactories() will be used instead.
		 *
		 * @param variableSuiteFactory The factory to build the VariableSuite for the product FieldSuite.
		 * @param fieldVisitorFactories The set of factories to be used for making the field specifications.
		 */
		public FieldSuiteFactory(
				@CheckForNull final VariableSuiteFactory<FieldWrapper> variableSuiteFactory,
				@CheckForNull final Set<ItemVisitorFactory<FieldWrapper, ?>> fieldVisitorFactories
		) {
			super(fieldVisitorFactories);
			this.variableSuiteFactory = variableSuiteFactory != null ?
					variableSuiteFactory :
					VariableSuiteFactory.getDefaultInst();
		}

		/**
		 * @return a pre-made, default instance for consumers of VariableSuiteFactory to use.
		 */
		public static FieldSuiteFactory getDefaultInst() {
			return new FieldSuiteFactory(null, null);
		}

		@Override
		public FieldSuite buildFromItem(
				@NonNull final FieldWrapper fieldWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			// Build field specs
			final Set<ItemVisitor<FieldWrapper>> specs = visitorFactories.stream()
					.map(factory -> factory.buildFromItem(
							fieldWrapper,
							parentName + "#" + fieldWrapper.getName(),
							noncomplianceConsumer)
					)
					.collect(Collectors.toSet());

			// Build variable suite
			final VariableSuite<FieldWrapper> variableSuite = variableSuiteFactory.buildFromItem(
					fieldWrapper,
					parentName,
					noncomplianceConsumer
			);
			specs.add(variableSuite);

			return FieldSuite.builder()
					.specs(specs)
					.parentName(parentName)
					.build();
		}
	}
}
