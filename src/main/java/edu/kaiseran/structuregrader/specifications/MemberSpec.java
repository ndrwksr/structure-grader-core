package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.visitors.ItemVisitor;
import edu.kaiseran.structuregrader.visitors.ItemVisitorFactory;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.lang.reflect.Member;
import java.util.function.Consumer;

/**
 * A specification for Members, checks the modifiers of the specified Member against those which were expected.
 *
 * @param <MEMBER> The type of the Member being specified.
 */
@Builder
public class MemberSpec<MEMBER extends Member> implements ItemVisitor<MEMBER> {
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
	public void visit(@CheckForNull final Member member) {
		if (member != null) {
			final int actualModifiers = member.getModifiers();

			if (actualModifiers != expectedModifiers) {
				noncomplianceConsumer.accept(
						Noncompliance.builder()
								.parentName(member.getDeclaringClass().getSimpleName())
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
	 * @param <MEMBER> The type of the Member being specified.
	 */
	public static class MemberSpecFactory<MEMBER extends Member> implements ItemVisitorFactory<MEMBER, MemberSpec<MEMBER>> {

		/**
		 * Returns a pre-configured instance of MemberSpecFactories for consumers of this type.
		 *
		 * @param <MEMBER> The type of the Member being specified.
		 * @return
		 */
		public static <MEMBER extends Member> MemberSpecFactory<MEMBER> getDefaultInst() {
			return new MemberSpecFactory<>();
		}

		@Override
		public MemberSpec<MEMBER> buildFromItem(
				@NonNull final MEMBER member,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return MemberSpec.<MEMBER>builder()
					.expectedModifiers(member.getModifiers())
					.noncomplianceConsumer(noncomplianceConsumer)
					.parentName(parentName)
					.build();
		}
	}
}
