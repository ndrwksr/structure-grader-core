package com.github.ndrwksr.structuregrader;

import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.specification.clazz.SuperclassSpec;
import com.github.ndrwksr.structuregrader.core.specification.clazz.SuperclassSpec.SuperclassSpecFactory;
import com.github.ndrwksr.structuregrader.core.wrapper.ClassWrapper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SuperclassSpecTest {
	private static final String TEST_NAME = "test name";

	private final SuperclassSpecFactory superclassSpecFactory = new SuperclassSpecFactory();

	private final ClassWrapper noSuperStructure = new ClassWrapper(
			edu.test.proj1.NoSuperClass.class
	);

	private final ClassWrapper subStructure = new ClassWrapper(
			edu.test.proj1.SubClass.class
	);

	private static class WrongSuperClass {
	}

	private static class SubClassOfWrongSuper extends WrongSuperClass {
	}

	@SuppressWarnings("ClassExplicitlyExtendsObject") // This class exists just to extend Object
	private static class ExplicitlyExtendsObject extends Object {
	}

	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};


	@Before
	public void setup() {
		noncompliances.clear();
	}

	@Test
	public void noSuperExpectedAndHadNone() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildFromItem(noSuperStructure, TEST_NAME, noncomplianceConsumer);
		superclassSpec.visit(noSuperStructure);
		assert noncompliances.size() == 0;
	}

	@Test
	public void noSuperExpectedAndExtendsObjectExplicitly() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildFromItem(noSuperStructure, TEST_NAME, noncomplianceConsumer);
		superclassSpec.visit(new ClassWrapper(ExplicitlyExtendsObject.class));
		assert noncompliances.size() == 0;
	}

	@Test
	public void noSuperExpectedButHadSuper() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildFromItem(noSuperStructure, TEST_NAME, noncomplianceConsumer);
		superclassSpec.visit(subStructure);
		assert noncompliances.size() == 1;
	}

	@Test
	public void superExpectedButHadNone() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildFromItem(subStructure, TEST_NAME, noncomplianceConsumer);
		superclassSpec.visit(noSuperStructure);
		assert noncompliances.size() == 1;
	}

	@Test
	public void superExpectedAndHadSuper() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildFromItem(subStructure, TEST_NAME, noncomplianceConsumer);
		superclassSpec.visit(subStructure);
		assert noncompliances.size() == 0;
	}

	@Test
	public void superExpectedButHadWrongSuper() {
		final SuperclassSpec superclassSpec = superclassSpecFactory.buildFromItem(subStructure, TEST_NAME, noncomplianceConsumer);
		superclassSpec.visit(new ClassWrapper(SubClassOfWrongSuper.class));
		assert noncompliances.size() == 1;
	}
}
