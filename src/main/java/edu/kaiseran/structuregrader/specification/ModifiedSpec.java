package edu.kaiseran.structuregrader.specification;

import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.property.Modified;
import edu.kaiseran.structuregrader.visitor.ItemVisitor;
import edu.kaiseran.structuregrader.visitor.ItemVisitorFactory;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.util.function.Consumer;

/**
 * A specification for Modified, checks the modifiers of the specified instance against those which were expected.
 *
 * @param <MODIFIED> The type of the Modified being specified.
 */
@Builder
@EqualsAndHashCode
public class ModifiedSpec<MODIFIED extends Modified> implements ItemVisitor<MODIFIED> {
	/**
	 * The expected value of the modifiers.
	 */
	private final int expectedModifiers;

	/**
	 * The name of the parent of the specified element.
	 */
	@NonNull
	@Getter
	private final String parentName;

	/**
	 * Accepts any generated noncompliances, decoupling the consumption of noncompliances from their
	 * creation.
	 */
	@NonNull
	private final Consumer<Noncompliance> noncomplianceConsumer;

	@Override
	public void visit(@CheckForNull final MODIFIED modified) {
		if (modified != null) {
			final int actualModifiers = modified.getModifiers();

			if (actualModifiers != expectedModifiers) {
				noncomplianceConsumer.accept(
						Noncompliance.builder()
								.parentName(modified.getName())
								.actual(actualModifiers)
								.expected(expectedModifiers)
								.explanation("Expected Member to have modifiers %E, but had %A")
								.build()
				);
			}
		}
	}

	/**
	 * Creates MemberSpecs from a Member where the modifiers must match those of member exactly.
	 *
	 * @param <MODIFIED> The type of the Member being specified.
	 */
	public static class ModifiedSpecFactory<MODIFIED extends Modified> implements ItemVisitorFactory<MODIFIED, ModifiedSpec<MODIFIED>> {

		/**
		 * @param <MODIFIED> The type of the Member being specified.
		 * @return a pre-configured instance of MemberSpecFactories for consumers of this type.
		 */
		public static <MODIFIED extends Modified> ModifiedSpecFactory<MODIFIED> getDefaultInst() {
			return new ModifiedSpecFactory<>();
		}

		@Override
		public ModifiedSpec<MODIFIED> buildFromItem(
				@NonNull final MODIFIED modified,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return ModifiedSpec.<MODIFIED>builder()
					.expectedModifiers(modified.getModifiers())
					.noncomplianceConsumer(noncomplianceConsumer)
					.parentName(parentName)
					.build();
		}
	}
}
