package edu.kaiseran.structuregrader.examples;

import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.core.ClassMap;
import edu.kaiseran.structuregrader.core.specification.clazz.ClassMapSuite;
import edu.kaiseran.structuregrader.core.specification.clazz.ClassSuite;
import edu.kaiseran.structuregrader.core.specification.clazz.InterfaceSetSuite;
import edu.kaiseran.structuregrader.core.specification.clazz.SuperclassSpec;
import edu.kaiseran.structuregrader.core.specification.collection.NoExtraMapSpec;
import edu.kaiseran.structuregrader.core.specification.collection.NoMissingMapSpec;
import edu.kaiseran.structuregrader.core.specification.common.ModifiedSpec;
import edu.kaiseran.structuregrader.core.specification.common.TypedSpec;
import edu.kaiseran.structuregrader.core.specification.executable.ConstructorSetSuite;
import edu.kaiseran.structuregrader.core.specification.executable.MethodMapSuite;
import edu.kaiseran.structuregrader.core.specification.variable.FieldMapSuite;
import edu.kaiseran.structuregrader.core.specification.variable.VariableSuite;
import edu.kaiseran.structuregrader.core.wrapper.MethodWrapper;

import java.io.IOException;

public class ComplexFactoryExample {
	private static ClassMapSuite.ClassMapSuiteFactory customFactory;

	public static void main(String[] args) throws IOException {
		customFactory = ClassMapSuite.ClassMapSuiteFactory.builder()
				.collectionVisitorFactories(ImmutableSet.of(
						// Don't report extra classes, don't add NoExtraMapSpecFactory
						NoMissingMapSpec.NoMissingMapSpecFactory.getDefaultInst("classes")
				))
				.itemSuiteFactory(
						ClassSuite.ClassSuiteFactory.builder()
								// ClassSuiteFactory needs a reference to customFactory, this can make instantiation a little tricky
								// Alternatively, a different ClassMapSuiteFactory could be used to have a separate set of expectations
								//  for top-level classes and inner classes.
								/* This factory can also be set like as shown here:
									   ((ClassSuite.ClassSuiteFactory)customFactory.getItemSuiteFactory())
										   .setClassMapSuiteFactory(customFactory); */
								.classMapSuiteFactory(customFactory)

								// Configure expectations about each top-level class
								// NOTE: This applies ONLY to top-level classes but can easily be made to apply to inner classes too by
								//  feeding the ClassMapSuiteFactory we're making back into the ClassSuiteFactory builder.
								// This can be a little tricky to accomplish because Java will attempt to prevent you from using a
								//  variable that hasn't been instantiated, but in practice there's no need to worry as we only need a
								//  reference (and not a fully instantiated ClassMapSuiteFactory) until the ClassSuiteFactory is
								//  actually used.
								.classVisitorFactories(ImmutableSet.of(
										// Don't report extra/missing classes, exclude No{Extra, Missing}MapSpecFactory

										// Do report incorrect superclass, include SuperclassSpecFactory
										SuperclassSpec.SuperclassSpecFactory.getDefaultInst(),

										// Don't report class-level annotations, exclude AnnotatedClassSuiteFactory

										// Do report incorrect interface implementations (missing & extra), include InterfaceMapSuiteFactory
										InterfaceSetSuite.InterfaceMapSuiteFactory.getDefaultInst(),

										// Custom ConstructorSetSuiteFactory
										ConstructorSetSuite.ConstructorSetSuiteFactory.builder()
												.mapVisitorFactories(ImmutableSet.of(
														// Don't report extra constructors, exclude NoExtraMapSpecFactory

														// Do report missing constructors, include NoMissingMapSpecFactory
														NoMissingMapSpec.NoMissingMapSpecFactory.getDefaultInst(
																ConstructorSetSuite.ConstructorSetSuiteFactory.ITEM_TYPE_PLURAL
														)
												))
												.build(),

										// Custom MethodMapSuiteFactory
										MethodMapSuite.MethodMapSuiteFactory.builder()
												// Expectations about the method set, but not about individual items
												.collectionVisitorFactories(ImmutableSet.of(
														// Don't report extra methods, exclude NoExtraMapSpecFactory

														// Do report missing methods, include NoMissingMapSpecFactory
														NoMissingMapSpec.NoMissingMapSpecFactory.getDefaultInst(
																MethodMapSuite.MethodMapSuiteFactory.ITEM_TYPE_PLURAL
														)
												))
												// Expectations about individual methods, but not the set as a whole
												.itemSuiteFactory(VariableSuite.VariableSuiteFactory.<MethodWrapper>builder()
														.visitorFactories(ImmutableSet.of(
																// Don't inspect annotations on methods, exclude AnnotatedSuiteFactory

																// Do report invalid method modifiers, include ModifiedSpecFactory
																ModifiedSpec.ModifiedSpecFactory.getDefaultInst(),

																// Do report invalid method types, include TypedSpecFactory
																TypedSpec.TypedSpecFactory.getDefaultInst()

																// Don't inspect method parameters, exclude ParameterMapSuiteFactory
														)).build()
												).build(),

										// Custom FieldMapSuiteFactory
										FieldMapSuite.FieldMapSuiteFactory.builder()
												.collectionVisitorFactories(ImmutableSet.of(
														// Don't report missing fields, exclude NoMissingSpecFactory

														// Do report extra fields, include NoExtraSpecFactory
														NoExtraMapSpec.NoExtraSpecFactory.getDefaultInst(
																FieldMapSuite.FieldMapSuiteFactory.ITEM_TYPE_PLURAL
														)
												)).build()
								)).build()
				).build();

		// Build a specification stack from our custom factory
		final ClassMapSuite classMapSuite = customFactory.buildFromCollection(
				ClassMap.buildFromPackage(
						ClassLoader.getPlatformClassLoader(),
						"com.example.pkg"
				),
				"com.example.pkg",
				$ -> {
				} // NOOP noncompliance consumer because we don't actually use this spec stack
		);

		// Serialize, then deserialize it using ClassMapSuite.JsonHelper
		final String classMapSuiteJson = ClassMapSuite.JsonHelper.toJson(classMapSuite);
		final ClassMapSuite deserializedClassMapSuite = ClassMapSuite.JsonHelper.fromJson(
				classMapSuiteJson,
				$ -> {
				} // NOOP noncompliance consumer because we don't actually use this spec stack
		);

		// There's very little configurability to (de)serialization but there's not a big need for that unless you have a
		//  use case goes very far beyond the original intentions of this project.
	}
}
