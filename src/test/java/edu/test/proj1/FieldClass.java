package edu.test.proj1;

import edu.test.annotations.TestAnnotation1;
import edu.test.annotations.TestAnnotation2;
import lombok.Data;

@Data
public class FieldClass {
	@TestAnnotation1
	private static final Object field1 = new Object();

	static String field2;

	@TestAnnotation1
	@TestAnnotation2
	private final int field3;

	public static Character field4;

	final boolean field5;
}
