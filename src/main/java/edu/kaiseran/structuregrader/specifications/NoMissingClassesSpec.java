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

import javax.annotation.CheckForNull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Enforces that the visitee declares all of the classes specified. Can visit classes (which will
 * result in visiting the class's ClassCollection) or ClassCollections directly.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class NoMissingClassesSpec extends NoMissingSpec<ClassWrapper> implements ClassHierarchyVisitor {

	@Override
	public void visit(@CheckForNull final ClassWrapper classWrapper) {
		super.visit(classWrapper != null ? classWrapper.getClassCollection() : null);
	}

	public static class NoMissingClassesSpecFactory implements ClassHierarchyVisitorFactory<NoMissingClassesSpec> {

		@NonNull
		public static NoMissingClassesSpecFactory getDefaultInst() {
			return new NoMissingClassesSpecFactory();
		}

		@Override
		public NoMissingClassesSpec buildFromCollection(
				@NonNull final NamedCollection<ClassWrapper> classCollection,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Set<String> declaredClassNames = new HashSet<>(classCollection.getItems().keySet());

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
			return buildFromCollection(classWrapper.getClassCollection(), classWrapper.getName(), noncomplianceConsumer);
		}
	}
}
