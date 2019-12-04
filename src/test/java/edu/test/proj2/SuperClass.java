package edu.test.proj2;

import edu.test.annotations.TestAnnotation1;
import edu.test.annotations.TestAnnotation2;

public class SuperClass implements Interface2 {
	@TestAnnotation1 @TestAnnotation2
	private final Object superclassObj = new Object();

	@TestAnnotation1
	@Override
	public void method(Object arg) {
		System.out.println(arg);
	}
}
