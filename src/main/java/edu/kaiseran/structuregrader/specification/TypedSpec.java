package edu.kaiseran.structuregrader.specification;

import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.property.Typed;
import edu.kaiseran.structuregrader.visitor.ItemVisitor;
import edu.kaiseran.structuregrader.visitor.ItemVisitorFactory;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A specification for checking the type of an implementor of Typed. Can check just the normal type, or the generic
 * type.
 *
 * @param <TYPED> The type of the Typed implementor the specification is for.
 */
@Builder
@EqualsAndHashCode
public class TypedSpec<TYPED extends Typed> implements ItemVisitor<TYPED> {
	/**
	 * The simple name of the <b>non-generic</b> type being specified.
	 */
	@CheckForNull
	private final String typeName;

	/**
	 * The simple name of the <b>generic</b> type being specified.
	 */
	@CheckForNull
	private final String genericTypeName;

	/**
	 * True if genericTypeName should be used and the generic value of the visitee should be evaluated, false if the
	 * non-generic type name should be used instead.
	 */
	private final boolean useGenericTypeName;

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
	public void visit(@CheckForNull final Typed typed) {
		if (typed != null) {
			if (!useGenericTypeName) {
				// Compare types
				//noinspection ConstantConditions -- typed.typeName can't be null, else useGenericTypeName would be true
				if (!typeName.equals(typed.getType().getTypeName())) {
					noncomplianceConsumer.accept(TypedNoncompliance.builder()
							.parentName(parentName)
							.expected(typeName)
							.actual(typed.getType().getTypeName())
							.explanation("Expected " + typed.getName() + " to have type %E, but had type %A.")
							.build());
				}
			} else {
				// Compare generic types
				final Optional<String> expectedGenericTypeName = Optional.ofNullable(genericTypeName);
				final Optional<String> actualGenericTypeName = Optional.ofNullable(typed.getGenericType())
						.map(Type::getTypeName);

				if (!expectedGenericTypeName.equals(actualGenericTypeName)) {
					noncomplianceConsumer.accept(TypedNoncompliance.builder()
							.parentName(parentName)
							.expected(genericTypeName)
							.actual(typed.getGenericType().getTypeName())
							.explanation("Expected " + typed.getName() + " to have generic type %E, but had type %A.")
							.build());
				}
			}
		}
	}

	/**
	 * A Noncompliance for when a Typed has the wrong type.
	 */
	@SuperBuilder
	public static class TypedNoncompliance extends Noncompliance<String> {
	}


	/**
	 * A class for creating new TypedSpecs. Can be configured to produce TypedSpecs which consider or ignore generic type
	 * parameters.
	 *
	 * @param <TYPED> The type of the Typed implementor the product TypedSpecs will specify.
	 */
	@Builder
	public static class TypedSpecFactory<TYPED extends Typed> implements ItemVisitorFactory<TYPED, TypedSpec<TYPED>> {

		/**
		 * Whether or not product TypedSpecs should consider generic type parameters.
		 */
		@Builder.Default
		private boolean useGenericTypeName = true;

		/**
		 * Returns a new pre-configured instance for consumers of this type to use.
		 *
		 * @param <TYPED> The type of the Typed implementor the product TypedSpecs will specify.
		 * @return a new pre-configured instance for consumers of this type to use.
		 */
		public static <TYPED extends Typed> TypedSpecFactory<TYPED> getDefaultInst() {
			return TypedSpecFactory.<TYPED>builder().useGenericTypeName(true).build();
		}

		@Override
		public TypedSpec<TYPED> buildFromItem(
				@NonNull final TYPED typed,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Type genericType = typed.getGenericType();
			TypedSpec.TypedSpecBuilder<TYPED> builder = TypedSpec.builder();

			if (!useGenericTypeName) {
				builder.typeName(typed.getType().getTypeName());
			} else {
				builder.genericTypeName(genericType != null ? genericType.getTypeName() : null);
			}

			return builder
					.parentName(parentName)
					.useGenericTypeName(useGenericTypeName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}
	}
}
