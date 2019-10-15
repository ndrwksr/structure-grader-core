package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.ClassStructure;
import edu.kaiseran.structuregrader.ClassStructure.ClassVisitor;
import edu.kaiseran.structuregrader.Noncompliance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * ClassSpecSuite is a composite of a set of specifications for a class, and a
 * ClassCollectionSpecSuite for said class's inner classes. When a ClassSpecSuite visits a class, it
 * applies all of its class visitors to said class, and applies its ClassCollectionSpecSuite to the
 * class's inner classes.
 */
@Data
@Builder
public class ClassSpecSuite implements ClassVisitor {

  /**
   * Stores specs for the class being visited.
   */
  @NonNull
  private final List<ClassVisitor> declaringClassSpecs;

  /**
   * Stores specs for the visited class's declared classes.
   */
  @NonNull
  private final ClassCollectionSpecSuite collectionSpecSuite;

  /**
   * Factory method for ClassSpecSuite. Returns the strictest possible class specification where all
   * structural components must match the provided ClassStructure exactly.
   *
   * @param classStructure the ClassStructure to base the ClassSpecSuite off of.
   * @param noncomplianceConsumer the noncompliance consumer for the composing members of the
   *     returned ClassSpecSuite.
   * @return the strictest possible class specification for the provided ClassStructure.
   */
  @SuppressWarnings("WeakerAccess")
  public static ClassSpecSuite makeDefaultSuite(
      @NonNull final ClassStructure classStructure,
      @NonNull final Consumer<Noncompliance> noncomplianceConsumer
  ) {
    final ClassSpecSuiteBuilder builder = ClassSpecSuite.builder();
    final Map<String, ClassStructure> declaredClasses = classStructure.getDeclaredClasses();
    final List<String> declaredClassNames = new ArrayList<>(declaredClasses.keySet());
    final Class superclass = classStructure.getSuperclass();

    // Make the default specs for the visited class
    final List<ClassVisitor> visitedClassSpecs = Arrays.asList(
        // TODO: Refactor to put construction of specs behind an interface which each
        //  spec must implement
        SuperclassSpec.builder()
            .expectedSuperclassName(superclass != null ? superclass.getSimpleName() : null)
            .noncomplianceConsumer(noncomplianceConsumer)
            .build(),
        NoMissingClassesSpec.builder()
            .expectedNames(declaredClassNames)
            .noncomplianceConsumer(noncomplianceConsumer)
            .build(),
        NoExtraClassesSpec.builder()
            .expectedNames(declaredClassNames)
            .noncomplianceConsumer(noncomplianceConsumer)
            .build()
    );

    builder.declaringClassSpecs(visitedClassSpecs);

    // Make the default spec suite for the declared classes of the visited class
    final ClassCollectionSpecSuite collectionSpecSuite = ClassCollectionSpecSuite.makeDefaultSuite(
        classStructure.getClassCollection(),
        noncomplianceConsumer
    );

    builder.collectionSpecSuite(collectionSpecSuite);

    return builder.build();
  }

  @Override
  public void visit(@NonNull final ClassStructure classStructure) {
    declaringClassSpecs.forEach(spec -> spec.visit(classStructure));
    collectionSpecSuite.visit(classStructure.getClassCollection());
  }
}
