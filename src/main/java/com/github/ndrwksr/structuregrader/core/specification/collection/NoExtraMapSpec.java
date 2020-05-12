package com.github.ndrwksr.structuregrader.core.specification.collection;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ndrwksr.structuregrader.core.NamedMap;
import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.property.Named;
import com.github.ndrwksr.structuregrader.core.specification.base.MapSpec;
import com.github.ndrwksr.structuregrader.core.visitor.MapVisitorFactory;
import com.google.common.collect.Sets;
import lombok.Builder;
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
public class NoExtraMapSpec<ITEM extends Named> extends MapSpec<ITEM, String> {
	@NonNull private final String itemTypePlural;

	@JsonCreator
	public static <ITEM extends Named> NoExtraMapSpec<ITEM> jsonCreate(
			@NonNull @JsonProperty("parentName") final String parentName,
			@NonNull @JsonProperty("expectedItemNames") final Set<String> expectedItemNames,
			@NonNull @JsonProperty("itemTypePlural") final String itemTypePlural,
			@NonNull @JacksonInject("noncomplianceConsumer") final Consumer<Noncompliance> noncomplianceConsumer
	) {
		return NoExtraMapSpec.<ITEM>builder()
				.itemTypePlural(itemTypePlural)
				.parentName(parentName)
				.expectedItemNames(expectedItemNames)
				.noncomplianceConsumer(noncomplianceConsumer)
				.build();
	}

	@Override
	public void visit(@CheckForNull final NamedMap<ITEM> namedMap) {
		if (namedMap != null) {
			MissingExtraHelper.checkMapForExtra(
					namedMap.getName(),
					getExpectedItemNames(),
					namedMap.getItems(),
					itemTypePlural,
					getNoncomplianceConsumer()
			);
		}
	}

	@Override
	public String getSpecDescription() {
		return "A specification that stipulates a maximum set of values in a map.";
	}

	/**
	 * Factory for NoExtraSpecs. Has no state/configuration.
	 *
	 * @param <ITEM> The type of the items in the maps this factory specifies.
	 */
	public static class NoExtraSpecFactory<ITEM extends Named>
			implements MapVisitorFactory<ITEM, NoExtraMapSpec<ITEM>> {
		private final String itemTypePlural;

		@Builder
		public NoExtraSpecFactory(final String itemTypePlural) {
			this.itemTypePlural = itemTypePlural;
		}


		/**
		 * @param <ITEM> The type of the items in the maps this factory specifies.
		 * @return a pre-configured instance for consumers of NoExtraSpecFactory to use.
		 */
		public static <ITEM extends Named> NoExtraSpecFactory<ITEM> getDefaultInst(@NonNull final String itemTypePlural) {
			return new NoExtraSpecFactory<>(itemTypePlural);
		}

		@Override
		public NoExtraMapSpec<ITEM> buildFromCollection(
				@NonNull final NamedMap<ITEM> namedMap,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Set<String> declaredItemNames = Sets.newHashSet(namedMap.getItems().keySet());

			return NoExtraMapSpec.<ITEM>builder()
					.expectedItemNames(declaredItemNames)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.itemTypePlural(itemTypePlural)
					.build();
		}
	}
}
