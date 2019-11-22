package edu.kaiseran.structuregrader.specification.quantity;

import com.google.common.collect.ImmutableList;
import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.property.Ordinal;
import edu.kaiseran.structuregrader.visitor.MapVisitor;
import edu.kaiseran.structuregrader.visitor.MapVisitorFactory;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Specifies the expected order and value of items in a list.
 *
 * @param <ITEM> The type of the items in the specified list.
 */
@Builder
public class OrderedListSpec<ITEM extends Ordinal> implements MapVisitor<ITEM> {
	/**
	 * The list of expected values (in order)
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
	private final Consumer<Noncompliance> noncomplianceConsumer;

	@Override
	public void visit(@CheckForNull final NamedMap<ITEM> namedMap) {
		if (namedMap != null) {
			final Map<String, ITEM> items = namedMap.getItems();

			// Ensure items was the same length as expected
			if (items.size() != expectedOrder.size()) {
				noncomplianceConsumer.accept(OrdinalNoncompliance.builder()
						.expected(expectedOrder.size())
						.actual(items.size())
						.parentName(parentName)
						.explanation("Expected items to have length %E but had %A")
						.build());
			} else {
				final Map<String, Integer> actualNameToIndexMap = items.entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getIndex()));
				items.forEach((expectedNameAtIndex, item) -> {
					final String actualNameAtIndex = this.expectedOrder.get(item.getIndex());

					if (!Objects.equals(actualNameAtIndex, expectedNameAtIndex)) {
						noncomplianceConsumer.accept(OrdinalNoncompliance.builder()
								.expected(expectedNameAtIndex)
								.actual(actualNameAtIndex)
								.parentName(parentName)
								.explanation("Expected item at index " + expectedOrder.indexOf(expectedNameAtIndex) +
										" to have value %E but had %A")
								.build());
					}
				});
			}
		}
	}

	@SuperBuilder
	public static class OrdinalNoncompliance extends Noncompliance<Object> {

	}

	/**
	 * Factory for OrderedListSpec. Has a function to map the items in a list into their expected values.
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
