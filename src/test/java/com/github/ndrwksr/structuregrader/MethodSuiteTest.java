package com.github.ndrwksr.structuregrader;

import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.wrapper.MethodWrapper;
import com.github.ndrwksr.structuregrader.core.specification.collection.MissingExtraHelper;
import com.github.ndrwksr.structuregrader.core.specification.collection.OrderedListSpec;
import com.github.ndrwksr.structuregrader.core.specification.common.ModifiedSpec;
import com.github.ndrwksr.structuregrader.core.specification.common.TypedSpec;
import com.github.ndrwksr.structuregrader.core.specification.variable.VariableSuite.VariableSuiteFactory;
import edu.test.proj1.MethodClass;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MethodSuiteTest {
	private static final String PARENT_NAME = "parent name";

	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};

	public MethodSuiteTest() throws NoSuchMethodException {
	}

	@Before
	public void setup() {
		noncompliances.clear();
	}

	private boolean noncomplianceOfTypeWasMade(
			final Class<? extends Noncompliance> noncomplianceClass
	) {
		return noncompliances.stream().anyMatch(noncomplianceClass::isInstance);
	}

	private final VariableSuiteFactory<MethodWrapper> factory = VariableSuiteFactory.getDefaultMethodSuiteFactoryInst();
	private final MethodWrapper methodWrapper1 = new MethodWrapper(
			ParameterSuiteTest.ParamsTestObject.class.getDeclaredMethod(
					"objMethod1", Object.class, List.class, List.class, Object[].class));
	private final MethodWrapper methodWrapper2 = new MethodWrapper(
			ParameterSuiteTest.ParamsTestObject.class.getDeclaredMethod(
					"objMethod2", Object.class, List.class, List.class, Object[].class));
	private final MethodWrapper methodWrapper3 = new MethodWrapper(
			ParameterSuiteTest.ParamsTestObject.class.getDeclaredMethod(
					"intMethod1", int.class, int.class, int.class));
	private final MethodWrapper methodWrapper4 = new MethodWrapper(
			ParameterSuiteTest.ParamsTestObject.class.getDeclaredMethod(
					"intMethod4", int.class, int.class, int.class));
	private final MethodWrapper methodWrapper5 = new MethodWrapper(
			MethodClass.class.getDeclaredMethod("method3", List.class, Object.class, Map.class));

	@Test
	public void sameMethods() {
		factory.buildFromItem(methodWrapper1, PARENT_NAME, noncomplianceConsumer).visit(methodWrapper1);
		assert noncompliances.isEmpty();
	}

	@Test
	public void identicalMethods() {
		factory.buildFromItem(methodWrapper1, PARENT_NAME, noncomplianceConsumer).visit(methodWrapper2);
		assert noncompliances.isEmpty();
	}

	@Test
	public void differentMethods() {
		factory.buildFromItem(methodWrapper1, PARENT_NAME, noncomplianceConsumer).visit(methodWrapper5);
		assert noncomplianceOfTypeWasMade(ModifiedSpec.ModifiedNoncompliance.class);
		assert noncomplianceOfTypeWasMade(TypedSpec.TypedNoncompliance.class);
		assert noncomplianceOfTypeWasMade(MissingExtraHelper.ExtraNoncompliance.class);
		assert noncomplianceOfTypeWasMade(MissingExtraHelper.MissingNoncompliance.class);
	}

	@Test
	public void wrongParamOrder() {
		factory.buildFromItem(methodWrapper3, PARENT_NAME, noncomplianceConsumer).visit(methodWrapper4);
		assert noncomplianceOfTypeWasMade(OrderedListSpec.OrdinalNoncompliance.class);
	}
}
