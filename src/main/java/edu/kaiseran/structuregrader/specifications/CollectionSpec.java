package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.visitors.CollectionVisitor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Abstract class which stores a Noncompliance consumer, the name of the parent item of this spec, and the set of
 * expected values for the collection. This set is often the set of names of the elements which should be present.
 *
 * @param <ITEM> The type of the item in the specified collection.
 * @param <EXPECTED> The type of the expected values in the collection, usually String.
 */
@Data
@SuperBuilder
public abstract class CollectionSpec<ITEM, EXPECTED> implements CollectionVisitor<ITEM> {
	/**
	 * Accepts any generated noncompliances, decoupling the consumption of noncompliances from their
	 * creation.
	 */
	@NonNull
	protected final Consumer<Noncompliance> noncomplianceConsumer;

	/**
	 * The name of the parent of the specified element.
	 */
	@NonNull
	protected final String parentName;

	/**
	 * The set of expected values for the collection.
	 */
	@NonNull
	protected final Set<EXPECTED> expected;
}
