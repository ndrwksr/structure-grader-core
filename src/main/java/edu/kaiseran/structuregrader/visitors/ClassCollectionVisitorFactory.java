package edu.kaiseran.structuregrader.visitors;

import edu.kaiseran.structuregrader.wrappers.ClassWrapper;

/**
 * Type alias for a factory for visitors of ClassStructure collections.
 * See type alias section in README.md for further explanation of type aliases.
 *
 * @param <VISITOR> The type of the visitor that this factory makes.
 */
public interface ClassCollectionVisitorFactory<VISITOR extends ClassCollectionVisitor>
		extends CollectionVisitorFactory<ClassWrapper, VISITOR> {
}
