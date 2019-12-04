package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.property.Named;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Set;

/**
 * A set of type ITEM which has a name.
 *
 * @param <ITEM> The type of the items in the set.
 */
@Data
@Builder
public class NamedSet<ITEM> implements Named {
	/**
	 * The name of the collection.
	 */
	@NonNull
	protected final String name;

	/**
	 * The items in the collection.
	 */
	@NonNull
	protected final Set<ITEM> items;
}
