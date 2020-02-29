package edu.kaiseran.structuregrader.core.visitor;

public interface Spec {
	/**
	 * Returns the name of the parent element of the visitee.
	 *
	 * @return the name of the parent element of the visitee.
	 */
	String getParentName();

	default String specTypeName() {
		return this.getClass().getSimpleName();
	}
}
