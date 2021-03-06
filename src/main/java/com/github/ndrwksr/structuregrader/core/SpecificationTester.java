package com.github.ndrwksr.structuregrader.core;

import com.github.ndrwksr.structuregrader.core.specification.clazz.ClassMapSuite;
import com.github.ndrwksr.structuregrader.core.specification.clazz.ClassMapSuite.ClassMapSuiteFactory;
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

		classMapSuite.visit(actualCollection);
	}
}
