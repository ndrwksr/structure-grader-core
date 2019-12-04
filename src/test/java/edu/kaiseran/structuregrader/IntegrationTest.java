package edu.kaiseran.structuregrader;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class IntegrationTest {
	private final String PKG1 = "edu.test.proj1";
	private final String PKG2 = "edu.test.proj2";

	@Before
	public void setup() {
		noncompliances.clear();
	}

	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};

	@Test
	public void testSame() throws IOException {
		final List<Noncompliance> noncompliances = SpecificationTester.getNoncompliancesForStructures(
				PKG1,
				PKG1,
				noncomplianceConsumer
		);
		assert noncompliances.size() == 0;
	}

	@Test
	public void testIdentical() throws IOException {
		final List<Noncompliance> noncompliances = SpecificationTester.getNoncompliancesForStructures(
				PKG1,
				PKG2,
				noncomplianceConsumer
		);
		noncompliances.forEach(System.out::println);
		assert noncompliances.size() == 0;
	}
}
