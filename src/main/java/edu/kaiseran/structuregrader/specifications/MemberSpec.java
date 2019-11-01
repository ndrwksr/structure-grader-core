package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.visitors.ItemVisitor;
import edu.kaiseran.structuregrader.visitors.ItemVisitorFactory;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.lang.reflect.Member;
import java.util.function.Consumer;

/**
 * A specification for Members, checks the modifiers of the specified Member against those which were expected.
 */
@Builder
public class MemberSpec implements ItemVisitor<Member> {
	/**
	 * The expected value of the modifiers.
	 */
	private final int expectedModifiers;

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
	 */
	public static class MemberSpecFactory implements ItemVisitorFactory<Member, MemberSpec> {

		@Override
		public MemberSpec buildFromItem(
				@NonNull final Member member,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return MemberSpec.builder()
					.expectedModifiers(member.getModifiers())
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}
	}
}
