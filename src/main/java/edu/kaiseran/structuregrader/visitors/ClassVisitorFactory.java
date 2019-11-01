package edu.kaiseran.structuregrader.visitors;

import edu.kaiseran.structuregrader.wrappers.ClassWrapper;

/**
 * Type alias for an ItemVisitorFactory for ClassStructures.
 * See type alias section in README.md for further explanation of type aliases.
 *
 * @param <VISITOR> The type of the visitor that this factory makes.
 */
public interface ClassVisitorFactory<VISITOR extends ClassVisitor>
		extends ItemVisitorFactory<ClassWrapper, VISITOR> {
}
