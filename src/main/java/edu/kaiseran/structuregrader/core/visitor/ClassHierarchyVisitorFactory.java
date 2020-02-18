package edu.kaiseran.structuregrader.core.visitor;


/**
 * Type alias for a factory for visitors of both classes and class collections.
 * See type alias section in README.md for further explanation of type aliases.
 *
 * @param <VISITOR> The type of the visitor that this factory makes.
 */
public interface ClassHierarchyVisitorFactory<VISITOR extends ClassHierarchyVisitor>
		extends ClassVisitorFactory<VISITOR>, ClassMapVisitorFactory<VISITOR> {
}
