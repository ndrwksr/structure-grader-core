package com.github.ndrwksr.structuregrader.core.specification.clazz;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ndrwksr.structuregrader.core.HasChildSet;
import com.github.ndrwksr.structuregrader.core.NamedSpecSet;
import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.specification.collection.NoExtraClassesMapSpec;
import com.github.ndrwksr.structuregrader.core.specification.collection.NoMissingClassesMapSpec;
import com.github.ndrwksr.structuregrader.core.specification.executable.ConstructorSetSuite;
import com.github.ndrwksr.structuregrader.core.specification.executable.MethodMapSuite;
import com.github.ndrwksr.structuregrader.core.specification.variable.FieldMapSuite;
import com.google.common.collect.ImmutableSet;
import com.github.ndrwksr.structuregrader.core.specification.clazz.AnnotatedClassSuite.AnnotatedClassSuiteFactory;
import com.github.ndrwksr.structuregrader.core.specification.clazz.ClassMapSuite.ClassMapSuiteFactory;
import com.github.ndrwksr.structuregrader.core.visitor.ClassVisitor;
import com.github.ndrwksr.structuregrader.core.visitor.ClassVisitorFactory;
import com.github.ndrwksr.structuregrader.core.wrapper.ClassWrapper;
import lombok.*;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.github.ndrwksr.structuregrader.core.specification.clazz.SuperclassSpec.SuperclassSpecFactory;

/**
 * Contains specifications for a single class (the "specified" class), possibly including a ClassMapSuite to specify
 * this class's inner classes.
 */
@EqualsAndHashCode
@Getter
public class ClassSuite implements ClassVisitor, HasChildSet {
	/**
	 * The specifications and suites to visit the specified class.
	 */
	@NonNull
	private final Set<ClassVisitor> specifiedClassVisitors;

	/**
	 * The name of the parent of the specified class.
	 */
	@NonNull
	private final String parentName;

	@Builder
	public ClassSuite(
			@NonNull @JsonProperty("specifiedClassVisitors") final Set<ClassVisitor> specifiedClassVisitors,
			@NonNull @JsonProperty("parentName") final String parentName
	) {
		this.specifiedClassVisitors = specifiedClassVisitors;
		this.parentName = parentName;
	}

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		specifiedClassVisitors.forEach(visitor -> visitor.visit(classWrapper));
	}

	@Override
	public NamedSpecSet getChildSet() {
		return NamedSpecSet.<ClassVisitor>builder()
				.items(specifiedClassVisitors)
				.name(".specifiedClassVisitors")
				.build();
	}

	@Override
	public String getSpecDescription() {
		return "A suite that holds all specifications for a single class and its declared classes.";
	}

	/**
	 * Factory class for ClassSuite. Has a set of ClassVisitorFactories provided by default to make the specifications
	 * for the specified class, and uses the default ClassMapSuiteFactory to specify the inner classes.
	 */
	public static class ClassSuiteFactory implements ClassVisitorFactory<ClassSuite> {

		/**
		 * The ClassMapSuiteFactory to be used to make a ClassMapSuite for the specified class's inner classes.
		 */
		@Setter
		private ClassMapSuiteFactory classMapSuiteFactory;

		/**
		 * The ClassVisitorFactories provided by default for populating specifiedClassVisitors from classStructure.
		 */
		private static ImmutableSet<ClassVisitorFactory<?>> getDefaultVisitorFactories() {
			return ImmutableSet.of(
					NoExtraClassesMapSpec.NoExtraClassesSpecFactory.getDefaultInst(),
					NoMissingClassesMapSpec.NoMissingClassesSpecFactory.getDefaultInst(),
					SuperclassSpecFactory.getDefaultInst(),
					AnnotatedClassSuiteFactory.getDefaultInst(),
					FieldMapSuite.FieldMapSuiteFactory.getDefaultInst(),
					MethodMapSuite.MethodMapSuiteFactory.getDefaultInst(),
					InterfaceSetSuite.InterfaceMapSuiteFactory.getDefaultInst(),
					ConstructorSetSuite.ConstructorSetSuiteFactory.getDefaultInst()
			);
		}

		/**
		 * @param classCollectionSuiteFactory The factory to be used to provide a suite for a class's inner classes.
		 * @return a pre-configured instance for consumers of ClassSuiteFactory to use.
		 */
		public static ClassSuiteFactory getDefaultInst(
				@NonNull final ClassMapSuiteFactory classCollectionSuiteFactory
		) {
			return new ClassSuiteFactory(null, classCollectionSuiteFactory);
		}

		/**
		 * The ClassVisitorFactories that this factory uses to populate specifiedClassVisitors with specs.
		 */
		private final ImmutableSet<ClassVisitorFactory<?>> classVisitorFactories;

		/**
		 * @param classVisitorFactories The ClassVisitorFactory instances this factory will use to populate
		 *                              specifiedClassVisitors with specs and suites.
		 * @param classMapSuiteFactory  The ClassMapSuiteFactory to make a ClassMapSuite for the inner classes.
		 */
		@Builder
		public ClassSuiteFactory(
				@CheckForNull final ImmutableSet<ClassVisitorFactory<?>> classVisitorFactories,
				@CheckForNull final ClassMapSuiteFactory classMapSuiteFactory
		) {
			this.classVisitorFactories = classVisitorFactories != null && !classVisitorFactories.isEmpty() ?
					classVisitorFactories :
					getDefaultVisitorFactories();
			this.classMapSuiteFactory = classMapSuiteFactory != null ?
					classMapSuiteFactory :
					ClassMapSuiteFactory.getDefaultInst();
		}

		@Override
		public ClassSuite buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			// Make the default specs for the visited class
			final Set<ClassVisitor> classVisitors = classVisitorFactories.stream()
					.map(factory -> (ClassVisitor) factory.buildFromItem(classWrapper, parentName, noncomplianceConsumer))
					.collect(Collectors.toSet());

			// Make the default spec suite for the declared classes of the visited class
			final ClassMapSuite classCollectionSuite = classMapSuiteFactory.buildFromCollection(
					classWrapper.getDeclaredClasses(),
					classWrapper.getName(),
					noncomplianceConsumer
			);
			classVisitors.add(classCollectionSuite);

			return ClassSuite.builder()
					.specifiedClassVisitors(classVisitors)
					.parentName(parentName)
					.build();
		}
	}
}
