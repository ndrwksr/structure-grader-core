package edu.kaiseran.structuregrader.visitors;

import edu.kaiseran.structuregrader.NamedCollection;
import edu.kaiseran.structuregrader.Noncompliance;
import lombok.NonNull;

import java.util.function.Consumer;

public interface CollectionVisitorFactory<ITEM, VISITOR extends CollectionVisitor<ITEM> > {

	@NonNull
	VISITOR buildFromCollection(
			@NonNull final NamedCollection<ITEM> classCollection,
			@NonNull final String parentName,
			@NonNull final Consumer<Noncompliance> noncomplianceConsumer
	);
}
