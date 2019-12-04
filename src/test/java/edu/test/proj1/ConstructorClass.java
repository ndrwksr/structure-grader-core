package edu.test.proj1;

import java.util.List;

public class ConstructorClass {
	private final String firstField;
	private final int secondField;
	private final List<String> thirdField;

	public ConstructorClass() {
		this.firstField = null;
		this.secondField = 0;
		this.thirdField = null;
	}

	private ConstructorClass(final String firstField, final int secondField, final List<String> thirdField) {
		this.firstField = firstField;
		this.secondField = secondField;
		this.thirdField = thirdField;
	}

	protected ConstructorClass(final String firstField, final List<String> thirdField) {
		this.firstField = firstField;
		this.secondField = 0;
		this.thirdField = thirdField;
	}

	ConstructorClass(final String firstField, final int secondField) {
		this.firstField = firstField;
		this.secondField = secondField;
		this.thirdField = null;
	}
}
