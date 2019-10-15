package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.specifications.ClassCollectionSpecSuite;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.NonNull;

public class SpecificationTester {

  public static List<Noncompliance> getNoncompliancesForStructures(
      @NonNull final String expectedPkg,
      @NonNull final String actualPkg
  ) throws IOException {
    final ClassCollection expectedCollection = ClassCollection.buildFrom(expectedPkg);
    final ClassCollection actualCollection = ClassCollection.buildFrom(actualPkg);

    final List<Noncompliance> noncompliances = new ArrayList<>();
    final Consumer<Noncompliance> noncomplianceConsumer = noncompliances::add;

    final ClassCollectionSpecSuite specSuite =
        ClassCollectionSpecSuite.makeDefaultSuite(expectedCollection, noncomplianceConsumer);
    actualCollection.accept(specSuite);

    return noncompliances;
  }
}
