package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.specification.clazz.AnnotatedClassSuite;
import edu.kaiseran.structuregrader.specification.clazz.AnnotatedClassSuite.AnnotatedClassSuiteFactory;
import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import edu.test.proj1.Interface1;
import edu.test.proj1.NoSuperClass;
import edu.test.proj1.SubClass;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotatedClassSuiteTest {
	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};

	private final AnnotatedClassSuiteFactory factory = new AnnotatedClassSuiteFactory(true, true);

	private final List<ClassWrapper> testClassWrappers = Stream.of(SubClass.class, Interface1.class, NoSuperClass.class)
			.map(ClassWrapper::new)
			.collect(Collectors.toList());

	private AnnotatedClassSuite buildFromClassWrapperAtIndex(final int index) {
		ClassWrapper testClassWrapper = testClassWrappers.get(index);
		return factory.buildFromItem(testClassWrapper, testClassWrapper.getName(), noncomplianceConsumer);
	}

	@Before
	public void setup() {
		noncompliances.clear();
	}

	@Test
	public void noneWhereNoneExpected() {
		final AnnotatedClassSuite suite = buildFromClassWrapperAtIndex(0);
		suite.visit(testClassWrappers.get(0));
		assert noncompliances.isEmpty();
	}

	@Test
	public void oneWhereOneExpected() {
		final AnnotatedClassSuite suite = buildFromClassWrapperAtIndex(1);
		suite.visit(testClassWrappers.get(1));
		assert noncompliances.isEmpty();
	}

	@Test
	public void manyWhereManyExpected() {
		final AnnotatedClassSuite suite = buildFromClassWrapperAtIndex(2);
		suite.visit(testClassWrappers.get(2));
		assert noncompliances.isEmpty();
	}

	@Test
	public void oneWhereNoneExpected() {
		final AnnotatedClassSuite suite = buildFromClassWrapperAtIndex(0);
		suite.visit(testClassWrappers.get(1));
		assert noncompliances.size() == 1;
	}

	@Test
	public void manyWhereNoneExpected() {
		final AnnotatedClassSuite suite = buildFromClassWrapperAtIndex(0);
		suite.visit(testClassWrappers.get(2));
		assert noncompliances.size() == 1;
	}

	@Test
	public void noneWhereOneExpected() {
		final AnnotatedClassSuite suite = buildFromClassWrapperAtIndex(1);
		suite.visit(testClassWrappers.get(0));
		assert noncompliances.size() == 1;
	}

	@Test
	public void manyWhereOneExpected() {
		final AnnotatedClassSuite suite = buildFromClassWrapperAtIndex(1);
		suite.visit(testClassWrappers.get(2));
		assert noncompliances.size() == 1;
	}

	@Test
	public void noneWhereManyExpected() {
		final AnnotatedClassSuite suite = buildFromClassWrapperAtIndex(2);
		suite.visit(testClassWrappers.get(0));
		assert noncompliances.size() == 1;
	}

	@Test
	public void oneWhereManyExpected() {
		final AnnotatedClassSuite suite = buildFromClassWrapperAtIndex(2);
		suite.visit(testClassWrappers.get(1));
		assert noncompliances.size() == 1;
	}
}
