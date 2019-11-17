package edu.kaiseran.structuregrader.specification.quantity;

import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.visitor.ClassHierarchyVisitor;
import edu.kaiseran.structuregrader.visitor.ClassHierarchyVisitorFactory;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Enforces that the specified collection has all of the classes specified. Can visit ClassWrappers (which will result
 * in visiting the class's ClassMaps) or ClassMaps directly.
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NoMissingClassesSpec extends NoMissingSpec<ClassWrapper> implements ClassHierarchyVisitor {

	@Override
	public void visit(@CheckForNull final ClassWrapper classWrapper) {
		super.visit(classWrapper != null ? classWrapper.getDeclaredClasses() : null);
	}

	/**
	 * Creates NoMissingClassesSpecs which mandate the presence of the classes in the specified class map.
	 */
	public static class NoMissingClassesSpecFactory implements ClassHierarchyVisitorFactory<NoMissingClassesSpec> {

		/**
		 * @return a pre-configured instance for consumers of NoMissingClassesSpecFactory to use.
		 */
		public static NoMissingClassesSpecFactory getDefaultInst() {
			return new NoMissingClassesSpecFactory();
		}

		@Override
		public NoMissingClassesSpec buildFromCollection(
				@NonNull final NamedMap<ClassWrapper> namedMap,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Set<String> declaredClassNames = new HashSet<>(namedMap.getItems().keySet());

			return NoMissingClassesSpec.builder()
					.expected(declaredClassNames)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}

		@Override
		public NoMissingClassesSpec buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return buildFromCollection(classWrapper.getDeclaredClasses(), classWrapper.getName(), noncomplianceConsumer);
		}
	}
}
