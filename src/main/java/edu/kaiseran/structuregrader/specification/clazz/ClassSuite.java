package edu.kaiseran.structuregrader.specification.clazz;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.specification.clazz.ClassMapSuite.ClassMapSuiteFactory;
import edu.kaiseran.structuregrader.specification.collection.NoExtraClassesMapSpec.NoExtraClassesSpecFactory;
import edu.kaiseran.structuregrader.specification.executable.ConstructorSetSuite;
import edu.kaiseran.structuregrader.specification.variable.FieldMapSuite.FieldMapSuiteFactory;
import edu.kaiseran.structuregrader.specification.executable.MethodMapSuite.MethodMapSuiteFactory;
import edu.kaiseran.structuregrader.visitor.ClassVisitor;
import edu.kaiseran.structuregrader.visitor.ClassVisitorFactory;
import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static edu.kaiseran.structuregrader.specification.clazz.AnnotatedClassSuite.AnnotatedClassSuiteFactory;
import static edu.kaiseran.structuregrader.specification.clazz.SuperclassSpec.SuperclassSpecFactory;
import static edu.kaiseran.structuregrader.specification.collection.NoMissingClassesMapSpec.NoMissingClassesSpecFactory;

/**
 * Contains specifications for a single class (the "specified" class), possibly including a ClassMapSuite to specify
 * this class's inner classes.
 */
@Builder
@EqualsAndHashCode
public class ClassSuite implements ClassVisitor {
	/**
	 * The specifications and suites to visit the specified class.
	 */
	@NonNull
	private final List<ClassVisitor> specifiedClassVisitors;

	/**
	 * The name of the parent of the specified class.
	 */
	@NonNull
	@Getter
	private final String parentName;

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		specifiedClassVisitors.forEach(visitor -> visitor.visit(classWrapper));
	}

	/**
	 * Factory class for ClassSuite. Has a list of ClassVisitorFactories provided by default to make the specifications
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
		private static ImmutableList<ClassVisitorFactory<?>> getDefaultVisitorFactories() {
			return ImmutableList.of(
					NoExtraClassesSpecFactory.getDefaultInst(),
					NoMissingClassesSpecFactory.getDefaultInst(),
					SuperclassSpecFactory.getDefaultInst(),
					AnnotatedClassSuiteFactory.getDefaultInst(),
					FieldMapSuiteFactory.getDefaultInst(),
					MethodMapSuiteFactory.getDefaultInst(),
					InterfaceMapSuite.InterfaceMapSuiteFactory.getDefaultInst(),
					ConstructorSetSuite.ConstructorSetSuiteFactory.getDefaultInst()
			);
		}

		/**
		 * @return a pre-made, default instance for consumers of ClassSuiteFactory to use.
		 *
		 * @param classCollectionSuiteFactory The factory to be used to provide a suite for a class's inner classes.
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
		public ClassSuiteFactory(
				@CheckForNull final ImmutableSet<ClassVisitorFactory<?>> classVisitorFactories,
				@CheckForNull final ClassMapSuiteFactory classMapSuiteFactory
		) {
			this.classVisitorFactories = classVisitorFactories != null && !classVisitorFactories.isEmpty() ?
					ImmutableSet.copyOf(classVisitorFactories) :
					ImmutableSet.copyOf(getDefaultVisitorFactories());
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
			// Make the default spec suite for the declared classes of the visited class
			final ClassMapSuite classCollectionSuite = classMapSuiteFactory.buildFromCollection(
					classWrapper.getDeclaredClasses(),
					classWrapper.getName(),
					noncomplianceConsumer
			);

			// Make the default specs for the visited class
			@SuppressWarnings("ArraysAsListWithZeroOrOneArgument") // Collections.singletonList is immutable
			final List<ClassVisitor> specifiedClassVisitors = new ArrayList<>(Arrays.asList(classCollectionSuite));
			classVisitorFactories.stream()
					.map(factory -> (ClassVisitor) factory.buildFromItem(classWrapper, parentName, noncomplianceConsumer))
					.forEach(specifiedClassVisitors::add);

			return ClassSuite.builder()
					.specifiedClassVisitors(specifiedClassVisitors)
					.parentName(parentName)
					.build();
		}
	}
}
