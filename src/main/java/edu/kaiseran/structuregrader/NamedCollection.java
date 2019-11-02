package edu.kaiseran.structuregrader;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * A map from element names to the actual items, which itself has a name.
 *
 * @param <ITEM> The type of the items in the collection.
 */
@Data
@SuperBuilder
public class NamedCollection<ITEM> {
	/**
	 * The name of the collection.
	 */
	protected final String name;

	/**
	 * The items in the collection.
	 */
	protected final Map<String, ITEM> items;
}
