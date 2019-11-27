package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.specification.collection.NoExtraSpec;
import edu.kaiseran.structuregrader.specification.collection.NoMissingSpec;
import edu.kaiseran.structuregrader.specification.executable.MethodMapSuite;
import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import edu.test.proj1.MethodClass;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MethodMapSuiteTest {
	private final ClassWrapper methodClass = new ClassWrapper(MethodClass.class);
	private final MethodMapSuite.MethodMapSuiteFactory factory = MethodMapSuite.MethodMapSuiteFactory.getDefaultInst();

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

	@Test
	public void testMethodMapSuiteVisitsCorrectData() {
		final MethodMapSuite methodMapSuite = factory.buildFromItem(
				methodClass,
				MethodClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		methodMapSuite.visit(methodClass);

		assert noncompliances.isEmpty();
	}

	@Test
	public void testMethodMapSuiteVisitsIncorrectData() {
		final MethodMapSuite methodMapSuite = factory.buildFromItem(
				methodClass,
				MethodClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		methodMapSuite.visit(new ClassWrapper(ClassWrapper.class));

		assert noncomplianceOfTypeWasMade(NoExtraSpec.ExtraNoncompliance.class);
		assert noncomplianceOfTypeWasMade(NoMissingSpec.MissingNoncompliance.class);
	}
}
