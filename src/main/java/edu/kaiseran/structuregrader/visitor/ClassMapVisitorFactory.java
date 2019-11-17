package edu.kaiseran.structuregrader.visitor;

import edu.kaiseran.structuregrader.wrapper.ClassWrapper;

/**
 * Type alias for a factory of ClassCollectionVisitors.
 * See type alias section in README.md for further explanation of type aliases.
 *
 * @param <VISITOR> The type of the visitor that this factory makes.
 */
public interface ClassMapVisitorFactory<VISITOR extends ClassMapVisitor>
		extends MapVisitorFactory<ClassWrapper, VISITOR> {
}
