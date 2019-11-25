package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.property.Named;
import edu.kaiseran.structuregrader.specification.ModifiedSpec;
import edu.kaiseran.structuregrader.specification.TypedSpec;
import edu.kaiseran.structuregrader.specification.quantity.NoExtraSpec;
import edu.kaiseran.structuregrader.specification.quantity.NoMissingSpec;
import edu.kaiseran.structuregrader.specification.variable.VariableSuite;
import edu.kaiseran.structuregrader.specification.variable.VariableSuite.VariableSuiteFactory;
import edu.kaiseran.structuregrader.wrapper.ParameterWrapper;
import edu.test.annotations.TestAnnotation1;
import edu.test.annotations.TestAnnotation2;
import lombok.NonNull;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParameterSuiteTest {
	private static final String PARENT_NAME = "parent name";

	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};

	public ParameterSuiteTest() throws NoSuchMethodException {
	}

	@Before
	public void setup() {
		noncompliances.clear();
	}

	class ParamsTestObject {
		private void intMethod1(int int1, final int int2, @TestAnnotation1 int int3) {

		}

		private void intMethod2(int int1, final int int2, @TestAnnotation1 int int3) {

		}

		private void intMethod3(Object obj0, int int1, int int2, int int3) {

		}

		private void intMethod4(final int int2, int int1, @TestAnnotation1 int int3) {

		}

		private void intMethod5(final int int1, int int2, @TestAnnotation2 int int3) {

		}

		private void objMethod1(Object obj0, List obj1, List<Integer> obj2, Object... objects) {

		}

		private void objMethod2(Object obj0, List obj1, List<Integer> obj2, Object... objects) {

		}

		private void objMethod3(Object obj0, List obj1, List<Integer> obj2, Object[] objects) {

		}

		private void objMethod4(Object obj0, List obj1, List obj2, Object... objects) {

		}
	}

	private final Method intMethod1 = ParamsTestObject.class.getDeclaredMethod(
			"intMethod1", int.class, int.class, int.class
	);
	private final Method intMethod2 = ParamsTestObject.class.getDeclaredMethod(
			"intMethod2", int.class, int.class, int.class
	);
	private final Method intMethod3 = ParamsTestObject.class.getDeclaredMethod(
			"intMethod3", Object.class, int.class, int.class, int.class
	);
	private final Method intMethod4 = ParamsTestObject.class.getDeclaredMethod(
			"intMethod4", int.class, int.class, int.class
	);
	private final Method intMethod5 = ParamsTestObject.class.getDeclaredMethod(
			"intMethod5", int.class, int.class, int.class
	);

	private final Method objMethod1 = ParamsTestObject.class.getDeclaredMethod(
			"objMethod1", Object.class, List.class, List.class, Object[].class
	);

	private final Method objMethod2 = ParamsTestObject.class.getDeclaredMethod(
			"objMethod2", Object.class, List.class, List.class, Object[].class
	);

	private final Method objMethod3 = ParamsTestObject.class.getDeclaredMethod(
			"objMethod3", Object.class, List.class, List.class, Object[].class
	);

	private final Method objMethod4 = ParamsTestObject.class.getDeclaredMethod(
			"objMethod4", Object.class, List.class, List.class, Object[].class
	);

	private final VariableSuiteFactory<ParameterWrapper> factory = VariableSuiteFactory.getDefaultInst();

	private Map<String, ParameterWrapper> getWrapperMapFrom(
			final Method method
	) {
		final Parameter[] suiteParams = method.getParameters();
		final Set<ParameterWrapper> suiteWrappers = new HashSet<>();

		for (int i = 0; i < method.getParameterCount(); i++) {
			suiteWrappers.add(new ParameterWrapper(suiteParams[i], i));
		}

		return suiteWrappers.stream().collect(Collectors.toMap(Named::getName, Function.identity()));
	}

	private Map<String, VariableSuite<ParameterWrapper>> getSuiteMapFrom(
			final Method method,
			final VariableSuiteFactory<ParameterWrapper> factory
	) {
		final Map<String, ParameterWrapper> wrappers = getWrapperMapFrom(method);
		final Map<String, VariableSuite<ParameterWrapper>> suites = wrappers.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> factory.buildFromItem(
						entry.getValue(),
						ParamsTestObject.class.getSimpleName() + "::" + method.getName(),
						noncomplianceConsumer
				)));
		return suites;
	}

	private boolean noncomplianceOfTypeWasMade(
			final Class<? extends Noncompliance> noncomplianceClass
	) {
		return noncompliances.stream().anyMatch(noncomplianceClass::isInstance);
	}

	private static void runSuitesAgainstWrappers(
			@NonNull final Map<String, ParameterWrapper> wrapperMap,
			@NonNull final Map<String, VariableSuite<ParameterWrapper>> suiteMap
	) {
		suiteMap.forEach((name, suite) -> suite.visit(wrapperMap.get(name)));
	}

	@Test
	public void intsSameMethod() {
		final Map<String, ParameterWrapper> wrappers = getWrapperMapFrom(intMethod1);
		final Map<String, VariableSuite<ParameterWrapper>> suites = getSuiteMapFrom(intMethod1, factory);
		runSuitesAgainstWrappers(
				wrappers,
				suites
		);
		assert noncompliances.isEmpty();
	}

	@Test
	public void intsIdenticalMethods() {
		final Map<String, ParameterWrapper> wrappers = getWrapperMapFrom(intMethod1);
		final Map<String, VariableSuite<ParameterWrapper>> suites = getSuiteMapFrom(intMethod2, factory);
		runSuitesAgainstWrappers(
				wrappers,
				suites
		);
		assert noncompliances.isEmpty();
	}

	@Test
	public void intsMethodsWithSameParamsInDifferentOrder() {
		// Order isn't important to the suites
		final Map<String, ParameterWrapper> wrappers = getWrapperMapFrom(intMethod1);
		final Map<String, VariableSuite<ParameterWrapper>> suites = getSuiteMapFrom(intMethod4, factory);
		runSuitesAgainstWrappers(
				wrappers,
				suites
		);
		assert noncompliances.isEmpty();
	}

	@Test
	public void intsShouldNotMatch() {
		final Map<String, ParameterWrapper> wrappers = getWrapperMapFrom(intMethod5);
		final Map<String, VariableSuite<ParameterWrapper>> suites = getSuiteMapFrom(intMethod1, factory);
		runSuitesAgainstWrappers(
				wrappers,
				suites
		);
		assert !noncompliances.isEmpty();
		assert noncomplianceOfTypeWasMade(ModifiedSpec.ModifiedNoncompliance.class);
		assert noncomplianceOfTypeWasMade(NoExtraSpec.ExtraNoncompliance.class);
		assert noncomplianceOfTypeWasMade(NoMissingSpec.MissingNoncompliance.class);
	}

	@Test
	public void objsShouldMatch() {
		final Map<String, ParameterWrapper> wrappers = getWrapperMapFrom(objMethod1);
		final Map<String, VariableSuite<ParameterWrapper>> suites = getSuiteMapFrom(objMethod2, factory);
		runSuitesAgainstWrappers(
				wrappers,
				suites
		);
		assert noncompliances.isEmpty();
	}

	@Test
	public void objsArrayVersusVariadic() {
		final Map<String, ParameterWrapper> wrappers = getWrapperMapFrom(objMethod1);
		final Map<String, VariableSuite<ParameterWrapper>> suites = getSuiteMapFrom(objMethod3, factory);
		runSuitesAgainstWrappers(
				wrappers,
				suites
		);
		assert noncompliances.isEmpty();
	}

	@Test
	public void objsListNotTyped() {
		final Map<String, ParameterWrapper> wrappers = getWrapperMapFrom(objMethod1);
		final Map<String, VariableSuite<ParameterWrapper>> suites = getSuiteMapFrom(objMethod4, factory);
		runSuitesAgainstWrappers(
				wrappers,
				suites
		);
		assert !noncompliances.isEmpty();
		assert noncomplianceOfTypeWasMade(TypedSpec.TypedNoncompliance.class);
	}
}