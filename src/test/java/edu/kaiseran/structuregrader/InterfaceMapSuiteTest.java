package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.specification.clazz.InterfaceMapSuite;
import edu.kaiseran.structuregrader.specification.collection.MissingExtraHelper;
import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InterfaceMapSuiteTest {
	private static final String PARENT_NAME = "parent name";
	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};

	@Before
	public void setup() {
		noncompliances.clear();
	}

	private boolean noncomplianceOfTypeWasMade(
			final Class<? extends Noncompliance> noncomplianceClass
	) {
		return noncompliances.stream().anyMatch(noncomplianceClass::isInstance);
	}

	private static class NoInterfaceClass{}
	private static class OneInterfaceClass implements edu.test.proj1.Interface1 {
		@Override
		public void method(final Object arg) {
		}
	}
	private static class TwoInterfaceClass1 implements edu.test.proj1.Interface1, edu.test.proj1.Interface2 {
		@Override
		public void method(final Object arg) {

		}
	}
	private static class TwoInterfaceClass2 implements edu.test.proj2.Interface1, edu.test.proj2.Interface2 {
		@Override
		public void method(final Object arg) {

		}
	}
	private final ClassWrapper noInterface = new ClassWrapper(NoInterfaceClass.class);
	private final ClassWrapper oneInterface = new ClassWrapper(OneInterfaceClass.class);
	private final ClassWrapper twoInterface1 = new ClassWrapper(TwoInterfaceClass1.class);
	private final ClassWrapper twoInterface2 = new ClassWrapper(TwoInterfaceClass2.class);

	private final InterfaceMapSuite.InterfaceMapSuiteFactory factory = InterfaceMapSuite.InterfaceMapSuiteFactory.getDefaultInst();

	@Test
	public void testSameNone() {
		factory.buildFromItem(noInterface, PARENT_NAME, noncomplianceConsumer).visit(noInterface);
		assert noncompliances.isEmpty();
	}

	@Test
	public void testSameOne() {
		factory.buildFromItem(oneInterface, PARENT_NAME, noncomplianceConsumer).visit(oneInterface);
		assert noncompliances.isEmpty();
	}

	@Test
	public void testSameTwo() {
		factory.buildFromItem(twoInterface1, PARENT_NAME, noncomplianceConsumer).visit(twoInterface1);
		assert noncompliances.isEmpty();
	}

	@Test
	public void testIdenticalTwo() {
		factory.buildFromItem(twoInterface1, PARENT_NAME, noncomplianceConsumer)
				.visit(twoInterface2);
		assert noncompliances.isEmpty();
	}

	@Test
	public void testMissing() {
		factory.buildFromItem(oneInterface, PARENT_NAME, noncomplianceConsumer).visit(noInterface);
		assert noncomplianceOfTypeWasMade(MissingExtraHelper.MissingNoncompliance.class);
	}

	@Test
	public void testExtra() {
		factory.buildFromItem(noInterface, PARENT_NAME, noncomplianceConsumer).visit(oneInterface);
		assert noncomplianceOfTypeWasMade(MissingExtraHelper.ExtraNoncompliance.class);
	}
}
