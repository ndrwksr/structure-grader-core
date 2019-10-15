package edu.kaiseran.structuregrader;

/**
 * Stores special keys used for templating operations, such as including the toString values of
 * expected and actual in Noncompliances.
 */
public class TemplateKeys {

  /**
   * The template key for Noncompliance.expected
   */
  public static final String EXPECTED_TEMPLATE = "%E";

  /**
   * The template key for Noncompliance.actual
   */
  public static final String ACTUAL_TEMPLATE = "%A";
}
