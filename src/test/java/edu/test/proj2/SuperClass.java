package edu.test.proj2;

public class SuperClass implements Interface {
	private final Object superclassObj = new Object();

	@Override
	public void method(Object arg) {
		System.out.println(arg);
	}
}
