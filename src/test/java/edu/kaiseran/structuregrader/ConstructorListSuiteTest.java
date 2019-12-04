package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.specification.executable.ConstructorListSuite.ConstructorListSuiteFactory;
import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import edu.kaiseran.structuregrader.wrapper.ConstructorWrapper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConstructorListSuiteTest {
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

	private final ConstructorListSuiteFactory factory = ConstructorListSuiteFactory.getDefaultInst();

	private static class ConstructorTest {
		ConstructorTest(){}
	}

	private final NamedList<ConstructorWrapper> easyConstructors1 =
			new ClassWrapper(edu.test.proj1.MethodClass.class).getConstructors();
	private final NamedList<ConstructorWrapper> easyConstructors2 =
			new ClassWrapper(edu.test.proj2.MethodClass.class).getConstructors();
	private final NamedList<ConstructorWrapper> complexConstructors1 =
			new ClassWrapper(edu.test.proj1.ConstructorClass.class).getConstructors();
	private final NamedList<ConstructorWrapper> complexConstructors2 =
			new ClassWrapper(edu.test.proj2.ConstructorClass.class).getConstructors();
	private final NamedList<ConstructorWrapper> mismatchConstructor =
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
