package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.specifications.SuperclassSpec;
import edu.kaiseran.structuregrader.specifications.SuperclassSpec.SuperclassSpecFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SuperclassSpecTest {

	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(noncompliance);
	};

	private final SuperclassSpecFactory superclassSpecFactory = new SuperclassSpecFactory();

	private final ClassStructure noSuperStructure = ClassStructure.buildFrom(
			edu.test.proj1.NoSuperClass.class
	);

	private final ClassStructure subStructure = ClassStructure.buildFrom(
			edu.test.proj1.SubClass.class
	);

	private static class WrongSuperClass {
	}

	private static class SubClassOfWrongSuper extends WrongSuperClass {
	}

	@SuppressWarnings("ClassExplicitlyExtendsObject") // This class exists just to extend Object
	private static class ExplicitlyExtendsObject extends Object {
	}

	@Before
	public void setup() {
		noncompliances.clear();
	}

	@Test
	public void noSuperExpectedAndHadNone() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildFromClass(noSuperStructure, noncomplianceConsumer);
		superclassSpec.visitClass(noSuperStructure);
		assert noncompliances.size() == 0;
	}

	@Test
	public void noSuperExpectedAndExtendsObjectExplicitly() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildFromClass(noSuperStructure, noncomplianceConsumer);
		superclassSpec.visitClass(ClassStructure.buildFrom(ExplicitlyExtendsObject.class));
		assert noncompliances.size() == 0;
	}

	@Test
	public void noSuperExpectedButHadSuper() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildFromClass(noSuperStructure, noncomplianceConsumer);
		superclassSpec.visitClass(subStructure);
		assert noncompliances.size() == 1;
	}

	@Test
	public void superExpectedButHadNone() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildFromClass(subStructure, noncomplianceConsumer);
		superclassSpec.visitClass(noSuperStructure);
		assert noncompliances.size() == 1;
	}

	@Test
	public void superExpectedAndHadSuper() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildFromClass(subStructure, noncomplianceConsumer);
		superclassSpec.visitClass(subStructure);
		assert noncompliances.size() == 0;
	}

	@Test
	public void superExpectedButHadWrongSuper() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildFromClass(subStructure, noncomplianceConsumer);
		superclassSpec.visitClass(ClassStructure.buildFrom(SubClassOfWrongSuper.class));
		assert noncompliances.size() == 1;
	}
}
