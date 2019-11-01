package edu.kaiseran.structuregrader;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestClassWrapper {
	private final String PKG1 = "edu.test.proj1";
	private final String PKG2 = "edu.test.proj2";

	@Test
	public void testSimpleSameStructureComparison() throws IOException {
		final List<Noncompliance> noncompliances = SpecificationTester.getNoncompliancesForStructures(PKG1, PKG1);
		noncompliances.forEach(System.out::println);
		assert noncompliances.size() == 0;
	}

	@Test
	public void testSimpleEqualStructureComparison() throws IOException {
		final List<Noncompliance> noncompliances = SpecificationTester.getNoncompliancesForStructures(PKG1, PKG2);
		noncompliances.forEach(System.out::println);
		assert noncompliances.size() == 0;
	}
}
