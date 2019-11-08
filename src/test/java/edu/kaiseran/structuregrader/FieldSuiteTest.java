package edu.kaiseran.structuregrader;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.kaiseran.structuregrader.specifications.FieldSuite;
import edu.kaiseran.structuregrader.specifications.FieldSuite.FieldSuiteFactory;
import edu.kaiseran.structuregrader.wrappers.FieldWrapper;
import edu.test.proj1.FieldClass;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class FieldSuiteTest {
	private static final String PARENT_NAME = "parent name";

	private final Field field1_1 = edu.test.proj1.FieldClass.class.getDeclaredField("field1");
	private final Field field1_2 = edu.test.proj1.FieldClass.class.getDeclaredField("field2");
	private final Field field1_3 = edu.test.proj1.FieldClass.class.getDeclaredField("field3");
	private final Field field1_4 = edu.test.proj1.FieldClass.class.getDeclaredField("field4");
	private final Field field1_5 = edu.test.proj1.FieldClass.class.getDeclaredField("field5");

	private final Field field2_1 = edu.test.proj1.FieldClass.class.getDeclaredField("field1");
	private final Field field2_2 = edu.test.proj1.FieldClass.class.getDeclaredField("field2");
	private final Field field2_3 = edu.test.proj1.FieldClass.class.getDeclaredField("field3");
	private final Field field2_4 = edu.test.proj1.FieldClass.class.getDeclaredField("field4");
	private final Field field2_5 = edu.test.proj1.FieldClass.class.getDeclaredField("field5");

	private final List<Field> fields_1 = Arrays.asList(field1_1, field1_2, field1_3, field1_4, field1_5);
	private final List<Field> fields_2 = Arrays.asList(field2_1, field2_2, field2_3, field2_4, field2_5);
	

	public FieldSuiteTest() throws NoSuchFieldException {
	}

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
	public void arraySuitesFailTest() {
		for (int i = 0; i < fields_1.size(); i++) {
			runArrayTestIteration(i, (i + 1) % fields_1.size(), true );
		}
	}

	@Test
	public void arraySuitesPassTest() {
		for (int i = 0; i < fields_1.size(); i++) {
			runArrayTestIteration(i, i, false);
		}
	}

	private void runArrayTestIteration(final int fields1Index, final int fields2Index, final boolean shouldFail) {
		final FieldSuiteFactory factory = FieldSuiteFactory.getDefaultInst();
		final FieldSuite fieldSuite = factory.buildFromItem(
				FieldWrapper.buildFrom(fields_1.get(fields1Index)),
				PARENT_NAME,
				noncomplianceConsumer
		);

		final FieldWrapper fieldWrapper = FieldWrapper.buildFrom(fields_2.get(fields2Index));
		fieldSuite.visit(fieldWrapper);
		assert shouldFail ? noncompliances.size() >= 1 : noncompliances.isEmpty();

		setup();
	}
}
