package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.ClassCollection;
import edu.kaiseran.structuregrader.ClassCollection.ClassCollectionVisitor;
import edu.kaiseran.structuregrader.ClassStructure;
import edu.kaiseran.structuregrader.ClassStructure.ClassVisitor;
import edu.kaiseran.structuregrader.Noncompliance;
import java.util.List;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * Enforces that the visitee declares all of the classes specified. Can visit classes (which will
 * result in visiting the class's ClassCollection) or ClassCollections directly.
 */
@Data
@Builder
public class NoMissingClassesSpec implements ClassVisitor, ClassCollectionVisitor {

  /**
   * Accepts any generated noncompliances, decoupling the consumption of noncompliances from their
   * creation.
   */
  @NonNull
  private final Consumer<Noncompliance> noncomplianceConsumer;

  /**
   * The names of the classes which are allowed to exist.
   */
  @NonNull
  private final List<String> expectedNames;

  @Override
  public void visit(@NonNull final ClassCollection classCollection) {
    MissingExtraHelper.checkForMissing(
        classCollection.getName(),
        expectedNames, classCollection.getDeclaredClasses(),
        noncomplianceConsumer
    );
  }

  @Override
  public void visit(@NonNull final ClassStructure classStructure) {
    visit(classStructure.getClassCollection());
  }
}
