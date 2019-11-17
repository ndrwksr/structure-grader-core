package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.specification.clazz.ClassMapSuite;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static edu.kaiseran.structuregrader.specification.clazz.ClassMapSuite.ClassMapSuiteFactory;

// TODO [ndrwksr | 10/15/19]: Document or discard.
//  This class currently only exists for testing, and will be discarded or entirely rewritten later.
public class SpecificationTester {

	public static List<Noncompliance> getNoncompliancesForStructures(
			@NonNull final String expectedPkg,
			@NonNull final String actualPkg,
			@NonNull final Consumer<Noncompliance> noncomplianceConsumer
	) throws IOException {
		final ClassMap expectedCollection = ClassMap.buildFromPackage(expectedPkg);
		final ClassMap actualCollection = ClassMap.buildFromPackage(actualPkg);

		final List<Noncompliance> noncompliances = new ArrayList<>();

		final ClassMapSuite specSuite = ClassMapSuiteFactory.getDefaultInst()
				.buildFromCollection(expectedCollection, expectedCollection.getName(), noncomplianceConsumer);
		specSuite.visit(actualCollection);

		return noncompliances;
	}
}
