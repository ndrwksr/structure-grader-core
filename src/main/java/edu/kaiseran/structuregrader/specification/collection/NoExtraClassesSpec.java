package edu.kaiseran.structuregrader.specification.collection;

import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.visitor.ClassHierarchyVisitor;
import edu.kaiseran.structuregrader.visitor.ClassHierarchyVisitorFactory;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Enforces that the specified collection has only the classes specified. Can visit ClassWrappers (which will result
 * in visiting the class's inner ClassMap) or ClassMaps directly.
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NoExtraClassesSpec extends NoExtraSpec<ClassWrapper> implements ClassHierarchyVisitor {

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		super.visit(classWrapper != null ? classWrapper.getDeclaredClasses() : null);
	}

	/**
	 * Creates NoExtraClassesSpecs which only permits the classes present in the provided class collection.
	 */
	public static class NoExtraClassesSpecFactory implements ClassHierarchyVisitorFactory<NoExtraClassesSpec> {

		/**
		 * @return a pre-configured instance for consumers of NoExtraClassesSpecFactory to use.
		 */
		public static NoExtraClassesSpecFactory getDefaultInst() {
			return new NoExtraClassesSpecFactory();
		}

		@Override
		public NoExtraClassesSpec buildFromCollection(
				@NonNull final NamedMap<ClassWrapper> classCollection,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Set<String> declaredClassNames = new HashSet<>(classCollection.getItems().keySet());

			return NoExtraClassesSpec.builder()
					.expected(declaredClassNames)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}

		@Override
		public NoExtraClassesSpec buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return buildFromCollection(classWrapper.getDeclaredClasses(), classWrapper.getName(), noncomplianceConsumer);
		}
	}
}
