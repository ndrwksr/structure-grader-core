package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.ClassStructure;
import edu.kaiseran.structuregrader.ClassStructure.ClassVisitor;
import edu.kaiseran.structuregrader.Noncompliance;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Enforces that the name of visitee's superclass matches what is specified.
 */
@Data
@Builder(access = AccessLevel.PRIVATE)
public class SuperclassSpec implements ClassVisitor {
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

	@Override
	public void visit(@NonNull final ClassStructure classStructure) {
		final Optional<String> expectedSuperclassName = Optional.ofNullable(this.expectedSuperclassName);
		final Optional<String> actualSuperclassName = Optional.ofNullable(classStructure.getSuperclass())
				.map(Class::getSimpleName)
				.map(SuperclassSpec::nullifyStringIfObject); // This doesn't nullify the Optional, it nullifies the inner value

		// TODO [ndrwksr | 10/15/19]: Refactor to condense construction of Noncompliances

		if (!expectedSuperclassName.isPresent() && actualSuperclassName.isPresent()) {
			// Only actual is present, expected was null

			getNoncomplianceConsumer().accept(
					Noncompliance.builder()
							.className(classStructure.getName())
							.expected(null)
							.actual(actualSuperclassName.get())
							.explanation("Expected class to have no superclass, but had %A")
							.build()
			);
		} else if (expectedSuperclassName.isPresent() && !actualSuperclassName.isPresent()) {
			// Only expected is present, actual was null

			getNoncomplianceConsumer().accept(
					Noncompliance.builder()
							.className(classStructure.getName())
							.expected(expectedSuperclassName.get())
							.actual(null)
							.explanation("Expected class to have superclass %E, but had none")
							.build()
			);
		} else if (expectedSuperclassName.isPresent()) {
			// Both are present. We didn't have to check actual, because if it wasn't present then
			// the second condition would have been true.

			if (!expectedSuperclassName.get().equals(actualSuperclassName.get())) {
				// The names of the superclasses weren't equal

				getNoncomplianceConsumer().accept(Noncompliance.builder()
						.className(classStructure.getName())
						.expected(expectedSuperclassName.get())
						.actual(actualSuperclassName.get())
						.explanation("Expected class to have superclass %E, but had %A")
						.build()
				);
			}
		}
	}

	/**
	 * Returns null if string is non-null and equals "Object". Used to ensure that the name of a class which extends only
	 * from Object is mapped to null (as extending from Object is equivalent to having no superclass).
	 *
	 * @param string The String to check for equality with "Object".
	 * @return null if string is non-null and equals "Object".
	 */
	private static String nullifyStringIfObject(@Nullable final String string) {
		return string != null && string.equals("Object") ? null : string;
	}

	/**
	 * Factory for SuperclassSpec instances. Creates SuperclassSpecs which mandate that the super class of any visited
	 * ClassStructure instances has the same name as the ClassStructure instance the specification was created from.
	 */
	public static class SuperclassSpecFactory implements ClassVisitorFactory<SuperclassSpec> {

		@Override
		public SuperclassSpec buildVisitorFrom(
				@NonNull final ClassStructure classStructure,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			// Get the super class name from superclass directly if present, else get it from superclassName
			final String uncheckedSuperclassName = classStructure.getSuperclass() != null ?
					classStructure.getSuperclass().getSimpleName() :
					classStructure.getSuperclassName();

			final String checkedSuperclassName = nullifyStringIfObject(uncheckedSuperclassName);

			return SuperclassSpec.builder()
					.expectedSuperclassName(checkedSuperclassName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}
	}
}
