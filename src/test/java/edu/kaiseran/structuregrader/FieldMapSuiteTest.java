package edu.kaiseran.structuregrader;

import com.google.common.collect.Sets;
import edu.kaiseran.structuregrader.specification.variable.FieldMapSuite;
import edu.kaiseran.structuregrader.specification.variable.FieldMapSuite.FieldMapSuiteFactory;
import edu.kaiseran.structuregrader.specification.variable.FieldSuite;
import edu.kaiseran.structuregrader.specification.variable.VariableSuite;
import edu.kaiseran.structuregrader.visitor.ItemVisitor;
import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import edu.kaiseran.structuregrader.wrapper.FieldWrapper;
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
		final FieldSuite fieldSuite = fieldMapSuite.getItemSuites().entrySet().iterator().next().getValue();
		// Ensure that suite has specs in it
		assert fieldSuite.getSpecs().size() > 0;

		// There should be two collection specs (extra & missing)
		assert fieldMapSuite.getCollectionSpecs().size() == 2;
	}

	@Test
	public void testEmptyFieldMapSuiteFactory() {
		final VariableSuite.VariableSuiteFactory<FieldWrapper> emptyVariableSuiteFactory = new VariableSuite.VariableSuiteFactory<>(
				Sets.newHashSet()
		);
		final FieldSuite.FieldSuiteFactory emptyFieldSuiteFactory = new FieldSuite.FieldSuiteFactory(
				emptyVariableSuiteFactory,
				Sets.newHashSet()
		);
		final FieldMapSuiteFactory emptyFieldMapSuiteFactory = new FieldMapSuiteFactory(
				Sets.newHashSet(),
				emptyFieldSuiteFactory
		);

		final FieldMapSuite fieldMapSuite = emptyFieldMapSuiteFactory.buildFromItem(
				fieldClass,
				FieldClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		// There should be one field suite per field
		assert fieldMapSuite.getItemSuites().size() == fieldClass.getFields().getItems().size();
		// Get one of the FieldSuites, doesn't matter which one we get
		final FieldSuite fieldSuite = fieldMapSuite.getItemSuites().entrySet().iterator().next().getValue();
		// Get the VariableSuite within this suite (should be the only item)
		assert fieldSuite.getSpecs().size() == 1;
		final ItemVisitor<FieldWrapper> variableSuite = fieldSuite.getSpecs().iterator().next();
		assert variableSuite instanceof VariableSuite;
		// Make sure the VariableSuite has no specifications
		assert ((VariableSuite<FieldWrapper>) variableSuite).getSpecs().size() == 0;

		// There should be no collection specs
		assert fieldMapSuite.getCollectionSpecs().size() == 0;
	}
}
