package edu.kaiseran.structuregrader.visitor;

import edu.kaiseran.structuregrader.wrapper.ClassWrapper;

/**
 * Type alias for a CollectionVisitor which visits ClassWrappers.
 * See type alias section in README.md for further explanation of type aliases.
 */
public interface ClassMapVisitor extends MapVisitor<ClassWrapper> {
}
