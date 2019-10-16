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

	@Before
	public void setup() {
		noncompliances.clear();
	}

	@Test
	public void noSuperExpectedAndHadNone() {

		final SuperclassSpec superclassSpec = superclassSpecFactory.buildVisitorFrom(noSuperStructure, noncomplianceConsumer);
		superclassSpec.visit(noSuperStructure);

		assert noncompliances.size() == 0;
	}

	@Test
	public void noSuperExpectedButHadSuper() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildVisitorFrom(noSuperStructure, noncomplianceConsumer);
		superclassSpec.visit(subStructure);

		assert noncompliances.size() == 1;

		final String explanation = noncompliances.get(0).getExplanation();
		assert !explanation.contains("Object");
		assert explanation.contains("to have no superclass");
		assert explanation.contains("but had SuperClass");
	}

	@Test
	public void superExpectedButHadNone() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildVisitorFrom(subStructure, noncomplianceConsumer);
		superclassSpec.visit(noSuperStructure);

		assert noncompliances.size() == 1;

		final String explanation = noncompliances.get(0).getExplanation();
		assert !explanation.contains("Object");
		assert explanation.contains("to have superclass SuperClass");
		assert explanation.contains("but had none");
	}

	@Test
	public void superExpectedAndHadSuper() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildVisitorFrom(subStructure, noncomplianceConsumer);
		superclassSpec.visit(subStructure);

		assert noncompliances.size() == 0;
	}

	@Test
	public void superExpectedButHadWrongSuper() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildVisitorFrom(subStructure, noncomplianceConsumer);

		class WrongSuperClass {
		}

		class SubClassOfWrongSuper extends WrongSuperClass {
		}

		superclassSpec.visit(ClassStructure.buildFrom(SubClassOfWrongSuper.class));

		assert noncompliances.size() == 1;

		final String explanation = noncompliances.get(0).getExplanation();
		assert !explanation.contains("Object");
		assert explanation.contains("to have superclass SuperClass");
		assert explanation.contains("but had WrongSuperClass");
	}

	// TODO [ndrwksr | 10/15/19]: Add tests for an object which explicitly extends Object
}
