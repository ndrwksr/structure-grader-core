package edu.kaiseran.structuregrader.core.visitor;

import edu.kaiseran.structuregrader.core.NamedMap;
import edu.kaiseran.structuregrader.core.Noncompliance;
import lombok.NonNull;

import java.util.function.Consumer;

/**
 * A factory of CollectionVisitors.
 *
 * @param <ITEM> The type of the items in the collection that will be visited.
 * @param <VISITOR> The type of the visitor the factory will produce
 */
public interface MapVisitorFactory<ITEM, VISITOR extends MapVisitor<ITEM>> {

	/**
	 * Returns a new VISITOR instance from the provided namedMap.
	 *
	 * @param namedMap The namedMap to build the visitor from
	 * @param parentName The name of the namedMap's parent element, used to provide context in Noncompliances.
	 * @param noncomplianceConsumer A consumer for any generated Noncompliances.
	 * @return a new VISITOR instance from the provided namedMap.
	 */
	@NonNull
	VISITOR buildFromCollection(
			@NonNull final NamedMap<ITEM> namedMap,
			@NonNull final String parentName,
			@NonNull final Consumer<Noncompliance> noncomplianceConsumer
	);
}
