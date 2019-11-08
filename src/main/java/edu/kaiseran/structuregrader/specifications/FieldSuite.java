package edu.kaiseran.structuregrader.specifications;

import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specifications.AnnotatedElementSuite.AnnotatedElementSuiteFactory;
import edu.kaiseran.structuregrader.specifications.MemberSpec.MemberSpecFactory;
import edu.kaiseran.structuregrader.specifications.TypedSpec.TypedSpecFactory;
import edu.kaiseran.structuregrader.visitors.ItemVisitor;
import edu.kaiseran.structuregrader.visitors.ItemVisitorFactory;
import edu.kaiseran.structuregrader.wrappers.FieldWrapper;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Contains all sub-specifications for specifying a single field, including the annotations, modifiers and type of a
 * field.
 */
@Data
@Builder
public class FieldSuite implements ItemVisitor<FieldWrapper> {
	/**
	 * The sub-specifications that make up this suite.
	 */
	@NonNull
	private final List<ItemVisitor<FieldWrapper>> specs;

	/**
	 * The name of the parent of the specified element.
	 */
	@NonNull
	@Getter
	private final String parentName;


	@Override
	public void visit(@CheckForNull final FieldWrapper fieldWrapper) {
		specs.forEach(spec -> spec.visit(fieldWrapper));
	}

	/**
	 * A factory for creating FieldSuites. Has a list of ItemVisitorFactories whose products can visit FieldWrappers.
	 * The default list of ItemVisitorFactories include an AnnotatedElementSuiteFactory, a MemberSpecFactory and a
	 * TypedSpecFactory.
	 */
	public static class FieldSuiteFactory implements ItemVisitorFactory<FieldWrapper, FieldSuite> {

		/**
		 * @return a pre-made, default instance for consumers of ClassSpecSuiteFactory to use.
		 */
		public static FieldSuiteFactory getDefaultInst() {
			return new FieldSuiteFactory(null);
		}

		private static ImmutableSet<ItemVisitorFactory<FieldWrapper, ?>> getDefaultVisitorFactories() {
			return ImmutableSet.of(
					AnnotatedElementSuiteFactory.getDefaultInst(),
					MemberSpecFactory.getDefaultInst(),
					TypedSpecFactory.getDefaultInst()
			);
		}

		/**
		 * Default constructor. If fieldVisitorFactories is null, getDefaultVisitorFactories() will be used instead.
		 *
		 * @param fieldVisitorFactories The set of factories to be used for making sub-specifications.
		 */
		public FieldSuiteFactory(
				@CheckForNull final Set<ItemVisitorFactory<FieldWrapper, FieldSuite>> fieldVisitorFactories
		) {
			this.fieldVisitorFactories = fieldVisitorFactories != null ?
					ImmutableSet.copyOf(fieldVisitorFactories) :
					getDefaultVisitorFactories();
		}

		/**
		 * The set of factories which will be used to generated the sub-specification for the products of this factory.
		 */
		private final ImmutableSet<ItemVisitorFactory<FieldWrapper, ?>> fieldVisitorFactories;

		@Override
		public FieldSuite buildFromItem(
				@NonNull final FieldWrapper fieldWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final List<ItemVisitor<FieldWrapper>> specs = fieldVisitorFactories.stream()
					.map(factory -> factory.buildFromItem(fieldWrapper, parentName, noncomplianceConsumer))
					.collect(Collectors.toList());

			return FieldSuite.builder()
					.specs(specs)
					.parentName(parentName)
					.build();
		}
	}
}
