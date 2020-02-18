package edu.kaiseran.structuregrader.core.specification.collection;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kaiseran.structuregrader.core.NamedSet;
import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.visitor.SetVisitor;
import edu.kaiseran.structuregrader.core.visitor.SetVisitorFactory;
import lombok.Builder;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Specifies that no items can be missing from a NamedSet.
 *
 * @param <ITEM> The type of the items in the specified set.
 */
public class NoMissingSetSpec<ITEM> implements SetVisitor<ITEM> {
	/**
	 * The minimum set of items allowed in the specified set.
	 */
	private final Set<ITEM> expectedItemNames;

	/**
	 * Accepts any generated noncompliances, decoupling the consumption of noncompliances from their
	 * creation.
	 */
	@NonNull
	@JsonIgnore
	private final Consumer<Noncompliance> noncomplianceConsumer;

	private final String itemTypePlural;

	@Builder
	@JsonCreator
	public NoMissingSetSpec(
			@NonNull @JsonProperty("expectedItemNames") final Set<ITEM> expectedItemNames,
			@NonNull @JsonProperty("itemTypePlural") final String itemTypePlural,
			@NonNull @JacksonInject("noncomplianceConsumer") final Consumer<Noncompliance> noncomplianceConsumer
	) {
		this.expectedItemNames = expectedItemNames;
		this.itemTypePlural = itemTypePlural;
		this.noncomplianceConsumer = noncomplianceConsumer;
	}

	@Override
	public void visit(@CheckForNull final NamedSet<ITEM> collection) {
		if (collection != null) {
			MissingExtraHelper.checkSetForMissing(
					collection.getName(),
					expectedItemNames,
					collection.getItems(),
					itemTypePlural,
					noncomplianceConsumer
			);
		}
	}

	/**
	 * Creates new NoMissingSetSpecs from provided NamedSets.
	 *
	 * @param <ITEM> The type of the items in the specified sets.
	 */
	public static class NoMissingSetSpecFactory<ITEM> implements SetVisitorFactory<ITEM, NoMissingSetSpec<ITEM>> {
		private final String itemTypePlural;

		@Builder
		public NoMissingSetSpecFactory(@NonNull final String itemTypePlural) {
			this.itemTypePlural = itemTypePlural;
		}

		/**
		 * @param <ITEM> The type of the items in the specified sets.
		 * @return a pre-configured instance for consumers of NoMissingSetSpec to use.
		 */
		public static <ITEM> NoMissingSetSpec.NoMissingSetSpecFactory<ITEM> getDefaultInst(	@NonNull final String itemTypePlural) {
			return new NoMissingSetSpec.NoMissingSetSpecFactory<>(itemTypePlural);
		}

		@Override
		public NoMissingSetSpec<ITEM> buildFromCollection(
				@NonNull final NamedSet<ITEM> namedSet,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return NoMissingSetSpec.<ITEM>builder()
					.expectedItemNames(namedSet.getItems())
					.noncomplianceConsumer(noncomplianceConsumer)
					.itemTypePlural(itemTypePlural)
					.build();
		}
	}
}
