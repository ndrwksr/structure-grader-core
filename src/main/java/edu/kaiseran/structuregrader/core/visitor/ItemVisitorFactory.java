package edu.kaiseran.structuregrader.core.visitor;

import edu.kaiseran.structuregrader.core.Noncompliance;
import lombok.NonNull;

import java.util.function.Consumer;


/**
 * A factory for ItemVisitors.
 *
 * @param <ITEM> The type of item that the visitor will visit.
 * @param <VISITOR> The type of the visitor the factory will produce.
 */
public interface ItemVisitorFactory<ITEM, VISITOR extends ItemVisitor<ITEM>> {

	/**
	 * Returns a new VISITOR instance from the provided item.
	 *
	 * @param item The item to build the visitor from
	 * @param parentName The name of the collection's parent element, used to provide context in Noncompliances.
	 * @param noncomplianceConsumer A consumer for any generated Noncompliances.
	 * @return a new VISITOR instance from the provided item.
	 */
	VISITOR buildFromItem(
			@NonNull final ITEM item,
			@NonNull final String parentName,
			@NonNull final Consumer<Noncompliance> noncomplianceConsumer
	);
}
