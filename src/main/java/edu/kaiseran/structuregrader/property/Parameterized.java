package edu.kaiseran.structuregrader.property;

import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.wrapper.ParameterWrapper;
import lombok.NonNull;

/**
 * An interface for wrappers which have parameters to return said parameters in a NamedMap.
 */
public interface Parameterized {
	/**
	 * @return the parameters for the Parameterized instance wrapped in ParameterWrappers in a NamedMap.
	 */
	@NonNull NamedMap<ParameterWrapper> getParameters();
}
