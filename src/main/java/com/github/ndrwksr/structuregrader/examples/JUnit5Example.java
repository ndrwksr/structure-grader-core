package com.github.ndrwksr.structuregrader.examples;

import com.github.ndrwksr.structuregrader.core.ClassMap;
import com.github.ndrwksr.structuregrader.core.specification.clazz.ClassMapSuite;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class JUnit5Example {
	// Change this to be the absolute path to your oracle solution.
	// File name doesn't have to be oracle.jar, but must be a .jar.
	private static final String MY_ORACLE_PATH = "C:/{PATH}/{TO}/{ORACLE}/{SOLUTION}/oracle.jar";
	private static final String PKG_TO_SCAN_FOR_ORACLE = "edu.kaiseran.example.pkg";

	// File name doesn't have to be student.jar, but must be a .jar.
	private static final String MY_STUDENT_SOLUTION_PATH = "C:/{PATH}/{TO}/{STUDENT}/{SOLUTION}/student.jar";

	@Test
	public void exampleTestCase() throws IOException {
		// First, we create a "wrapper stack" from our oracle solution
		// This is a simple data structure which wraps the Java reflective API
		final ClassMap oracleClassMap = ClassMap.buildFromPackage(
				// If your oracle solution IS the current project, you could instead use
				//  getClass().getClassLoader()
				URLClassLoader.newInstance(new URL[]{new URL(MY_ORACLE_PATH)}),
				PKG_TO_SCAN_FOR_ORACLE
		);

		// Secondly, we use the wrapper stack from step 1 to create a "specification stack",
		//  a much less trivial data structure that encodes the structure of the oracle solution.
		// The default factory is the strictest possible, meaning the resulting specification stack will mandate a nearly
		//  identical solution to the oracle.
		// This suite can be pared down to have much more lenient expectations via the Specifier (a JavaFX application
		//  for creating and modifying specification stacks).
		final ClassMapSuite suite = ClassMapSuite.ClassMapSuiteFactory.getDefaultInst().buildFromCollection(
				oracleClassMap,
				PKG_TO_SCAN_FOR_ORACLE, // By convention, should be the package that you scanned to build the class map.

				// This is how the suite will report back any deviations from the expected structure.
				// We're just printing them out here, but a more real-world application would be to return them to students.
				noncompliance -> System.out.println(noncompliance.getExplanation())
		);

		// Third, we create a wrapper stack from the student solution
		final ClassMap studentClassMap = ClassMap.buildFromPackage(
				URLClassLoader.newInstance(new URL[]{new URL(MY_STUDENT_SOLUTION_PATH)}),
				PKG_TO_SCAN_FOR_ORACLE
		);

		// Here's where the real magic happens, but unless you're interested in the inner workings then this can be treated
		//  as a black box. The noncompliance consumer is your key to accessing the outputs of the structure analyzer.
		suite.visit(studentClassMap);
	}
}
