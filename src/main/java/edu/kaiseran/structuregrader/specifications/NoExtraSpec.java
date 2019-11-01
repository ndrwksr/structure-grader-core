package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.Named;
import edu.kaiseran.structuregrader.NamedCollection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;

@EqualsAndHashCode(callSuper = true)
@Data
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
