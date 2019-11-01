package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.visitors.CollectionVisitor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Data
@SuperBuilder
public abstract class CollectionSpec<ITEM, EXPECTED> implements CollectionVisitor<ITEM> {
	@NonNull
	protected final Consumer<Noncompliance> noncomplianceConsumer;

	@NonNull
	protected final String parentName;

	@NonNull
	protected final Set<EXPECTED> expected;
}
