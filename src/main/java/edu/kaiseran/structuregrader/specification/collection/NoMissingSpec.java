package edu.kaiseran.structuregrader.specification.collection;

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
 * Specifies the minimum set of entries that a map must contain.
 *
 * @param <ITEM> The type of the values in the specified map.
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NoMissingSpec<ITEM extends Named> extends MapSpec<ITEM, String> {

	@Override
	public void visit(@CheckForNull final NamedMap<ITEM> namedMap) {
		if (namedMap != null) {
			MissingExtraHelper.checkForMissing(
					namedMap.getName(),
					getExpected(),
					namedMap.getItems(),
					getNoncomplianceConsumer()
			);
		}
	}

	/**
	 * A noncompliance for when a NoMissingSpec encounters missing elements.
	 */
	@SuperBuilder
	public static class MissingNoncompliance extends Noncompliance<Set> {

	}

	/**
	 * Factory for NoMissingSpecs. Has no state/configuration.
	 *
	 * @param <ITEM> The type of the items in the maps this factory specifies.
	 */
	public static class NoMissingSpecFactory<ITEM extends Named>
			implements MapVisitorFactory<ITEM, NoMissingSpec<ITEM>> {

		/**
		 * @param <ITEM> The type of the items in the maps this factory specifies.
		 * @return a pre-configured instance for consumers of NoMissingSpecFactory to use.
		 */
		public static <ITEM extends Named> NoMissingSpecFactory<ITEM> getDefaultInst() {
			return new NoMissingSpecFactory<>();
		}

		@Override
		public NoMissingSpec<ITEM> buildFromCollection(
				@NonNull final NamedMap<ITEM> namedMap,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Set<String> declaredItemNames = Sets.newHashSet(namedMap.getItems().keySet());

			return NoMissingSpec.<ITEM>builder()
					.expected(declaredItemNames)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}
	}
}
