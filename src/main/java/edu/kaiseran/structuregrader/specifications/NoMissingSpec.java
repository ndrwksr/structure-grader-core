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
public class NoMissingSpec<ITEM extends Named> extends CollectionSpec<ITEM, String> {

	@Override
	public void visit(@CheckForNull final NamedCollection<ITEM> collection) {
		if (collection != null) {
			MissingExtraHelper.checkForMissing(
					collection.getName(),
					getExpected(),
					collection.getItems(),
					getNoncomplianceConsumer()
			);
		}
	}
}
