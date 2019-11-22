package edu.test.proj1;

import edu.test.annotations.TestAnnotation1;
import edu.test.annotations.TestAnnotation2;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MethodClass {
	private static final void method1(
			@NonNull final String myString
	) {
	}

	private String method2() {
		return "String";
	}

	MethodClass method3(
			List<Integer> ints,
			@NonNull final Object obj,
			@TestAnnotation1 final Map map
	) {
		return this;
	}

	public static List<Integer> method4(
			@TestAnnotation2 int myInt,
			final MethodClass... methodClasses
	) {
		return Arrays.asList(1, 2);
	}
}
