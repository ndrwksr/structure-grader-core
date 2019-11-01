package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.wrappers.ClassWrapper;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.visitors.ClassVisitor;
import edu.kaiseran.structuregrader.visitors.ClassVisitorFactory;
import lombok.*;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Enforces that the name of visitee's superclass matches what is specified.
 */
@Builder(access = AccessLevel.PRIVATE)
public class SuperclassSpec implements ClassVisitor {
	public static final String NO_SUPERCLASS = "no superclass";

	/**
	 * Accepts any generated noncompliances, decoupling the consumption of noncompliances from their
	 * creation.
	 */
	@Getter
	@NonNull
	private final Consumer<Noncompliance> noncomplianceConsumer;

	/**
	 * The expected name of the visitee's superclass.
	 */
	@Nullable
	private final String expectedSuperclassName;

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

	// These warnings are promoting a structure which is substantially less readable, and thus have been suppressed.
	@SuppressWarnings({"StringConcatenationInsideStringBufferAppend", "StringBufferReplaceableByString"})
	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		if (classWrapper != null) {
			final String actualSuperclassName = getNameFromSuperclass(classWrapper.getSuperclass());

			if (!Objects.equals(expectedSuperclassName, actualSuperclassName)) {
				final StringBuilder stringBuilder = new StringBuilder();

				stringBuilder.append("Expected class " + classWrapper.getName() + " to have ");
				stringBuilder.append(expectedSuperclassName == null ? NO_SUPERCLASS : "superclass %E");
				stringBuilder.append(", but had ");
				stringBuilder.append(actualSuperclassName == null ? NO_SUPERCLASS : "superclass %A.");

				getNoncomplianceConsumer().accept(
						Noncompliance.builder()
								.parentName(classWrapper.getName())
								.expected(expectedSuperclassName)
								.actual(actualSuperclassName)
								.explanation(stringBuilder.toString())
								.build()
				);
			}
		}
	}

	/**
	 * Factory for SuperclassSpec instances. Creates SuperclassSpecs which mandate that the super class of any visited
	 * ClassStructure instances has the same name as the ClassStructure instance the specification was created from.
	 */
	public static class SuperclassSpecFactory implements ClassVisitorFactory<SuperclassSpec> {

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
					.build();
		}
	}
}
