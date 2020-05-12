package com.github.ndrwksr.structuregrader.core.specification.variable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ndrwksr.structuregrader.core.HasChildSet;
import com.github.ndrwksr.structuregrader.core.NamedSpecSet;
import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.visitor.ItemVisitor;
import com.github.ndrwksr.structuregrader.core.visitor.ItemVisitorFactory;
import com.github.ndrwksr.structuregrader.core.wrapper.MethodWrapper;
import com.google.common.collect.ImmutableSet;
import com.github.ndrwksr.structuregrader.core.property.Variable;
import com.github.ndrwksr.structuregrader.core.specification.common.AnnotatedSuite.AnnotatedSuiteFactory;
import com.github.ndrwksr.structuregrader.core.specification.common.ModifiedSpec.ModifiedSpecFactory;
import com.github.ndrwksr.structuregrader.core.specification.common.TypedSpec.TypedSpecFactory;
import lombok.Builder;
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
@SuperBuilder(toBuilder = true)
public class VariableSuite<ITEM extends Variable> implements ItemVisitor<ITEM>, HasChildSet {
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

	@JsonCreator
	public static <ITEM extends Variable> VariableSuite<ITEM> jsonCreate(
			@NonNull @JsonProperty("specs") final Set<ItemVisitor<ITEM>> specs,
			@NonNull @JsonProperty("parentName") final String parentName
	) {
		return VariableSuite.<ITEM>builder()
				.specs(specs)
				.parentName(parentName)
				.build();
	}

	@Override
	public NamedSpecSet getChildSet() {
		return NamedSpecSet.<ItemVisitor<ITEM>>builder()
				.items(specs)
				.name(".specs")
				.build();
	}

	@Override
	public String getSpecDescription() {
		return "A suite for any set of Variables, such as fields, methods, parameters, etc.";
	}

	/**
	 * A factory for creating VariableSuite. Has a list of ItemVisitorFactories whose products can visit VariableWrappers.
	 * The default list of ItemVisitorFactories include an AnnotatedElementSuiteFactory, a MemberSpecFactory and a
	 * TypedSpecFactory.
	 */
	public static class VariableSuiteFactory<ITEM extends Variable>
			implements ItemVisitorFactory<ITEM, VariableSuite<ITEM>> {
		/**
		 * The factories for populating VariableSuite.specs.
		 */
		private final ImmutableSet<ItemVisitorFactory<ITEM, ?>> visitorFactories;

		/**
		 * @param <ITEM> The type of the Variable which the VariableSuite is for.
		 * @return a pre-configured instance for consumers of VariableSuiteFactory to use.
		 */
		public static <ITEM extends Variable> VariableSuiteFactory<ITEM> getDefaultInst() {
			return new VariableSuiteFactory<>(null);
		}

		public static VariableSuiteFactory<MethodWrapper> getDefaultMethodSuiteFactoryInst() {
			return new VariableSuiteFactory<>(ImmutableSet.of(
					ModifiedSpecFactory.getDefaultInst(),
					AnnotatedSuiteFactory.getDefaultInst(),
					TypedSpecFactory.getDefaultInst(),
					ParameterMapSuite.ParameterMapSuiteFactory.getDefaultInst()
			));
		}

		/**
		 * @param <ITEM> The type of the Variable which the VariableSuite is for.
		 * @return the default ItemVisitorFactories to be used to populate VariableSuite.specs.
		 */
		private static <ITEM extends Variable> ImmutableSet<ItemVisitorFactory<ITEM, ?>> getDefaultVisitorFactories() {
			return ImmutableSet.of(
					ModifiedSpecFactory.getDefaultInst(),
					AnnotatedSuiteFactory.getDefaultInst(),
					TypedSpecFactory.getDefaultInst()
			);
		}

		/**
		 * @param visitorFactories The set of factories to be used for making sub-specifications.
		 */
		@Builder
		public VariableSuiteFactory(
				@CheckForNull final ImmutableSet<ItemVisitorFactory<ITEM, ?>> visitorFactories
		) {
			this.visitorFactories = visitorFactories != null ?
					ImmutableSet.copyOf(visitorFactories) :
					getDefaultVisitorFactories();
		}

		@Override
		public VariableSuite<ITEM> buildFromItem(
				@NonNull final ITEM item,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Set<ItemVisitor<ITEM>> specs = visitorFactories.stream()
					.map(factory -> factory.buildFromItem(
							item,
							item.getName(),
							noncomplianceConsumer
					))
					.collect(Collectors.toSet());

			return VariableSuite.<ITEM>builder()
					.specs(specs)
					.parentName(parentName)
					.build();
		}
	}
}
