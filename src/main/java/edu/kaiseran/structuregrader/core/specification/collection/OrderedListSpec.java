package edu.kaiseran.structuregrader.core.specification.collection;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import edu.kaiseran.structuregrader.core.NamedMap;
import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.property.Ordinal;
import edu.kaiseran.structuregrader.core.visitor.MapVisitor;
import edu.kaiseran.structuregrader.core.visitor.MapVisitorFactory;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Specifies the expected order of items in a List of Ordinals.
 *
 * @param <ITEM> The type of the items in the specified list.
 */
public class OrderedListSpec<ITEM extends Ordinal> implements MapVisitor<ITEM> {
	/**
	 * The list of expected values, in their expected order.
	 */
	@NonNull
	private final ImmutableList<String> expectedOrder;

	/**
	 * The name of the parent element.
	 */
	@NonNull
	private final String parentName;

	/**
	 * Accepts any generated noncompliances, decoupling the consumption of noncompliances from their
	 * creation.
	 */
	@NonNull
	@JsonIgnore
	private final Consumer<Noncompliance> noncomplianceConsumer;

	@JsonCreator
	@Builder
	public OrderedListSpec(
			@NonNull @JsonProperty("expectedOrder") final ImmutableList<String> expectedOrder,
			@NonNull @JsonProperty("parentName") final String parentName,
			@NonNull @JacksonInject("noncomplianceConsumer") final Consumer<Noncompliance> noncomplianceConsumer
	) {
		this.expectedOrder = expectedOrder;
		this.parentName = parentName;
		this.noncomplianceConsumer = noncomplianceConsumer;
	}

	@Override
	public void visit(@CheckForNull final NamedMap<ITEM> namedMap) {
		if (namedMap != null) {
			final Map<String, ITEM> items = namedMap.getItems();

			final Map<String, Integer> actualNameToIndexMap = items.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getIndex()));

			for (int expectedIndex = 0; expectedIndex < expectedOrder.size(); expectedIndex++) {
				final String expectedName = expectedOrder.get(expectedIndex);
				final Integer actualIndex = actualNameToIndexMap.get(expectedName);
				if (actualIndex != null && actualIndex != expectedIndex) {
					noncomplianceConsumer.accept(OrdinalNoncompliance.builder()
							.expected(expectedIndex)
							.actual(actualIndex)
							.parentName(parentName)
							.explanation("Expected item with name " + expectedName + " to be at index %E, but was at index %A")
							.build());
				}
			}
		}
	}

	@SuperBuilder
	public static class OrdinalNoncompliance extends Noncompliance<Object> {

	}

	/**
	 * Factory for OrderedListSpec. Has a function to map the items in a list into their expected values.
	 * Has no state/configuration.
	 *
	 * @param <ITEM> The type of the items in the specified list.
	 */
	public static class OrderedListSpecFactory<ITEM extends Ordinal>
			implements MapVisitorFactory<ITEM, OrderedListSpec<ITEM>> {

		/**
		 * @param <ITEM> The type of the item in the specified list.
		 * @return a pre-configured instance for consumers of OrderedListSpecFactory to use. The default instance assumes
		 * EXPECTED is a String and uses Named::getName as the default itemToExpectedMapper.
		 */
		public static <ITEM extends Ordinal> OrderedListSpecFactory<ITEM> getDefaultInst() {
			return new OrderedListSpecFactory<>();
		}

		@Override
		public OrderedListSpec<ITEM> buildFromCollection(
				@NonNull final NamedMap<ITEM> namedMap,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			// Determine the expected value at each index
			final ImmutableList<String> expectedValues = ImmutableList.copyOf(
					namedMap.getItems().entrySet().stream()
							.sorted(Comparator.comparingInt(entry -> entry.getValue().getIndex()))
							.map(Map.Entry::getKey)
							.collect(Collectors.toList())
			);

			return OrderedListSpec.<ITEM>builder()
					.expectedOrder(expectedValues)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}
	}
}
