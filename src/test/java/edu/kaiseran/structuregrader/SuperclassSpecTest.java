package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.specifications.SuperclassSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;

public class SuperclassSpecTest {
  private static class Superclass {

  }

  private static class Subclass extends Superclass {

  }

  private static class NormalClass {

  }

  private final List<Noncompliance> noncompliances = new ArrayList<>();
  private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
    noncompliances.add(noncompliance);
    System.out.println(noncompliance);
  };

  @Before
  public void setup() {
    noncompliances.clear();
  }

  @Test
  public void noSuperExpectedAndHadNone() {
    final ClassStructure classStructure = ClassStructure.buildFrom(NormalClass.class);

    final SuperclassSpec superclassSpec = SuperclassSpec.builder()
        .expectedSuperclassName(null)
        .noncomplianceConsumer(noncomplianceConsumer)
        .build();
  }

}
