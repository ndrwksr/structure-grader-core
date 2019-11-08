package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.specifications.TypedSpec;
import edu.kaiseran.structuregrader.specifications.TypedSpec.TypedSpecFactory;
import edu.kaiseran.structuregrader.wrappers.FieldWrapper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TypedSpecTest {
	private static final String PARENT_NAME = "parent name";

	private static class TypedSpecTestClass {
		int intField;
		List untypedListField;
		List<Integer> typedListField;
		// TODO [ndrwksr | 11/7/19]: Augment test for MethodWrappers
//		int intMethod(){return 0;}
//		List untypedListMethod(){ return new ArrayList();}
//		List<Integer> typedListMethod(){return new ArrayList<>();}
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
	public void intVisitsIntFieldTest() throws NoSuchFieldException {
		FieldWrapper intWrapper = FieldWrapper.buildFrom(
				TypedSpecTestClass.class.getDeclaredField("intField")
		);

		TypedSpec typedSpec = TypedSpecFactory.getDefaultInst().buildFromItem(
				intWrapper,
				PARENT_NAME,
				noncomplianceConsumer
		);

		typedSpec.visit(intWrapper);

		assert noncompliances.isEmpty();
	}

	@Test
	public void untypedListVisitsUntypedListFieldTest()  throws NoSuchFieldException {
		FieldWrapper untypedListWrapper = FieldWrapper.buildFrom(
				TypedSpecTestClass.class.getDeclaredField("untypedListField")
		);

		TypedSpec typedSpec = TypedSpecFactory.getDefaultInst().buildFromItem(
				untypedListWrapper,
				PARENT_NAME,
				noncomplianceConsumer
		);

		typedSpec.visit(untypedListWrapper);

		assert noncompliances.isEmpty();
	}

	@Test
	public void typedListVisitsTypedListFieldTest() throws NoSuchFieldException {
		FieldWrapper typedListWrapper = FieldWrapper.buildFrom(
				TypedSpecTestClass.class.getDeclaredField("typedListField")
		);

		TypedSpec typedSpec = TypedSpecFactory.getDefaultInst().buildFromItem(
				typedListWrapper,
				PARENT_NAME,
				noncomplianceConsumer
		);

		typedSpec.visit(typedListWrapper);

		assert noncompliances.isEmpty();
	}

	@Test
	public void typedListVisitsUntypedListWithGenericFlagFalseFieldTest() throws NoSuchFieldException {
		FieldWrapper typedListWrapper = FieldWrapper.buildFrom(
				TypedSpecTestClass.class.getDeclaredField("typedListField")
		);

		final TypedSpecFactory<FieldWrapper> genericIgnoringTypeCheckFactory =
				TypedSpecFactory.<FieldWrapper>builder().useGenericTypeName(false).build();

		TypedSpec typedSpec = genericIgnoringTypeCheckFactory.buildFromItem(
				typedListWrapper,
				PARENT_NAME,
				noncomplianceConsumer
		);

		FieldWrapper untypedListWrapper = FieldWrapper.buildFrom(
				TypedSpecTestClass.class.getDeclaredField("untypedListField")
		);

		typedSpec.visit(untypedListWrapper);

		assert noncompliances.isEmpty();
	}

	@Test
	public void typedListVisitsUntypedListWithGenericFlagTrueFieldTest() throws NoSuchFieldException {
		FieldWrapper typedListWrapper = FieldWrapper.buildFrom(
				TypedSpecTestClass.class.getDeclaredField("typedListField")
		);

		TypedSpec typedSpec = TypedSpecFactory.getDefaultInst().buildFromItem(
				typedListWrapper,
				PARENT_NAME,
				noncomplianceConsumer
		);

		FieldWrapper untypedListWrapper = FieldWrapper.buildFrom(
				TypedSpecTestClass.class.getDeclaredField("untypedListField")
		);

		typedSpec.visit(untypedListWrapper);

		assert noncompliances.size() == 1;
	}

	@Test
	public void typedListVisitsIntFieldTest() throws NoSuchFieldException {
		FieldWrapper typedListWrapper = FieldWrapper.buildFrom(
				TypedSpecTestClass.class.getDeclaredField("typedListField")
		);

		TypedSpec typedSpec = TypedSpecFactory.getDefaultInst().buildFromItem(
				typedListWrapper,
				PARENT_NAME,
				noncomplianceConsumer
		);

		FieldWrapper intWrapper = FieldWrapper.buildFrom(
				TypedSpecTestClass.class.getDeclaredField("intField")
		);

		typedSpec.visit(intWrapper);

		assert noncompliances.size() == 1;
	}
}
