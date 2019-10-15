package edu.kaiseran.structuregrader;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * A collection of ClassStructure objects with its own name. Can be visited by any
 * ClassCollectionVisitors.
 */
@Data
@Builder
public class ClassCollection {

  /**
   * The name of the parent of the collection. If this collection is part of a class structure, then
   * name will be the name of the declaring class. If this collection represents a package's
   * contents, then name will be the name of the package.
   */
  @NonNull
  private final String name;

  /**
   * The classes contained within the collection. The key of declaredClasses is the name of the
   * class the entry contains.
   */
  @NonNull
  private final Map<String, ClassStructure> declaredClasses;

  /**
   * Factory method. Returns a ClassCollection containing all top-level classes in the provided package.
   *
   * @param pkg The name of the package to build the ClassCollection from.
   * @return a ClassCollection containing all top-level classes in the provided package.
   * @throws IOException If ClassPath cannot access the top level classes of pkg.
   */
  @SuppressWarnings({"UnstableApiUsage", "WeakerAccess"})
  // ClassPath is marked as @Beta, so it's fine for applications
  public static ClassCollection buildFrom(
      @NonNull final String pkg
  ) throws IOException {
    final ClassPath classPath = ClassPath.from(ClassCollection.class.getClassLoader());

    // Get a map of all of the classes in pkg, where the key is the class's name and the value is
    // a ClassStructure representing the class.
    final Map<String, ClassStructure> declaredClasses =
        classPath.getTopLevelClassesRecursive(pkg).stream()
            .map(ClassInfo::load)
            .map(ClassStructure::buildFrom)
            .collect(Collectors.toMap(
                ClassStructure::getName, Function.identity())
            );

    return ClassCollection.builder()
        .name(pkg)
        .declaredClasses(declaredClasses)
        .build();
  }

  /**
   * Accepts a ClassCollectionVisitor.
   *
   * @param classCollectionVisitor The visitor to accept.
   */
  public void accept(@NonNull final ClassCollectionVisitor classCollectionVisitor) {
    classCollectionVisitor.visit(this);
  }

  /**
   * Provides a way for specifications to gather information about the ClassCollection under
   * evaluation.
   */
  public interface ClassCollectionVisitor {

    /**
     * Visits the provided ClassCollection.
     * @param classCollection The ClassCollection to visit.
     */
    void visit(@NonNull final ClassCollection classCollection);
  }
}
