package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.wrappers.ClassWrapper;
import edu.kaiseran.structuregrader.NamedCollection;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.visitors.ClassHierarchyVisitor;
import edu.kaiseran.structuregrader.visitors.ClassHierarchyVisitorFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Enforces that the specified collection has only the classes specified. Can visit ClassWrappers (which will result
 * in visiting the class's ClassCollection) or ClassCollections directly.
 */
@SuperBuilder
public class NoExtraClassesSpec extends NoExtraSpec<ClassWrapper> implements ClassHierarchyVisitor {

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		super.visit(classWrapper != null ? classWrapper.getClassCollection() : null);
	}

	/**
	 * Creates NoExtraClassesSpecs which only permits the classes present in the provided class collection.
	 */
	public static class NoExtraClassesSpecFactory implements ClassHierarchyVisitorFactory<NoExtraClassesSpec> {

		@Override
		public NoExtraClassesSpec buildFromCollection(
				@NonNull final NamedCollection<ClassWrapper> classCollection,
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
			return buildFromCollection(classWrapper.getClassCollection(), classWrapper.getName(), noncomplianceConsumer);
		}
	}
}
