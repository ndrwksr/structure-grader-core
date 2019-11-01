package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.specifications.ClassCollectionSuite;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static edu.kaiseran.structuregrader.specifications.ClassCollectionSuite.ClassCollectionSuiteFactory;

// TODO [ndrwksr | 10/15/19]: Document or discard.
//  This class currently only exists for testing, and will be discarded or entirely rewritten later.
public class SpecificationTester {

	public static List<Noncompliance> getNoncompliancesForStructures(
			@NonNull final String expectedPkg,
			@NonNull final String actualPkg
	) throws IOException {
		final ClassCollection expectedCollection = ClassCollection.buildFromPackage(expectedPkg);
		final ClassCollection actualCollection = ClassCollection.buildFromPackage(actualPkg);

		final List<Noncompliance> noncompliances = new ArrayList<>();
		final Consumer<Noncompliance> noncomplianceConsumer = noncompliances::add;

		final ClassCollectionSuite specSuite = ClassCollectionSuiteFactory.getDefaultInst()
				.buildFromCollection(expectedCollection, expectedCollection.getName(), noncomplianceConsumer);
		specSuite.visit(actualCollection);

		return noncompliances;
	}
}
