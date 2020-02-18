package edu.kaiseran.structuregrader.core.specification.common;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.property.Modified;
import edu.kaiseran.structuregrader.core.visitor.ItemVisitor;
import edu.kaiseran.structuregrader.core.visitor.ItemVisitorFactory;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import java.util.function.Consumer;

/**
 * A specification for Modified, checks the modifiers of the specified instance against those which were expected.
 *
 * @param <MODIFIED> The type of the Modified being specified.
 */
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
	@JsonIgnore
	private final Consumer<Noncompliance> noncomplianceConsumer;

	@Builder
	public ModifiedSpec(
			@JsonProperty("expectedModifiers") final int expectedModifiers,
			@NonNull @JsonProperty("parentName") final String parentName,
			@NonNull @JacksonInject("noncomplianceConsumer") final Consumer<Noncompliance> noncomplianceConsumer
	) {
		this.expectedModifiers = expectedModifiers;
		this.parentName = parentName;
		this.noncomplianceConsumer = noncomplianceConsumer;
	}

	@Override
	public void visit(@CheckForNull final MODIFIED modified) {
		if (modified != null) {
			final int actualModifiers = modified.getModifiers();

			if (actualModifiers != expectedModifiers) {
				noncomplianceConsumer.accept(
						ModifiedNoncompliance.builder()
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
	 * Has no state/configuration.
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

	/**
	 * A Noncompliance for when a Modified has the wrong modifiers.
	 */
	@SuperBuilder
	public static class ModifiedNoncompliance extends Noncompliance<Integer> {

	}
}
