package edu.kaiseran.structuregrader.specification.variable;

import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.property.Variable;
import edu.kaiseran.structuregrader.specification.AnnotatedSuite.AnnotatedSuiteFactory;
import edu.kaiseran.structuregrader.specification.TypedSpec.TypedSpecFactory;
import edu.kaiseran.structuregrader.visitor.ItemVisitor;
import edu.kaiseran.structuregrader.visitor.ItemVisitorFactory;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Contains all sub-specifications for specifying a single variable (a field or parameter), including the annotations,
 * modifiers and type of a variable.
 */
@Data
@SuperBuilder
public class VariableSuite<ITEM extends Variable> implements ItemVisitor<ITEM> {
	/**
	 * The sub-specifications that make up this suite.
	 */
	@NonNull
	protected final Set<ItemVisitor<ITEM>> specs;

	/**
	 * The name of the parent of the specified element.
	 */
	@NonNull
	@Getter
	protected final String parentName;

	@Override
	public void visit(@CheckForNull final ITEM item) {
		specs.forEach(spec -> spec.visit(item));
	}

	/**
	 * A factory for creating VariableSuite. Has a list of ItemVisitorFactories whose products can visit VariableWrappers.
	 * The default list of ItemVisitorFactories include an AnnotatedElementSuiteFactory, a MemberSpecFactory and a
	 * TypedSpecFactory.
	 */
	public static class VariableSuiteFactory<ITEM extends Variable>
			implements ItemVisitorFactory<ITEM, VariableSuite<ITEM>> {
		protected final ImmutableSet<ItemVisitorFactory<ITEM, ?>> visitorFactories;

		public static <ITEM extends Variable> VariableSuiteFactory<ITEM> getDefaultInst() {
			return new VariableSuiteFactory<>(null);
		}

		private static <ITEM extends Variable> ImmutableSet<ItemVisitorFactory<ITEM, ?>> getDefaultVisitorFactories() {
			return ImmutableSet.of(
					AnnotatedSuiteFactory.getDefaultInst(),
					TypedSpecFactory.getDefaultInst()
			);
		}

		/**
		 * @param visitorFactories The set of factories to be used for making sub-specifications.
		 */
		public VariableSuiteFactory(
				@CheckForNull final Set<ItemVisitorFactory<ITEM, ?>> visitorFactories
		) {
			this.visitorFactories = visitorFactories != null ?
					ImmutableSet.copyOf(visitorFactories) :
					getDefaultVisitorFactories();
		}

		public VariableSuite<ITEM> buildFromItem(
				@NonNull final ITEM item,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Set<ItemVisitor<ITEM>> specs = visitorFactories.stream()
			.map(factory -> factory.buildFromItem(item, parentName, noncomplianceConsumer))
			.collect(Collectors.toSet());

			return VariableSuite.<ITEM>builder()
					.specs(specs)
					.parentName(parentName)
					.build();
		}
	}
}
