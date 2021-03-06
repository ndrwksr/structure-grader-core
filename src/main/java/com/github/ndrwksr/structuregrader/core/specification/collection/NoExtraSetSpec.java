package com.github.ndrwksr.structuregrader.core.specification.collection;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ndrwksr.structuregrader.core.NamedSet;
import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.visitor.SetVisitor;
import com.github.ndrwksr.structuregrader.core.visitor.SetVisitorFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Specifies that a NamedSet can have no extra items.
 *
 * @param <ITEM> The type of the items in the specified set.
 */
public class NoExtraSetSpec<ITEM> implements SetVisitor<ITEM> {
	/**
	 * The values expected to be present in the set.
	 */
	@NonNull
	private final Set<ITEM> expectedItemNames;

	@NonNull
	private final String itemTypePlural;

	/**
	 * The name of the parent element.
	 */
	@NonNull
	@Getter
	private final String parentName;

	@Builder
	@JsonCreator
	public NoExtraSetSpec(
			@NonNull @JsonProperty("expectedItemNames") final Set<ITEM> expectedItemNames,
			@NonNull @JsonProperty("itemTypePlural") final String itemTypePlural,
			@NonNull @JsonProperty("parentName") final String parentName,
			@NonNull @JacksonInject("noncomplianceConsumer") final Consumer<Noncompliance> noncomplianceConsumer
	) {
		this.expectedItemNames = expectedItemNames;
		this.itemTypePlural = itemTypePlural;
		this.parentName = parentName;
		this.noncomplianceConsumer = noncomplianceConsumer;
	}

	/**
	 * Accepts any generated noncompliances, decoupling the consumption of noncompliances from their
	 * creation.
	 */
	@NonNull
	@JsonIgnore
	private final Consumer<Noncompliance> noncomplianceConsumer;

	@Override
	public void visit(@CheckForNull final NamedSet<ITEM> collection) {
		if (collection != null) {
			MissingExtraHelper.checkSetForExtra(
					collection.getName(),
					expectedItemNames,
					collection.getItems(),
					itemTypePlural,
					noncomplianceConsumer
			);
		}
	}

	@Override
	public String getSpecDescription() {
		return "A specification that stipulates a maximum set of values a set can have.";
	}

	/**
	 * Creates NoExtraSetSpecs from NamedSets. Has no state/configuration.
	 *
	 * @param <ITEM> The type of the items in the specified sets.
	 */
	public static class NoExtraSetSpecFactory<ITEM> implements SetVisitorFactory<ITEM, NoExtraSetSpec<ITEM>> {
		private final String itemTypePlural;

		@Builder
		public NoExtraSetSpecFactory(@NonNull final String itemTypePlural) {
			this.itemTypePlural = itemTypePlural;
		}

		/**
		 * @param <ITEM> The type of the items the result factory will make specs for.
		 * @return a pre-configured instance for consumers of NoExtraSetSpecFactory to use.
		 */
		public static <ITEM> NoExtraSetSpecFactory<ITEM> getDefaultInst(@NonNull final String itemTypePlural) {
			return new NoExtraSetSpecFactory<>(itemTypePlural);
		}

		@Override
		public NoExtraSetSpec<ITEM> buildFromCollection(
				@NonNull final NamedSet<ITEM> namedSet,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return NoExtraSetSpec.<ITEM>builder()
					.expectedItemNames(namedSet.getItems())
					.noncomplianceConsumer(noncomplianceConsumer)
					.itemTypePlural(itemTypePlural)
					.parentName(parentName)
					.build();
		}
	}
}
