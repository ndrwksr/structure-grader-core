package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.specifications.AnnotatedElementSuite;
import edu.kaiseran.structuregrader.specifications.AnnotatedElementSuite.AnnotatedElementSuiteFactory;
import edu.test.annotations.TestAnnotation1;
import edu.test.annotations.TestAnnotation2;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class AnnotatedElementSuiteTest {

	public static final String TEST_NAME = "test name";

	private static class AnnotationTestClass {
		private void noAnnotationMethod() {
		}

		@TestAnnotation1
		private void oneAnnotationMethod() {
		}

		@TestAnnotation1
		@TestAnnotation2
		private void manyAnnotationMethod() {
		}
	}

	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(noncompliance);
	};

	private final AnnotatedElementSuiteFactory<Method> factory = new AnnotatedElementSuiteFactory<>();

	private final List<Method> testMethods = Arrays.asList(AnnotationTestClass.class.getDeclaredMethods());

	private AnnotatedElementSuite<Method> buildFromMethodAtIndex(final int index) {
		Method testMethod = testMethods.get(index);
		return factory.buildFromItem(testMethod, testMethod.getName(), noncomplianceConsumer);
	}

	@Before
	public void setup() {
		noncompliances.clear();
	}

	@Test
	public void noneWhereNoneExpected() {
		final AnnotatedElementSuite<Method> suite = buildFromMethodAtIndex(0);
		suite.visit(testMethods.get(0));
		assert noncompliances.isEmpty();
	}

	@Test
	public void oneWhereOneExpected() {
		final AnnotatedElementSuite<Method> suite = buildFromMethodAtIndex(1);
		suite.visit(testMethods.get(1));
		assert noncompliances.isEmpty();
	}

	@Test
	public void manyWhereManyExpected() {
		final AnnotatedElementSuite<Method> suite = buildFromMethodAtIndex(2);
		suite.visit(testMethods.get(2));
		assert noncompliances.isEmpty();
	}

	@Test
	public void oneWhereNoneExpected() {
		final AnnotatedElementSuite<Method> suite = buildFromMethodAtIndex(0);
		suite.visit(testMethods.get(1));
		assert noncompliances.size() == 1;
	}

	@Test
	public void manyWhereNoneExpected() {
		final AnnotatedElementSuite<Method> suite = buildFromMethodAtIndex(0);
		suite.visit(testMethods.get(2));
		assert noncompliances.size() == 1;
	}

	@Test
	public void noneWhereOneExpected() {
		final AnnotatedElementSuite<Method> suite = buildFromMethodAtIndex(1);
		suite.visit(testMethods.get(0));
		assert noncompliances.size() == 1;
	}

	@Test
	public void manyWhereOneExpected() {
		final AnnotatedElementSuite<Method> suite = buildFromMethodAtIndex(1);
		suite.visit(testMethods.get(2));
		assert noncompliances.size() == 1;
	}

	@Test
	public void noneWhereManyExpected() {
		final AnnotatedElementSuite<Method> suite = buildFromMethodAtIndex(2);
		suite.visit(testMethods.get(0));
		assert noncompliances.size() == 1;
	}

	@Test
	public void oneWhereManyExpected() {
		final AnnotatedElementSuite<Method> suite = buildFromMethodAtIndex(2);
		suite.visit(testMethods.get(1));
		assert noncompliances.size() == 1;
	}
}
