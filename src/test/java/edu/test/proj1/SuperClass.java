package edu.test.proj1;

public class SuperClass implements Interface {
	private final Object superclassObj = new Object();

	@Override
	public void method(Object arg) {
		System.out.println(arg);
	}
}
