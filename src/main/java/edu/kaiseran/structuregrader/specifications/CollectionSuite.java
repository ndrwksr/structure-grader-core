package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.visitors.CollectionVisitor;
import edu.kaiseran.structuregrader.visitors.ItemVisitor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@SuperBuilder
public abstract class CollectionSuite<ITEM, SUITE extends ItemVisitor<ITEM>> implements CollectionVisitor<ITEM> {
	@NonNull
	protected final List<CollectionVisitor<ITEM>> collectionSpecs;

	@NonNull
	protected final Map<String, SUITE> itemSuites;

	@NonNull
	protected final Function<ITEM, String> nameMapper;
}
