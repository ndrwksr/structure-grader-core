package edu.kaiseran.structuregrader.core.specification.common;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.TemplateKeys;
import edu.kaiseran.structuregrader.core.property.Typed;
import edu.kaiseran.structuregrader.core.visitor.ItemVisitor;
import edu.kaiseran.structuregrader.core.visitor.ItemVisitorFactory;
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
	@JsonIgnore
	private final Consumer<Noncompliance> noncomplianceConsumer;

	@Builder
	@JsonCreator
	public TypedSpec(
			@CheckForNull @JsonProperty("typeName") final String typeName,
			@CheckForNull @JsonProperty("genericTypeName") final String genericTypeName,
			@JsonProperty("useGenericTypeName") final boolean useGenericTypeName,
			@NonNull @JsonProperty("parentName") final String parentName,
			@NonNull @JacksonInject("noncomplianceConsumer") final Consumer<Noncompliance> noncomplianceConsumer
	) {
		this.typeName = typeName;
		this.genericTypeName = genericTypeName;
		this.useGenericTypeName = useGenericTypeName;
		this.parentName = parentName;
		this.noncomplianceConsumer = noncomplianceConsumer;
	}

	@Override
	public void visit(@CheckForNull final Typed typed) {
		if (typed != null) {
			if (!useGenericTypeName) {
				// Compare types
				//noinspection ConstantConditions -- typed.typeName can't be null, else useGenericTypeName would be true
				if (!typeName.equals(unlocateTypeString(typed.getType().getTypeName()))) {
					noncomplianceConsumer.accept(TypedNoncompliance.builder()
							.parentName(parentName)
							.expected(typeName)
							.actual(typed.getType().getTypeName())
							.explanation("Incorrect type! Expected " + typed.getName() + " to have type %E, but had type %A.")
							.build());
				}
			} else {
				// Compare generic types
				final Optional<String> expectedGenericTypeName = Optional.ofNullable(genericTypeName);
				final Optional<String> actualGenericTypeName = Optional.ofNullable(typed.getGenericType())
						.map(Type::getTypeName)
						.map(TypedSpec::unlocateTypeString);

				if (!expectedGenericTypeName.equals(actualGenericTypeName)) {
					noncomplianceConsumer.accept(TypedNoncompliance.builder()
							.parentName(parentName)
							.expected(genericTypeName)
							.actual(typed.getGenericType().getTypeName())
							.explanation("Incorrect type! Expected " + typed.getName() + " to have generic type %E, but had type %A.")
							.build());
				}
			}
		}
	}

	private static String unlocateTypeString(@NonNull final String rawTypeName) {
		// rawTypeName: "java.util.List<java.lang.String>"

		// ["java.util.List", "java.lang.String>"]
		final String[] splitRawName = rawTypeName.split("<");

		// ["java", "util", "List"]
		final String[] typeTokens = splitRawName[0].split("\\.");
		// "List"
		final String unlocatedType = typeTokens[typeTokens.length - 1];

		final String genericParam;
		if (splitRawName.length >= 2) {
			// ["java", "lang", "String>"]
			final String[] genericParamTokens = splitRawName[1].split("\\.");
			// "<String>"
			genericParam = '<' + genericParamTokens[genericParamTokens.length - 1];
		} else {
			genericParam = "";
		}

		// List<String>
		return unlocatedType + genericParam;
	}

	/**
	 * A Noncompliance for when a Typed has the wrong type.
	 */
	@SuperBuilder
	public static class TypedNoncompliance extends Noncompliance<String> {
		@Override
		public String getExplanation() {
			return explanation.replaceAll(
					TemplateKeys.EXPECTED_TEMPLATE,
					expected != null ? unlocateTypeString(expected) : "null"
			).replaceAll(
					TemplateKeys.ACTUAL_TEMPLATE,
					actual != null ? unlocateTypeString(actual) : "null"
			);
		}
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
				builder.typeName(unlocateTypeString(typed.getType().getTypeName()));
			} else {
				final String genericTypeName;
				if (genericType == null) {
					genericTypeName = null;
				} else {
					genericTypeName = unlocateTypeString(genericType.getTypeName());
				}

				builder.genericTypeName(genericTypeName);
			}

			return builder
					.parentName(parentName)
					.useGenericTypeName(useGenericTypeName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}
	}
}
