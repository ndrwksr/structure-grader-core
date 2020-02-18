package edu.kaiseran.structuregrader.core.specification.clazz;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.specification.clazz.AnnotatedClassSuite.AnnotatedClassSuiteFactory;
import edu.kaiseran.structuregrader.core.specification.clazz.ClassMapSuite.ClassMapSuiteFactory;
import edu.kaiseran.structuregrader.core.specification.clazz.InterfaceSetSuite.InterfaceMapSuiteFactory;
import edu.kaiseran.structuregrader.core.specification.collection.NoExtraClassesMapSpec.NoExtraClassesSpecFactory;
import edu.kaiseran.structuregrader.core.specification.executable.ConstructorSetSuite.ConstructorSetSuiteFactory;
import edu.kaiseran.structuregrader.core.specification.executable.MethodMapSuite.MethodMapSuiteFactory;
import edu.kaiseran.structuregrader.core.specification.variable.FieldMapSuite.FieldMapSuiteFactory;
import edu.kaiseran.structuregrader.core.visitor.ClassVisitor;
import edu.kaiseran.structuregrader.core.visitor.ClassVisitorFactory;
import edu.kaiseran.structuregrader.core.wrapper.ClassWrapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static edu.kaiseran.structuregrader.core.specification.clazz.SuperclassSpec.SuperclassSpecFactory;
import static edu.kaiseran.structuregrader.core.specification.collection.NoMissingClassesMapSpec.NoMissingClassesSpecFactory;

/**
 * Contains specifications for a single class (the "specified" class), possibly including a ClassMapSuite to specify
 * this class's inner classes.
 */
@EqualsAndHashCode
@Getter
public class ClassSuite implements ClassVisitor {
	/**
	 * The specifications and suites to visit the specified class.
	 */
	@NonNull
	private final ImmutableSet<ClassVisitor> specifiedClassVisitors;

	/**
	 * The name of the parent of the specified class.
	 */
	@NonNull
	private final String parentName;

	@Builder
	public ClassSuite(
			@NonNull @JsonProperty("specifiedClassVisitors") final ImmutableSet<ClassVisitor> specifiedClassVisitors,
			@NonNull @JsonProperty("parentName") final String parentName
	) {
		this.specifiedClassVisitors = specifiedClassVisitors;
		this.parentName = parentName;
	}

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		specifiedClassVisitors.forEach(visitor -> visitor.visit(classWrapper));
	}

	/**
	 * Factory class for ClassSuite. Has a set of ClassVisitorFactories provided by default to make the specifications
	 * for the specified class, and uses the default ClassMapSuiteFactory to specify the inner classes.
	 */
	public static class ClassSuiteFactory implements ClassVisitorFactory<ClassSuite> {

		/**
		 * The ClassMapSuiteFactory to be used to make a ClassMapSuite for the specified class's inner classes.
		 */
		private ClassMapSuiteFactory classMapSuiteFactory;

		/**
		 * The ClassVisitorFactories provided by default for populating specifiedClassVisitors from classStructure.
		 */
		private static ImmutableSet<ClassVisitorFactory<?>> getDefaultVisitorFactories() {
			return ImmutableSet.of(
					NoExtraClassesSpecFactory.getDefaultInst(),
					NoMissingClassesSpecFactory.getDefaultInst(),
					SuperclassSpecFactory.getDefaultInst(),
					AnnotatedClassSuiteFactory.getDefaultInst(),
					FieldMapSuiteFactory.getDefaultInst(),
					MethodMapSuiteFactory.getDefaultInst(),
					InterfaceMapSuiteFactory.getDefaultInst(),
					ConstructorSetSuiteFactory.getDefaultInst()
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
					.specifiedClassVisitors(ImmutableSet.copyOf(classVisitors))
					.parentName(parentName)
					.build();
		}
	}
}
