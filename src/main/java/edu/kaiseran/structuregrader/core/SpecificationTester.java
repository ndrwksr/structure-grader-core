package edu.kaiseran.structuregrader.core;

import edu.kaiseran.structuregrader.core.specification.clazz.ClassMapSuite;
import edu.kaiseran.structuregrader.core.specification.clazz.ClassMapSuite.ClassMapSuiteFactory;
import lombok.NonNull;

import java.io.IOException;
import java.util.function.Consumer;

public class SpecificationTester {
	public static void getNoncompliancesForStructures(
			@NonNull final String expectedPkg,
			@NonNull final String actualPkg,
			@NonNull final Consumer<Noncompliance> noncomplianceConsumer,
			@NonNull final ClassMapSuiteFactory mapSuiteFactory
	) throws IOException {
		final ClassMap expectedCollection = ClassMap.buildFromPackage(SpecificationTester.class.getClassLoader(), expectedPkg);
		final ClassMap actualCollection = ClassMap.buildFromPackage(SpecificationTester.class.getClassLoader(), actualPkg);

		final ClassMapSuite classMapSuite = mapSuiteFactory.buildFromCollection(
				expectedCollection,
				expectedCollection.getName(),
				noncomplianceConsumer
		);



		final String classMapJson = ClassMapSuite.JsonHelper.toJson(classMapSuite);
		//System.out.println(classMapJson);

		final ClassMapSuite desClassMapSuite = ClassMapSuite.JsonHelper.fromJson(classMapJson, noncomplianceConsumer);

		desClassMapSuite.visit(actualCollection);
	}
}
