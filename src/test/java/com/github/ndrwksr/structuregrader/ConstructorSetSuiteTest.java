package com.github.ndrwksr.structuregrader;

import com.github.ndrwksr.structuregrader.core.NamedSet;
import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.specification.executable.ConstructorSetSuite.ConstructorSetSuiteFactory;
import com.github.ndrwksr.structuregrader.core.wrapper.ClassWrapper;
import com.github.ndrwksr.structuregrader.core.wrapper.ConstructorWrapper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConstructorSetSuiteTest {
	private static final String PARENT_NAME = "parent name";
	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};

	@Before
	public void setup() {
		noncompliances.clear();
	}

	private final ConstructorSetSuiteFactory factory = ConstructorSetSuiteFactory.getDefaultInst();

	private static class ConstructorTest {
		ConstructorTest(){}
	}

	private final NamedSet<ConstructorWrapper> easyConstructors1 =
			new ClassWrapper(edu.test.proj1.MethodClass.class).getConstructors();
	private final NamedSet<ConstructorWrapper> easyConstructors2 =
			new ClassWrapper(edu.test.proj2.MethodClass.class).getConstructors();
	private final NamedSet<ConstructorWrapper> complexConstructors1 =
			new ClassWrapper(edu.test.proj1.ConstructorClass.class).getConstructors();
	private final NamedSet<ConstructorWrapper> complexConstructors2 =
			new ClassWrapper(edu.test.proj2.ConstructorClass.class).getConstructors();
	private final NamedSet<ConstructorWrapper> mismatchConstructor =
			new ClassWrapper(ConstructorTest.class).getConstructors();

	@Test
	public void testEasySame() {
		factory.buildFromCollection(easyConstructors1, PARENT_NAME, noncomplianceConsumer).visit(easyConstructors1);
		assert noncompliances.isEmpty();
	}

	@Test
	public void testEasyIdentical() {
		factory.buildFromCollection(easyConstructors1, PARENT_NAME, noncomplianceConsumer).visit(easyConstructors2);
		assert noncompliances.isEmpty();
	}

	@Test
	public void testComplexSame() {
		factory.buildFromCollection(complexConstructors1, PARENT_NAME, noncomplianceConsumer).visit(complexConstructors1);
		assert noncompliances.isEmpty();
	}

	@Test
	public void testComplexIdentical() {
		factory.buildFromCollection(complexConstructors1, PARENT_NAME, noncomplianceConsumer).visit(complexConstructors2);
		assert noncompliances.isEmpty();
	}

	@Test
	public void testEasyWrongModifier() {
		factory.buildFromCollection(easyConstructors1, PARENT_NAME, noncomplianceConsumer).visit(mismatchConstructor);
		assert !noncompliances.isEmpty();
	}

	@Test
	public void testComplexMismatch() {
		factory.buildFromCollection(easyConstructors1, PARENT_NAME, noncomplianceConsumer).visit(complexConstructors1);
		assert !noncompliances.isEmpty();
	}
}
