package edu.kaiseran.structuregrader.specification.quantity;

import com.google.common.collect.Sets;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.TemplateKeys;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Helper class for determining if there are missing or extra elements in a map, given a list of the
 * String keys that the map is expected to have.
 */
public class MissingExtraHelper {

	/**
	 * Checks the items map for any entries whose keys aren't in expectedItemNames. If any extra keys
	 * are found, noncompliances are created and fed into the provided Consumer.
	 *
	 * @param declaringName         The name of the parent of the items map. Used to provide additional
	 *                              context for Noncompliances.
	 * @param expectedItemNames     The expected String keys for the items map.
	 * @param items                 The map of items to check for extra entries.
	 * @param noncomplianceConsumer The consumer for any generated Noncompliances.
	 * @param <T>                   The type of the elements in items.
	 */
	public static <T> void checkForExtra(
			@NonNull final String declaringName,
			@NonNull final Set<String> expectedItemNames,
			@NonNull final Map<String, T> items,
			@NonNull final Consumer<Noncompliance> noncomplianceConsumer
	) {
		// Filter out entries whose keys aren't in expectedItemNames
		final Map<String, T> extraItems = items.entrySet().stream()
				.filter(entry -> !expectedItemNames.contains(entry.getKey()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		// If any such entries remain,
		if (extraItems.size() > 0) {
			final Set<String> actualItemNames = Sets.newHashSet(items.keySet());

			noncomplianceConsumer.accept(NoExtraSpec.ExtraNoncompliance.builder()
					.parentName(declaringName)
					.expected(expectedItemNames)
					.actual(actualItemNames)
					.explanation("Found extra items!"
							+ " Expected to have items " + TemplateKeys.EXPECTED_TEMPLATE
							+ ", but got " + TemplateKeys.ACTUAL_TEMPLATE)
					.build()
			);
		}
	}


	/**
	 * Checks the expectedItemNames list for any keys which don't appear in the items map. If any
	 * missing keys are found, noncompliances are created and fed into the provided Consumer.
	 *
	 * @param declaringName         The name of the parent of the items map. Used to provide additional
	 *                              * context for Noncompliances.
	 * @param expectedItemNames     The expected String keys for the items map.
	 * @param items                 The map of items to check for missing entries.
	 * @param noncomplianceConsumer The consumer for any generated Noncompliances.
	 * @param <T>                   The type of the elements in items.
	 */
	public static <T> void checkForMissing(
			@NonNull final String declaringName,
			@NonNull final Set<String> expectedItemNames,
			@NonNull final Map<String, T> items,
			@NonNull final Consumer<Noncompliance> noncomplianceConsumer
	) {
		// Find names of any entries in expectedItemNames that aren't also keys in items
		final List<String> missingItemNames = expectedItemNames.stream()
				.filter(name -> !items.containsKey(name))
				.collect(Collectors.toList());

		// If any such names found,
		if (missingItemNames.size() > 0) {
			final Set<String> actualItemNames = Sets.newHashSet(items.keySet());

			noncomplianceConsumer.accept(NoMissingSpec.MissingNoncompliance.builder()
					.parentName(declaringName)
					.expected(expectedItemNames)
					.actual(actualItemNames)
					.explanation("Found missing items!"
							+ " Expected to have items " + TemplateKeys.EXPECTED_TEMPLATE
							+ ", but got " + TemplateKeys.ACTUAL_TEMPLATE)
					.build()
			);
		}
	}
}
