package edu.kaiseran.structuregrader.visitor;

import edu.kaiseran.structuregrader.NamedSet;
import edu.kaiseran.structuregrader.Noncompliance;
import lombok.NonNull;

import java.util.function.Consumer;

/**
 * Makes new ListVisitors of type ITEM.
 *
 * @param <ITEM> The type of the NamedLists that implementors create visitors for.
 * @param <VISITOR> The type of the visitors created.
 */
public interface SetVisitorFactory<ITEM, VISITOR extends SetVisitor<ITEM>> {

	/**
	 * Returns a new VISITOR instance from the provided namedMap.
	 *
	 * @param namedSet The NamedList to build the visitor from
	 * @param parentName The name of the namedMap's parent element, used to provide context in Noncompliances.
	 * @param noncomplianceConsumer A consumer for any generated Noncompliances.
	 * @return a new VISITOR instance from the provided namedMap.
	 */
	@NonNull
	VISITOR buildFromCollection(
			@NonNull final NamedSet<ITEM> namedSet,
			@NonNull final String parentName,
			@NonNull final Consumer<Noncompliance> noncomplianceConsumer
	);
}
