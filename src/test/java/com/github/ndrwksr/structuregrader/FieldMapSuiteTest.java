package com.github.ndrwksr.structuregrader;

import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.specification.variable.FieldMapSuite;
import com.github.ndrwksr.structuregrader.core.specification.variable.FieldMapSuite.FieldMapSuiteFactory;
import com.github.ndrwksr.structuregrader.core.specification.variable.VariableSuite;
import com.github.ndrwksr.structuregrader.core.wrapper.ClassWrapper;
import com.github.ndrwksr.structuregrader.core.wrapper.FieldWrapper;
import edu.test.proj1.FieldClass;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FieldMapSuiteTest {
	private final ClassWrapper fieldClass = new ClassWrapper(FieldClass.class);
	private final FieldMapSuiteFactory factory = FieldMapSuiteFactory.getDefaultInst();

	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};

	@Before
	public void setup() {
		noncompliances.clear();
	}

	@Test
	public void testFieldMapSuiteVisitsCorrectData() {
		final FieldMapSuite fieldMapSuite = factory.buildFromItem(
				fieldClass,
				FieldClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		fieldMapSuite.visit(fieldClass);

		assert noncompliances.isEmpty();
	}

	@Test
	public void testFieldMapSuiteVisitsIncorrectData() {
		final FieldMapSuite fieldMapSuite = factory.buildFromItem(
				fieldClass,
				FieldClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		fieldMapSuite.visit(new ClassWrapper(ClassWrapper.class));

		assert noncompliances.size() == 2;
	}

	@Test
	public void testDefaultFieldMapSuiteFactory() {

		final FieldMapSuite fieldMapSuite = factory.buildFromItem(
				fieldClass,
				FieldClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		// There should be one field suite per field
		assert fieldMapSuite.getItemSuites().size() == fieldClass.getFields().getItems().size();

		// Get one of the FieldSuites, doesn't matter which one we get
		final VariableSuite<FieldWrapper> fieldSuite = fieldMapSuite.getItemSuites().entrySet().iterator().next().getValue();
		// Ensure that suite has specs in it
		assert fieldSuite.getSpecs().size() > 0;

		// There should be two collection specs (extra & missing)
		assert fieldMapSuite.getCollectionSpecs().size() == 2;
	}
}
