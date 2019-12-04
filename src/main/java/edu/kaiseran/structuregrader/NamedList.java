package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.property.Named;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * A list of type ITEM which has a name.
 *
 * @param <ITEM> The type of the items in the list.
 */
@Data
@Builder
public class NamedList<ITEM> implements Named {
	/**
	 * The name of the collection.
	 */
	@NonNull
	protected final String name;

	/**
	 * The items in the collection.
	 */
	@NonNull
	protected final List<ITEM> items;
}
