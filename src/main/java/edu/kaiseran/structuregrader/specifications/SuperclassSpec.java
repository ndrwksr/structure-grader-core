package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.ClassStructure;
import edu.kaiseran.structuregrader.ClassStructure.ClassVisitor;
import edu.kaiseran.structuregrader.Noncompliance;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * Enforces that the name of visitee's superclass matches what is specified.
 */
@Data
@Builder
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
    final Optional<String> expectedSuperclassName =
        Optional.ofNullable(this.expectedSuperclassName);
    final Optional<String> actualSuperclassName =
        Optional.ofNullable(classStructure.getSuperclass())
            .map(Class::getSimpleName);

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
              .expected(expectedSuperclassName)
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
}
