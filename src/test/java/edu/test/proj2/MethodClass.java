package edu.test.proj2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MethodClass {
	private static final void method1(final String myString){}
	private String method2(){return "String";}
	MethodClass method3(List<Integer> ints, final Object obj, final Map map){return this;}
	public static List<Integer> method4(int myInt, final MethodClass... methodClasses){return Arrays.asList(1, 2);}
}
