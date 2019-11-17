package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.specification.AnnotatedSuite;
import edu.kaiseran.structuregrader.specification.AnnotatedSuite.AnnotatedSuiteFactory;
import edu.kaiseran.structuregrader.wrapper.FieldWrapper;
import edu.test.annotations.TestAnnotation1;
import edu.test.annotations.TestAnnotation2;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AnnotatedSuiteTest {

	public static final String TEST_NAME = "test name";

	private static class AnnotationTestClass {
		private int noAnnotationField;

		@TestAnnotation1
		private int oneAnnotationField;

		@TestAnnotation1
		@TestAnnotation2
		private int manyAnnotationField;
	}

	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};

	private final AnnotatedSuiteFactory<FieldWrapper> factory = new AnnotatedSuiteFactory<>();

	private final List<FieldWrapper> testFields = Arrays.stream(AnnotationTestClass.class.getDeclaredFields())
			.map(FieldWrapper::new)
			.collect(Collectors.toList());

	private AnnotatedSuite<FieldWrapper> buildFromFieldAtIndex(final int index) {
		FieldWrapper testMethod = testFields.get(index);
		return factory.buildFromItem(testMethod, testMethod.getName(), noncomplianceConsumer);
	}

	@Before
	public void setup() {
		noncompliances.clear();
	}

	@Test
	public void noneWhereNoneExpected() {
		final AnnotatedSuite<FieldWrapper> suite = buildFromFieldAtIndex(0);
		suite.visit(testFields.get(0));
		assert noncompliances.isEmpty();
	}

	@Test
	public void oneWhereOneExpected() {
		final AnnotatedSuite<FieldWrapper> suite = buildFromFieldAtIndex(1);
		suite.visit(testFields.get(1));
		assert noncompliances.isEmpty();
	}

	@Test
	public void manyWhereManyExpected() {
		final AnnotatedSuite<FieldWrapper> suite = buildFromFieldAtIndex(2);
		suite.visit(testFields.get(2));
		assert noncompliances.isEmpty();
	}

	@Test
	public void oneWhereNoneExpected() {
		final AnnotatedSuite<FieldWrapper> suite = buildFromFieldAtIndex(0);
		suite.visit(testFields.get(1));
		assert noncompliances.size() == 1;
	}

	@Test
	public void manyWhereNoneExpected() {
		final AnnotatedSuite<FieldWrapper> suite = buildFromFieldAtIndex(0);
		suite.visit(testFields.get(2));
		assert noncompliances.size() == 1;
	}

	@Test
	public void noneWhereOneExpected() {
		final AnnotatedSuite<FieldWrapper> suite = buildFromFieldAtIndex(1);
		suite.visit(testFields.get(0));
		assert noncompliances.size() == 1;
	}

	@Test
	public void manyWhereOneExpected() {
		final AnnotatedSuite<FieldWrapper> suite = buildFromFieldAtIndex(1);
		suite.visit(testFields.get(2));
		assert noncompliances.size() == 1;
	}

	@Test
	public void noneWhereManyExpected() {
		final AnnotatedSuite<FieldWrapper> suite = buildFromFieldAtIndex(2);
		suite.visit(testFields.get(0));
		assert noncompliances.size() == 1;
	}

	@Test
	public void oneWhereManyExpected() {
		final AnnotatedSuite<FieldWrapper> suite = buildFromFieldAtIndex(2);
		suite.visit(testFields.get(1));
		assert noncompliances.size() == 1;
	}
}
