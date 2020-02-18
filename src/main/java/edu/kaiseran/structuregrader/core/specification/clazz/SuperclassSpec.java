package edu.kaiseran.structuregrader.core.specification.clazz;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.visitor.ClassVisitor;
import edu.kaiseran.structuregrader.core.visitor.ClassVisitorFactory;
import edu.kaiseran.structuregrader.core.wrapper.ClassWrapper;
import lombok.*;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Enforces that the name of specified class's superclass matches what is specified.
 */
@Builder(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Getter
public class SuperclassSpec implements ClassVisitor {
	/**
	 * The string to use in a noncompliance explanation when no superclass was found/expected.
	 */
	public static final String NO_SUPERCLASS = "no superclass";

	/**
	 * The expected name of the specified class's superclass.
	 */
	@Nullable
	private final String expectedSuperclassName;

	/**
	 * The name of the parent of the specified class.
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

	public SuperclassSpec(
			@Nullable @JsonProperty("expectedSuperclassName") final String expectedSuperclassName,
			@NonNull @JsonProperty("parentName") final String parentName,
			@NonNull @JacksonInject("noncomplianceConsumer") final Consumer<Noncompliance> noncomplianceConsumer
	) {
		this.expectedSuperclassName = expectedSuperclassName;
		this.parentName = parentName;
		this.noncomplianceConsumer = noncomplianceConsumer;
	}

	/**
	 * Returns the name of the Class. If the Class is Object, null is returned instead.
	 *
	 * @param clazz The Class whose name should be returned.
	 * @return the name of the Class. If the Class is Object, null is returned instead.
	 */
	private static String getNameFromSuperclass(@Nullable final Class clazz) {
		final String name;

		if (clazz == null) {
			name = null;
		} else {
			if (clazz.equals(Object.class)) {
				name = null;
			} else {
				name = clazz.getSimpleName();
			}
		}

		return name;
	}

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		if (classWrapper != null) {
			final String actualSuperclassName = getNameFromSuperclass(classWrapper.getSuperclass());

			if (!Objects.equals(expectedSuperclassName, actualSuperclassName)) {
				final String explanation = "Expected class " + classWrapper.getName() + " to have " +
						(expectedSuperclassName == null ? NO_SUPERCLASS : "superclass %E") + ", but had " +
						(actualSuperclassName == null ? NO_SUPERCLASS : "superclass %A.");

				noncomplianceConsumer.accept(
						Noncompliance.builder()
								.parentName(classWrapper.getName())
								.expected(expectedSuperclassName)
								.actual(actualSuperclassName)
								.explanation(explanation)
								.build()
				);
			}
		}
	}

	/**
	 * Factory for SuperclassSpec instances. Creates SuperclassSpecs which mandate that the super class of any visited
	 * ClassStructure instances has the same name as the ClassStructure instance the specification was created from.
	 * Has no state/configuration.
	 */
	public static class SuperclassSpecFactory implements ClassVisitorFactory<SuperclassSpec> {

		/**
		 * @return a pre-configured instance for consumers of SuperclassSpecFactory to use.
		 */
		public static SuperclassSpecFactory getDefaultInst() {
			return new SuperclassSpecFactory();
		}

		@Override
		public SuperclassSpec buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final String checkedSuperclassName = getNameFromSuperclass(classWrapper.getSuperclass());

			return SuperclassSpec.builder()
					.expectedSuperclassName(checkedSuperclassName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.parentName(parentName)
					.build();
		}
	}
}
