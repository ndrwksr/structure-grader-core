package edu.kaiseran.structuregrader;


import edu.kaiseran.structuregrader.specification.quantity.MissingExtraHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("ConstantConditions")
public class MissingExtraHelperTest {
	private static final String A_KEY = "A key";
	private static final String A_VAL = "A value";
	private static final String B_KEY = "B key";
	private static final String B_VAL = "B value";
	private static final String C_KEY = "C key";
	private static final String C_VAL = "C value";
	private static final String D_KEY = "D key";
	private static final String D_VAL = "D value";
	private static final String E_KEY = "E key";
	private static final String E_VAL = "E value";

	private final Map<String, Object> items = new HashMap<>();
	private final Set<String> expectedNames = new HashSet<>();
	private final List<Noncompliance> noncompliances = new ArrayList<>();
	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};

	@Before
	public void setup() {
		items.clear();
		expectedNames.clear();

		noncompliances.clear();
	}

	@Test
	public void testExtraWhereOneMissing() {
		items.put(A_KEY, A_VAL);
		expectedNames.add(A_KEY);

		// b key and b val not put in items
		expectedNames.add(B_KEY);

		MissingExtraHelper.checkForExtra(
				"testExtraWhereOneMissing",
				expectedNames,
				items,
				noncomplianceConsumer
		);

		assert noncompliances.size() == 0;
	}

	@Test
	public void testExtraWhereNoExtra() {
		items.put(A_KEY, A_VAL);
		expectedNames.add(A_KEY);

		items.put(B_KEY, B_VAL);
		expectedNames.add(B_KEY);

		MissingExtraHelper.checkForExtra(
				"testExtraWhereNoExtra",
				expectedNames,
				items,
				noncomplianceConsumer
		);

		assert noncompliances.size() == 0;
	}

	@Test
	public void testExtraWhereOneExtra() {
		items.put(A_KEY, A_VAL);
		expectedNames.add(A_KEY);

		items.put(B_KEY, B_VAL);
		expectedNames.add(B_KEY);

		items.put(C_KEY, C_VAL);
		// C key not added to expectedNames

		MissingExtraHelper.checkForExtra(
				"testExtraWhereOneExtra",
				expectedNames,
				items,
				noncomplianceConsumer
		);

		assert noncompliances.size() == 1;

		final Noncompliance extraNoncompliance = noncompliances.get(0);
		final String expectedString = extraNoncompliance.getExpected().toString();
		final String actualString = extraNoncompliance.getActual().toString();

		assert expectedString.contains(A_KEY);
		assert expectedString.contains(B_KEY);

		assert actualString.contains(A_KEY);
		assert actualString.contains(B_KEY);
		assert actualString.contains(C_KEY);
	}

	@Test
	public void testExtraWhereThreeExtra() {
		items.put(A_KEY, A_VAL);
		expectedNames.add(A_KEY);

		items.put(B_KEY, B_VAL);
		expectedNames.add(B_KEY);

		items.put(C_KEY, C_VAL);
		// C key not added to expectedNames

		items.put(D_KEY, D_VAL);
		// D key not added to expectedNames

		items.put(E_KEY, E_VAL);
		// E key not added to expectedNames

		MissingExtraHelper.checkForExtra(
				"testExtraWhereThreeExtra",
				expectedNames,
				items,
				noncomplianceConsumer
		);

		assert noncompliances.size() == 1;

		final Noncompliance extraNoncompliance = noncompliances.get(0);
		final String expectedString = extraNoncompliance.getExpected().toString();
		final String actualString = extraNoncompliance.getActual().toString();

		assert expectedString.contains(A_KEY);
		assert expectedString.contains(B_KEY);

		assert actualString.contains(A_KEY);
		assert actualString.contains(B_KEY);
		assert actualString.contains(C_KEY);
		assert actualString.contains(D_KEY);
		assert actualString.contains(E_KEY);
	}

	@Test
	public void testMissingWhereOneExtra() {
		items.put(A_KEY, A_VAL);
		expectedNames.add(A_KEY);

		items.put(B_KEY, B_VAL);
		// B key not added to expectedNames

		MissingExtraHelper.checkForMissing(
				"testMissingWhereOneExtra",
				expectedNames,
				items,
				noncomplianceConsumer
		);

		assert noncompliances.size() == 0;
	}

	@Test
	public void testMissingWhereNoMissing() {
		items.put(A_KEY, A_VAL);
		expectedNames.add(A_KEY);

		items.put(B_KEY, B_VAL);
		expectedNames.add(B_KEY);

		MissingExtraHelper.checkForMissing(
				"testMissingWhereNoMissing",
				expectedNames,
				items,
				noncomplianceConsumer
		);

		assert noncompliances.size() == 0;
	}

	@Test
	public void testMissingWhereOneMissing() {
		items.put(A_KEY, A_VAL);
		expectedNames.add(A_KEY);

		items.put(B_KEY, B_VAL);
		expectedNames.add(B_KEY);

		// c key and c val not put in items
		expectedNames.add(C_KEY);

		MissingExtraHelper.checkForMissing(
				"testMissingWhereOneMissing",
				expectedNames,
				items,
				noncomplianceConsumer
		);

		assert noncompliances.size() == 1;

		final Noncompliance missingNoncompliance = noncompliances.get(0);
		final String expectedString = missingNoncompliance.getExpected().toString();
		final String actualString = missingNoncompliance.getActual().toString();

		assert expectedString.contains(A_KEY);
		assert expectedString.contains(B_KEY);
		assert expectedString.contains(C_KEY);

		assert actualString.contains(A_KEY);
		assert actualString.contains(B_KEY);
	}

	@Test
	public void testMissingWhereThreeMissing() {
		items.put(A_KEY, A_VAL);
		expectedNames.add(A_KEY);

		items.put(B_KEY, B_VAL);
		expectedNames.add(B_KEY);

		// c key and c val not put in items
		expectedNames.add(C_KEY);

		// d key and d val not put in items
		expectedNames.add(D_KEY);

		// e key and e val not put in items
		expectedNames.add(E_KEY);

		MissingExtraHelper.checkForMissing(
				"testMissingWhereThreeMissing",
				expectedNames,
				items,
				noncomplianceConsumer
		);

		assert noncompliances.size() == 1;

		final Noncompliance missingNoncompliance = noncompliances.get(0);
		final String expectedString = missingNoncompliance.getExpected().toString();
		final String actualString = missingNoncompliance.getActual().toString();

		assert expectedString.contains(A_KEY);
		assert expectedString.contains(B_KEY);
		assert expectedString.contains(C_KEY);
		assert expectedString.contains(D_KEY);
		assert expectedString.contains(E_KEY);

		assert actualString.contains(A_KEY);
		assert actualString.contains(B_KEY);
	}
}
