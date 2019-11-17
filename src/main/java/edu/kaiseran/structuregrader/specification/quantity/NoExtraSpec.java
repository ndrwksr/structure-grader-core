package edu.kaiseran.structuregrader.specification.quantity;

import com.google.common.collect.Sets;
import edu.kaiseran.structuregrader.property.Named;
import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specification.MapSpec;
import edu.kaiseran.structuregrader.visitor.MapVisitorFactory;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Specifies that no extra items in a map may exist.
 *
 * @param <ITEM> The type of the items in the specified collection.
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NoExtraSpec<ITEM extends Named> extends MapSpec<ITEM, String> {

	@Override
	public void visit(@CheckForNull final NamedMap<ITEM> namedMap) {
		if (namedMap != null) {
			MissingExtraHelper.checkForExtra(
					namedMap.getName(),
					getExpected(),
					namedMap.getItems(),
					getNoncomplianceConsumer()
			);
		}
	}

	/**
	 * Factory for NoExtraSpecs. Has no state/configuration.
	 *
	 * @param <ITEM> The type of the items in the maps this factory specifies.
	 */
	public static class NoExtraSpecFactory<ITEM extends Named>
			implements MapVisitorFactory<ITEM, NoExtraSpec<ITEM>> {

		/**
		 * @param <ITEM> The type of the items in the maps this factory specifies.
		 * @return a pre-configured instance for consumers of NoExtraSpecFactory to use.
		 */
		public static <ITEM extends Named> NoExtraSpecFactory<ITEM> getDefaultInst() {
			return new NoExtraSpecFactory<>();
		}

		@Override
		public NoExtraSpec<ITEM> buildFromCollection(
				@NonNull final NamedMap<ITEM> namedMap,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Set<String> declaredItemNames = Sets.newHashSet(namedMap.getItems().keySet());

			return NoExtraSpec.<ITEM>builder()
					.expected(declaredItemNames)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}
	}
}
