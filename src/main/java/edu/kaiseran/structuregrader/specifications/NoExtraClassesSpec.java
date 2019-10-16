package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.ClassCollection;
import edu.kaiseran.structuregrader.ClassCollection.CollectionVisitor;
import edu.kaiseran.structuregrader.ClassStructure;
import edu.kaiseran.structuregrader.Noncompliance;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Enforces that the visitee has only the classes specified. Can visit classes (which will result
 * in visiting the class's ClassCollection) or ClassCollections directly.
 */
@Data
@Builder(access = AccessLevel.PRIVATE)
public class NoExtraClassesSpec implements CollectionVisitor {

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
		MissingExtraHelper.checkForExtra(
				classCollection.getName(),
				expectedNames,
				classCollection.getDeclaredClasses(),
				noncomplianceConsumer
		);
	}

	@Override
	public void visit(@NonNull final ClassStructure classStructure) {
		visit(classStructure.getClassCollection());
	}

	/**
	 * Factory of NoExtraClassesSpec. Creates a NoExtraClassesSpec whose set of legal class names is the set of class
	 * names in classCollection.
	 */
	public static class NoExtraClassesSpecFactory implements CollectionVisitorFactory<NoExtraClassesSpec> {

		@Override
		public NoExtraClassesSpec buildVisitorFrom(
				@NonNull final ClassCollection classCollection,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final List<String> declaredClassNames = new ArrayList<>(classCollection.getDeclaredClasses().keySet());

			return NoExtraClassesSpec.builder()
					.expectedNames(declaredClassNames)
					.noncomplianceConsumer(noncomplianceConsumer)
					.build();
		}

		@Override
		public NoExtraClassesSpec buildVisitorFrom(
				@NonNull final ClassStructure classStructure,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return buildVisitorFrom(classStructure.getClassCollection(), noncomplianceConsumer);
		}
	}

}
