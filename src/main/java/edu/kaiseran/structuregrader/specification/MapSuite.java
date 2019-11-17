package edu.kaiseran.structuregrader.specification;

import edu.kaiseran.structuregrader.visitor.ItemVisitor;
import edu.kaiseran.structuregrader.visitor.MapVisitor;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Abstract class for a suite specifying a collection of items. Has both specifications for the collection as a whole
 * (such as NoExtraSpec and NoMissingSpec) and suites for each of the items in the collection.
 *
 * @param <ITEM> The type of the item in the collection.
 * @param <SUITE> The type of the suite of inheritors of CollectionSuite.
 */

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@SuperBuilder
public abstract class MapSuite<ITEM, SUITE extends ItemVisitor<ITEM>> implements MapVisitor<ITEM> {

	/**
	 * The specifications to be applied to the collection as a whole.
	 */
	@NonNull
	protected final Set<MapVisitor<ITEM>> collectionSpecs;

	/**
	 * The suites for the individual items in the collection.
	 */
	@NonNull
	protected final Map<String, SUITE> itemSuites;

	/**
	 * The name of the parent of the specified element.
	 */
	@NonNull
	@Getter
	protected final String parentName;

	/**
	 * Returns the names of the items in the collection. Used where ITEM can't be made to implement Named.
	 */
	@NonNull
	protected final Function<ITEM, String> nameMapper;
}
