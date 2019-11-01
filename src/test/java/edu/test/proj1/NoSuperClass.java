package edu.test.proj1;

import edu.test.annotations.TestAnnotation2;

public class NoSuperClass {
	@TestAnnotation2
	private static final String CONST = "const";

	public String getConst() {
		return CONST;
	}
}
