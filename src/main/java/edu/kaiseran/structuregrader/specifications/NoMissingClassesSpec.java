package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.ClassCollection;
import edu.kaiseran.structuregrader.ClassCollection.CollectionVisitor;
import edu.kaiseran.structuregrader.ClassStructure;
import edu.kaiseran.structuregrader.ClassStructure.ClassVisitor;
import edu.kaiseran.structuregrader.Noncompliance;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Enforces that the visitee declares all of the classes specified. Can visit classes (which will
 * result in visiting the class's ClassCollection) or ClassCollections directly.
 */
@Data
@Builder(access = AccessLevel.PRIVATE)
public class NoMissingClassesSpec implements ClassVisitor, CollectionVisitor {

	/**
	 * Accepts any generated noncompliances, decoupling the consumption of noncompliances from their
	 * creation.
	 */
	@NonNull
	private final Consumer<Noncompliance> noncomplianceConsumer;

	/**
	 * The names of the classes which are allowed to exist.
	 */
	@NonNull
	private final List<String> expectedNames;

	@Override
	public void visit(@NonNull final ClassCollection classCollection) {
		MissingExtraHelper.checkForMissing(
				classCollection.getName(),
				expectedNames, classCollection.getDeclaredClasses(),
				noncomplianceConsumer
		);
	}

	@Override
	public void visit(@NonNull final ClassStructure classStructure) {
		visit(classStructure.getClassCollection());
	}

	/**
	 * Factory of NoMissingClassesSpec. Creates a NoMissingClassesSpec whose set of required class names is the set of
	 * class names in classCollection.
	 */
	public static class NoMissingClassesSpecFactory implements CollectionVisitorFactory<NoMissingClassesSpec> {

		@Override
		public NoMissingClassesSpec buildVisitorFrom(
				@NonNull final ClassCollection classCollection,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final List<String> declaredClassNames = new ArrayList<>(classCollection.getDeclaredClasses().keySet());

			return NoMissingClassesSpec.builder()
					.expectedNames(declaredClassNames)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}

		@Override
		public NoMissingClassesSpec buildVisitorFrom(
				@NonNull final ClassStructure classStructure,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return buildVisitorFrom(classStructure.getClassCollection(), noncomplianceConsumer);
		}
	}
}
