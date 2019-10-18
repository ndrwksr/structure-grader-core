package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.ClassStructure;
import edu.kaiseran.structuregrader.ClassStructure.ClassVisitor;
import edu.kaiseran.structuregrader.Noncompliance;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Enforces that the name of visitee's superclass matches what is specified.
 */
@Data
@Builder(access = AccessLevel.PRIVATE)
public class SuperclassSpec implements ClassVisitor {
	public static final String NO_SUPERCLASS = "no superclass";

	/**
	 * Accepts any generated noncompliances, decoupling the consumption of noncompliances from their
	 * creation.
	 */
	@NonNull
	private final Consumer<Noncompliance> noncomplianceConsumer;

	/**
	 * The expected name of the visitee's superclass.
	 */
	@Nullable
	private final String expectedSuperclassName;

	// These warnings are promoting a structure which is substantially less readable.
	@SuppressWarnings({"StringConcatenationInsideStringBufferAppend", "StringBufferReplaceableByString"})
	@Override
	public void visitClass(@NonNull final ClassStructure classStructure) {
		final String actualSuperclassName = getNameFromSuperclass(classStructure.getSuperclass());

		if (!Objects.equals(expectedSuperclassName, actualSuperclassName)) {
			final StringBuilder stringBuilder = new StringBuilder();

			stringBuilder.append("Expected class " + classStructure.getName() + " to have ");
			stringBuilder.append(expectedSuperclassName == null ? NO_SUPERCLASS : "superclass %E");
			stringBuilder.append(", but had ");
			stringBuilder.append(actualSuperclassName == null ? NO_SUPERCLASS : "superclass %A.");

			getNoncomplianceConsumer().accept(
					Noncompliance.builder()
							.className(classStructure.getName())
							.expected(expectedSuperclassName)
							.actual(actualSuperclassName)
							.explanation(stringBuilder.toString())
							.build()
			);
		}
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

	/**
	 * Factory for SuperclassSpec instances. Creates SuperclassSpecs which mandate that the super class of any visited
	 * ClassStructure instances has the same name as the ClassStructure instance the specification was created from.
	 */
	public static class SuperclassSpecFactory implements ClassVisitorFactory<SuperclassSpec> {

		@Override
		public SuperclassSpec buildFromClass(
				@NonNull final ClassStructure classStructure,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final String checkedSuperclassName = getNameFromSuperclass(classStructure.getSuperclass());

			return SuperclassSpec.builder()
					.expectedSuperclassName(checkedSuperclassName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}
	}
}
