package com.github.ndrwksr.structuregrader.core;

import com.github.ndrwksr.structuregrader.core.property.Named;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * A map from element names to the actual items, which itself has a name.
 *
 * @param <ITEM> The type of the items in the collection.
 */
@Data
@SuperBuilder
public class NamedMap<ITEM> implements Named {
	/**
	 * The name of the collection.
	 */
	@NonNull
	protected final String name;

	/**
	 * The items in the collection.
	 */
	@NonNull
	protected final Map<String, ITEM> items;
}
