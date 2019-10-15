package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.ClassCollection;
import edu.kaiseran.structuregrader.ClassCollection.ClassCollectionVisitor;
import edu.kaiseran.structuregrader.ClassStructure;
import edu.kaiseran.structuregrader.Noncompliance;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * ClassCollectionSpecSuite is a composite of two sets of visitors, those for the visited collection
 * itself (classCollectionVisitors) and those for the classes that the collection contains
 * (classSpecSuites). When ClassCollectionSpecSuite visits a ClassCollection, it applies all of its
 * visitors to said ClassCollection and its composing members.
 */
@Data
@Builder
public class ClassCollectionSpecSuite implements ClassCollectionVisitor {

  /**
   * Stores specs for the entire collection of classes, such as NoExtraClassesSpec at the top level,
   * which would ensure that there are no extra top-level classes
   */
  @NonNull
  private final List<ClassCollectionVisitor> classCollectionVisitors;

  /**
   * Stores spec suites for the individual classes within the collection
   */
  @NonNull
  private final Map<String, ClassSpecSuite> classSpecSuites;

  /**
   * Factory method for ClassCollectionSpecSuite. Returns the strictest possible specification stack
   * where all structural components must match the provided ClassCollection exactly.
   *
   * @param classCollection the ClassCollection to base the specification stack off of.
   * @param noncomplianceConsumer the noncompliance consumer for the composing members of the
   * returned ClassCollectionSpecSuite.
   * @return the strictest possible specification stack for the provided ClassCollection.
   */
  public static ClassCollectionSpecSuite makeDefaultSuite(
      @NonNull final ClassCollection classCollection,
      @NonNull final Consumer<Noncompliance> noncomplianceConsumer
  ) {
    final ClassCollectionSpecSuiteBuilder builder = ClassCollectionSpecSuite.builder();
    final Map<String, ClassStructure> declaredClasses = classCollection.getDeclaredClasses();

    // Make the default spec suites for each class in the collection
    final Map<String, ClassSpecSuite> classSpecs = declaredClasses.entrySet().stream()
        .map(entry -> new AbstractMap.SimpleEntry<>(
            entry.getKey(),
            ClassSpecSuite.makeDefaultSuite(entry.getValue(), noncomplianceConsumer)
        ))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

    builder.classSpecSuites(classSpecs);

    final List<String> expectedClassNames = new ArrayList<>(declaredClasses.keySet());

    // Make sure that there are no missing/extra classes
    final List<ClassCollectionVisitor> classCollectionSpecs = Arrays.asList(
        NoMissingClassesSpec.builder()
            .expectedNames(expectedClassNames)
            .noncomplianceConsumer(noncomplianceConsumer)
            .build(),

        NoExtraClassesSpec.builder()
            .expectedNames(expectedClassNames)
            .noncomplianceConsumer(noncomplianceConsumer)
            .build()
    );

    builder.classCollectionVisitors(classCollectionSpecs);

    return builder.build();
  }

  @Override
  public void visit(@NonNull final ClassCollection classCollection) {
    classCollectionVisitors.forEach(spec -> spec.visit(classCollection));
  }
}
