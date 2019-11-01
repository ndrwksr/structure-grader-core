package edu.test.proj1;

import edu.test.annotations.TestAnnotation1;

public class SubClass extends SuperClass {
	@TestAnnotation1
	private final Object subclassObj = new Object();
}
