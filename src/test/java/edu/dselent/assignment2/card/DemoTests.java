package edu.dselent.assignment2.card;

import com.github.ndrwksr.structuregrader.core.specification.clazz.ClassMapSuite;
import com.github.ndrwksr.structuregrader.core.specification.clazz.ClassSuite;
import com.github.ndrwksr.structuregrader.core.specification.clazz.InterfaceSetSuite;
import com.github.ndrwksr.structuregrader.core.specification.clazz.SuperclassSpec;
import com.google.common.collect.ImmutableSet;
import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.SpecificationTester;
import com.github.ndrwksr.structuregrader.core.specification.collection.MissingExtraHelper;
import com.github.ndrwksr.structuregrader.core.specification.collection.NoExtraMapSpec;
import com.github.ndrwksr.structuregrader.core.specification.collection.NoMissingMapSpec;
import com.github.ndrwksr.structuregrader.core.specification.common.ModifiedSpec;
import com.github.ndrwksr.structuregrader.core.specification.common.TypedSpec;
import com.github.ndrwksr.structuregrader.core.specification.executable.ConstructorSetSuite;
import com.github.ndrwksr.structuregrader.core.specification.executable.MethodMapSuite;
import com.github.ndrwksr.structuregrader.core.specification.variable.FieldMapSuite;
import com.github.ndrwksr.structuregrader.core.specification.variable.VariableSuite;
import com.github.ndrwksr.structuregrader.core.wrapper.MethodWrapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DemoTests {
	public static final String PACKAGE_PREFIX = "edu.dselent.assignment2.card";
	private final ClassMapSuite.ClassMapSuiteFactory strictFactory = ClassMapSuite.ClassMapSuiteFactory.getDefaultInst();
	private ClassMapSuite.ClassMapSuiteFactory lenientFactory;

	private final List<Noncompliance> noncompliances = new ArrayList<>();
	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(noncompliance.getExplanation());
	};

	@Before
	public void setup() {
		noncompliances.clear();

		lenientFactory = ClassMapSuite.ClassMapSuiteFactory.builder()
				.collectionVisitorFactories(ImmutableSet.of(
						// Don't report extra classes
						NoMissingMapSpec.NoMissingMapSpecFactory.getDefaultInst("classes")
				))
				.itemSuiteFactory(
						ClassSuite.ClassSuiteFactory.builder()
								.classMapSuiteFactory(lenientFactory)
								.classVisitorFactories(ImmutableSet.of(
										// Don't report extra/missing classes
										SuperclassSpec.SuperclassSpecFactory.getDefaultInst(),
										// Don't report class-level annotations
										InterfaceSetSuite.InterfaceMapSuiteFactory.getDefaultInst(),
										ConstructorSetSuite.ConstructorSetSuiteFactory.getDefaultInst(),

										// Override default Method specification
										MethodMapSuite.MethodMapSuiteFactory.builder()
												.collectionVisitorFactories(ImmutableSet.of(
														// Don't report extra methods
														NoMissingMapSpec.NoMissingMapSpecFactory.getDefaultInst("methods")
												))
												.itemSuiteFactory(VariableSuite.VariableSuiteFactory.<MethodWrapper>builder()
														.visitorFactories(ImmutableSet.of(
																// Don't inspect annotations on methods
																ModifiedSpec.ModifiedSpecFactory.getDefaultInst(),
																TypedSpec.TypedSpecFactory.getDefaultInst()
																// Don't inspect method parameters
														)).build()).build(),

										// Override default Field specification
										FieldMapSuite.FieldMapSuiteFactory.builder()
												.collectionVisitorFactories(ImmutableSet.of(
														// Don't report missing fields
														NoExtraMapSpec.NoExtraSpecFactory.getDefaultInst("fields")
												)).build()
								))
								.build()
				)
				.build();
	}

	@Test
	public void testProfessorWithStrict() throws IOException {
		SpecificationTester.getNoncompliancesForStructures(
				PACKAGE_PREFIX + ".professor",
				PACKAGE_PREFIX + ".professor",
				noncomplianceConsumer,
				strictFactory
		);
		assert noncompliances.isEmpty();
	}

	@Test
	public void testPerfectWithStrict() throws IOException {
		SpecificationTester.getNoncompliancesForStructures(
				PACKAGE_PREFIX + ".professor",
				PACKAGE_PREFIX + ".student.perfect",
				noncomplianceConsumer,
				strictFactory
		);
		assert noncompliances.isEmpty();
	}

	@Test
	public void testBadWithStrict() throws IOException {
		SpecificationTester.getNoncompliancesForStructures(
				PACKAGE_PREFIX + ".professor",
				PACKAGE_PREFIX + ".student.bad",
				noncomplianceConsumer,
				strictFactory
		);
		assert !noncompliances.isEmpty();
	}

	@Test
	public void testBadWithLenientThenStrict() throws IOException {
		SpecificationTester.getNoncompliancesForStructures(
				PACKAGE_PREFIX + ".professor",
				PACKAGE_PREFIX + ".student.bad",
				noncomplianceConsumer,
				lenientFactory
		);
		final int lenientCount = noncompliances.size();
		final List<Noncompliance> lenientNoncompliances = List.copyOf(noncompliances);
		setup();

		SpecificationTester.getNoncompliancesForStructures(
				PACKAGE_PREFIX + ".professor",
				PACKAGE_PREFIX + ".student.bad",
				noncomplianceConsumer,
				strictFactory
		);
		final int strictCount = noncompliances.size();

		// Fewer noncompliances from lenient
		assert lenientCount < strictCount;

		// Strict noncompliance set is a superset of lenient noncompliance set
		assert noncompliances.containsAll(lenientNoncompliances);

		// Lenient noncompliance set did not have any noncompliances relating to missing annotations
		assert lenientNoncompliances.stream()
				.filter(MissingExtraHelper.MissingNoncompliance.class::isInstance)
				.noneMatch(noncompliance -> noncompliance.getExplanation().contains("annotation"));

		// Strict noncompliances did have at least one noncompliance relating to missing annotations
		assert noncompliances.stream()
				.filter(MissingExtraHelper.ExtraNoncompliance.class::isInstance)
				.anyMatch(noncompliance -> noncompliance.getExplanation().contains("annotation"));
	}
}
