package edu.kaiseran.structuregrader.core.specification.collection;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import edu.kaiseran.structuregrader.core.NamedMap;
import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.property.Named;
import edu.kaiseran.structuregrader.core.specification.base.MapSpec;
import edu.kaiseran.structuregrader.core.visitor.MapVisitorFactory;
import lombok.Builder;
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
public class NoMissingMapSpec<ITEM extends Named> extends MapSpec<ITEM, String> {
	private final String itemTypePlural;

	@JsonCreator
	public static <ITEM extends Named> NoMissingMapSpec<ITEM> jsonCreate(
			@NonNull @JsonProperty("parentName") final String parentName,
			@NonNull @JsonProperty("expectedItemNames") final Set<String> expectedItemNames,
			@NonNull @JsonProperty("itemTypePlural") final String itemTypePlural,
			@NonNull @JacksonInject("noncomplianceConsumer") final Consumer<Noncompliance> noncomplianceConsumer
	) {
		return NoMissingMapSpec.<ITEM>builder()
				.itemTypePlural(itemTypePlural)
				.parentName(parentName)
				.expectedItemNames(expectedItemNames)
				.noncomplianceConsumer(noncomplianceConsumer)
				.build();
	}

	@Override
	public void visit(@CheckForNull final NamedMap<ITEM> namedMap) {
		if (namedMap != null) {
			MissingExtraHelper.checkMapForMissing(
					namedMap.getName(),
					getExpectedItemNames(),
					namedMap.getItems(),
					itemTypePlural,
					getNoncomplianceConsumer()
			);
		}
	}

	/**
	 * Factory for NoMissingSpecs. Has no state/configuration.
	 *
	 * @param <ITEM> The type of the items in the maps this factory specifies.
	 */
	public static class NoMissingMapSpecFactory<ITEM extends Named>
			implements MapVisitorFactory<ITEM, NoMissingMapSpec<ITEM>> {
		private final String itemTypePlural;

		@Builder
		public NoMissingMapSpecFactory(@NonNull final String itemTypePlural) {
			this.itemTypePlural = itemTypePlural;
		}

		/**
		 * @param <ITEM> The type of the items in the maps this factory specifies.
		 * @return a pre-configured instance for consumers of NoMissingSpecFactory to use.
		 */
		public static <ITEM extends Named> NoMissingMapSpecFactory<ITEM> getDefaultInst(@NonNull final String itemTypePlural
		) {
			return new NoMissingMapSpecFactory<>(itemTypePlural);
		}

		@Override
		public NoMissingMapSpec<ITEM> buildFromCollection(
				@NonNull final NamedMap<ITEM> namedMap,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Set<String> declaredItemNames = Sets.newHashSet(namedMap.getItems().keySet());

			return NoMissingMapSpec.<ITEM>builder()
					.expectedItemNames(declaredItemNames)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.itemTypePlural(itemTypePlural)
					.build();
		}
	}
}
