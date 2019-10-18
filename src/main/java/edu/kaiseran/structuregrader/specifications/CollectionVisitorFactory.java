package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.ClassCollection;
import edu.kaiseran.structuregrader.ClassCollection.CollectionVisitor;
import edu.kaiseran.structuregrader.Noncompliance;
import lombok.NonNull;

import java.util.function.Consumer;

/**
 * Interface for factories of CollectionVisitor. Requires that a CollectionVisitor can be built from a ClassStructure or
 * a ClassCollection and a Noncompliance consumer.
 *
 * @param <T> The type of the CollectionVisitor being built.
 */
public interface CollectionVisitorFactory<T extends CollectionVisitor> extends ClassVisitorFactory<T> {
	/**
	 * Builds and returns a visitor from the provided ClassCollection and Noncompliance consumer.
	 *
	 * @param classCollection       The ClassCollection to base the visitor off of
	 * @param noncomplianceConsumer A consumer for any Noncompliances the generated class may create.
	 * @return a visitor from the provided ClassCollection and Noncompliance consumer.
	 */
	T buildFromCollection(
			@NonNull final ClassCollection classCollection,
			@NonNull final Consumer<Noncompliance> noncomplianceConsumer
	);
}
