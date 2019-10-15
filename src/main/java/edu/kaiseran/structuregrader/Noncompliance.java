package edu.kaiseran.structuregrader;

import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * Analogous to an Exception, and is constructed by a specification any time said specification
 * detects that the structure it is analyzing does not conform to the specification's expectation.
 *
 * @param <T> The type of expected and actual.
 */
@Data
@Builder
public class Noncompliance<T> {

  /**
   * The name of the parent object the noncompliance is for, used to provide additional context.
   */
  @NonNull
  private final String className;

  /**
   * The value that the constructing specification expected to find, and the actual value it found.
   */
  @Nullable
  private final T expected, actual;

  /**
   * A human-readable explanation of what went wrong. In order to access the values of expected and
   * actual, the template keys EXPECTED_TEMPLATE and ACTUAL_TEMPLATE from the TemplateKeys class
   * should be used wherever expected or actual are desired. This templating operation is performed
   * during construction.
   */
  @NonNull
  private final String explanation;

  private Noncompliance(
      @NonNull final String className,
      @Nullable final T expected,
      @Nullable final T actual,
      @NonNull final String explanation
  ) {
    this.className = className;
    this.expected = expected;
    this.actual = actual;

    String templatedExplanation = explanation;


    if (expected != null) {
      // Replace EXPECTED_TEMPLATE with expected.toString() if expected is non-null,
      templatedExplanation = templatedExplanation.replaceAll(
          TemplateKeys.EXPECTED_TEMPLATE,
          expected.toString()
      );
    } else {
      // Else replace EXPECTED_TEMPLATE with "null"
      templatedExplanation = templatedExplanation.replaceAll(
          TemplateKeys.EXPECTED_TEMPLATE,
          "null"
      );
    }

    if (actual != null) {
      // Replace ACTUAL_TEMPLATE with actual.toString() if actual is non-null,
      templatedExplanation = templatedExplanation.replaceAll(
          TemplateKeys.ACTUAL_TEMPLATE,
          actual.toString()
      );
    } else {
      // Else replace ACTUAL_TEMPLATE with "null"
      templatedExplanation = templatedExplanation.replaceAll(
          TemplateKeys.ACTUAL_TEMPLATE,
          "null"
      );
    }

    this.explanation = templatedExplanation;
  }
}
