package com.github.ndrwksr.structuregrader.core.visitor;

import com.github.ndrwksr.structuregrader.core.wrapper.ClassWrapper;

/**
 * Type alias for an ItemVisitor which only visits ClassWrappers.
 */
public interface ClassVisitor extends ItemVisitor<ClassWrapper> {
}
