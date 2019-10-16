package edu.kaiseran.structuregrader.specifications;

import edu.kaiseran.structuregrader.ClassStructure;
import edu.kaiseran.structuregrader.Noncompliance;
import lombok.NonNull;

import java.util.function.Consumer;

/**
 * Interface for factories of ClassVisitors. Requires that a ClassVisitor can be built from a ClassStructure and a
 * Noncompliance consumer.
 *
 * @param <T> The type of the ClassVisitor being built.
 */
public interface ClassVisitorFactory<T extends ClassStructure.ClassVisitor> {
	/**
	 * Builds and returns a visitor from the provided ClassStructure and Noncompliance consumer.
	 *
	 * @param classStructure        The ClassStructure to base the visitor off of
	 * @param noncomplianceConsumer A consumer for any Noncompliances the generated class may create.
	 * @return a visitor from the provided ClassStructure and Noncompliance consumer.
	 */
	T buildVisitorFrom(
			@NonNull final ClassStructure classStructure,
			@NonNull final Consumer<Noncompliance> noncomplianceConsumer
	);
}
