package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.Named;
import edu.kaiseran.structuregrader.NamedCollection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;

/**
 * Specifies that no extra items in a collection may exist.
 *
 * @param <ITEM> The type of the items in the specified collection.
 */
@SuperBuilder
public class NoExtraSpec<ITEM extends Named> extends CollectionSpec<ITEM, String> {

	@Override
	public void visit(@CheckForNull final NamedCollection<ITEM> collection) {
		if (collection != null) {
			MissingExtraHelper.checkForExtra(
					collection.getName(),
					getExpected(),
					collection.getItems(),
					getNoncomplianceConsumer()
			);
		}
	}
}
