package edu.kaiseran.structuregrader.specification.collection;

import edu.kaiseran.structuregrader.NamedSet;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.visitor.SetVisitor;
import edu.kaiseran.structuregrader.visitor.SetVisitorFactory;
import lombok.Builder;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.util.Set;
import java.util.function.Consumer;

@Builder
public class NoMissingSetSpec<ITEM> implements SetVisitor<ITEM> {
	private final Set<ITEM> expectedValues;
	private final Consumer<Noncompliance> noncomplianceConsumer;

	@Override
	public void visit(@CheckForNull final NamedSet<ITEM> collection) {
		if (collection != null) {
			MissingExtraHelper.checkSetForMissing(
					collection.getName(),
					expectedValues,
					collection.getItems(),
					noncomplianceConsumer
			);
		}
	}

	public static class NoMissingSetSpecFactory<ITEM> implements SetVisitorFactory<ITEM, NoMissingSetSpec<ITEM>> {
		public static <ITEM> NoMissingSetSpec.NoMissingSetSpecFactory<ITEM> getDefaultInst() {
			return new NoMissingSetSpec.NoMissingSetSpecFactory<>();
		}

		@Override
		public NoMissingSetSpec<ITEM> buildFromCollection(
				@NonNull final NamedSet<ITEM> namedSet,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return NoMissingSetSpec.<ITEM>builder()
					.expectedValues(namedSet.getItems())
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}
	}
}
