package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.visitors.CollectionVisitor;
import edu.kaiseran.structuregrader.visitors.ItemVisitor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Abstract class for a suite specifying a collection of items. Has both specifications for the collection as a whole
 * (such as NoExtraSpec and NoMissingSpec) and suites for each of the items in the collection.
 *
 * @param <ITEM> The type of the item in the collection.
 * @param <SUITE> The type of the suite of inheritors of CollectionSuite.
 */
@SuperBuilder
public abstract class CollectionSuite<ITEM, SUITE extends ItemVisitor<ITEM>> implements CollectionVisitor<ITEM> {
	/**
	 * The specifications to be applied to the collection as a whole.
	 */
	@NonNull
	protected final List<CollectionVisitor<ITEM>> collectionSpecs;

	/**
	 * The suites for the individual items in the collection.
	 */
	@NonNull
	protected final Map<String, SUITE> itemSuites;

	/**
	 * The name of the collection this suite specifies.
	 */
	@NonNull
	protected final String parentName;

	/**
	 * Returns the names of the items in the collection. Used where ITEM can't be made to implement Named.
	 */
	@NonNull
	protected final Function<ITEM, String> nameMapper;
}
