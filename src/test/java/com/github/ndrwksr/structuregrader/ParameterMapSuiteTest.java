package com.github.ndrwksr.structuregrader;

import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.google.common.collect.ImmutableSet;
import com.github.ndrwksr.structuregrader.core.specification.collection.MissingExtraHelper;
import com.github.ndrwksr.structuregrader.core.specification.collection.NoExtraMapSpec;
import com.github.ndrwksr.structuregrader.core.specification.collection.NoMissingMapSpec;
import com.github.ndrwksr.structuregrader.core.specification.collection.OrderedListSpec;
import com.github.ndrwksr.structuregrader.core.specification.variable.ParameterMapSuite;
import com.github.ndrwksr.structuregrader.core.specification.variable.ParameterMapSuite.ParameterMapSuiteFactory;
import com.github.ndrwksr.structuregrader.core.specification.variable.VariableSuite;
import com.github.ndrwksr.structuregrader.core.wrapper.MethodWrapper;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ParameterMapSuiteTest {
	private static final String PARENT_NAME = "parent name";

	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};

	public ParameterMapSuiteTest() throws NoSuchMethodException {
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

	private final Method intMethod1 = ParameterSuiteTest.ParamsTestObject.class.getDeclaredMethod(
			"intMethod1", int.class, int.class, int.class
	);
	private final Method intMethod2 = ParameterSuiteTest.ParamsTestObject.class.getDeclaredMethod(
			"intMethod2", int.class, int.class, int.class
	);
	private final Method intMethod3 = ParameterSuiteTest.ParamsTestObject.class.getDeclaredMethod(
			"intMethod3", Object.class, int.class, int.class, int.class
	);
	private final Method intMethod4 = ParameterSuiteTest.ParamsTestObject.class.getDeclaredMethod(
			"intMethod4", int.class, int.class, int.class
	);

	private final ParameterMapSuiteFactory orderSpecificFactory = ParameterMapSuiteFactory.getDefaultInst();

	private final ParameterMapSuiteFactory orderAgnosticFactory = ParameterMapSuiteFactory.builder()
			.itemSuiteFactory(VariableSuite.VariableSuiteFactory.getDefaultInst())
			.collectionVisitorFactories(ImmutableSet.of(
					NoMissingMapSpec.NoMissingMapSpecFactory.getDefaultInst("parameters"),
					NoExtraMapSpec.NoExtraSpecFactory.getDefaultInst("parameters")
			))
			.build();

	@Test
	public void intSameMethod() {
		final MethodWrapper intWrapper = new MethodWrapper(intMethod1);
		final ParameterMapSuite mapSuite = orderSpecificFactory.buildFromItem(
				new MethodWrapper(intMethod1),
				PARENT_NAME,
				noncomplianceConsumer
		);

		mapSuite.visit(intWrapper);

		assert noncompliances.isEmpty();
	}

	@Test
	public void intIdenticalMethod() {
		final MethodWrapper intWrapper = new MethodWrapper(intMethod1);
		final ParameterMapSuite mapSuite = orderSpecificFactory.buildFromItem(
				new MethodWrapper(intMethod2),
				PARENT_NAME,
				noncomplianceConsumer
		);

		mapSuite.visit(intWrapper);

		assert noncompliances.isEmpty();
	}

	@Test
	public void intSameOrder_OrderSpecific() {
		final MethodWrapper intWrapper1 = new MethodWrapper(intMethod1);
		final MethodWrapper intWrapper2 = new MethodWrapper(intMethod2);
		orderSpecificFactory.buildFromItem(intWrapper1, PARENT_NAME, noncomplianceConsumer).visit(intWrapper2);
		assert noncompliances.isEmpty();
	}

	@Test
	public void intSameOrder_OrderAgnostic() {
		final MethodWrapper intWrapper1 = new MethodWrapper(intMethod1);
		final MethodWrapper intWrapper2 = new MethodWrapper(intMethod2);
		orderAgnosticFactory.buildFromItem(intWrapper1, PARENT_NAME, noncomplianceConsumer).visit(intWrapper2);
		assert noncompliances.isEmpty();
	}

	@Test
	public void intDifferingOrder_OrderSpecific() {
		final MethodWrapper intWrapper1 = new MethodWrapper(intMethod1);
		final MethodWrapper intWrapper4 = new MethodWrapper(intMethod4);
		orderSpecificFactory.buildFromItem(intWrapper1, PARENT_NAME, noncomplianceConsumer).visit(intWrapper4);
		assert !noncompliances.isEmpty();
		assert noncomplianceOfTypeWasMade(OrderedListSpec.OrdinalNoncompliance.class);
	}

	@Test
	public void intDifferingOrder_OrderAgnostic() {
		final MethodWrapper intWrapper1 = new MethodWrapper(intMethod1);
		final MethodWrapper intWrapper4 = new MethodWrapper(intMethod4);
		orderAgnosticFactory.buildFromItem(intWrapper1, PARENT_NAME, noncomplianceConsumer).visit(intWrapper4);
		assert noncompliances.isEmpty();
	}

	@Test
	public void intExtraParam() {
		final MethodWrapper intWrapper1 = new MethodWrapper(intMethod1);
		final MethodWrapper intWrapper3 = new MethodWrapper(intMethod3);
		orderAgnosticFactory.buildFromItem(intWrapper1, PARENT_NAME, noncomplianceConsumer).visit(intWrapper3);
		assert !noncompliances.isEmpty();
		assert noncomplianceOfTypeWasMade(MissingExtraHelper.ExtraNoncompliance.class);
	}
}
