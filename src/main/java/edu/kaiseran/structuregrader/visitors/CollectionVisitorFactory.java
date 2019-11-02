package edu.kaiseran.structuregrader.visitors;

import edu.kaiseran.structuregrader.NamedCollection;
import edu.kaiseran.structuregrader.Noncompliance;
import lombok.NonNull;

import java.util.function.Consumer;

/**
 * A factory of CollectionVisitors.
 *
 * @param <ITEM> The type of the items in the collection that will be visited.
 * @param <VISITOR> The type of the visitor the factory will produce
 */
public interface CollectionVisitorFactory<ITEM, VISITOR extends CollectionVisitor<ITEM> > {

	/**
	 * Returns a new VISITOR instance from the provided collection.
	 *
	 * @param collection The collection to build the visitor from
	 * @param parentName The name of the collection's parent element, used to provide context in Noncompliances.
	 * @param noncomplianceConsumer A consumer for any generated Noncompliances.
	 * @return a new VISITOR instance from the provided collection.
	 */
	@NonNull
	VISITOR buildFromCollection(
			@NonNull final NamedCollection<ITEM> collection,
			@NonNull final String parentName,
			@NonNull final Consumer<Noncompliance> noncomplianceConsumer
	);
}
