package edu.kaiseran.structuregrader.visitors;

import edu.kaiseran.structuregrader.Noncompliance;
import lombok.NonNull;

import java.util.function.Consumer;


public interface ItemVisitorFactory<ITEM, VISITOR extends ItemVisitor<ITEM>> {

	VISITOR buildFromItem(
			@NonNull final ITEM item,
			@NonNull final String parentName,
			@NonNull final Consumer<Noncompliance> noncomplianceConsumer
	);
}
