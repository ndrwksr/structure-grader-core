package edu.test.proj2;

import edu.test.annotations.TestAnnotation1;
import edu.test.annotations.TestAnnotation2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MethodClass {
	@TestAnnotation1
	@TestAnnotation2
	private static final void method1(@TestAnnotation1 final String myString) {
	}

	private String method2() {
		return "String";
	}

	@TestAnnotation1
	MethodClass method3(List<Integer> ints, final Object obj, final Map map) {
		return this;
	}

	public static List<Integer> method4(int myInt, @TestAnnotation2 final MethodClass... methodClasses) {
		return Arrays.asList(1, 2);
	}
}
