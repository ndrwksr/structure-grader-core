package edu.kaiseran.structuregrader.core.property;

import edu.kaiseran.structuregrader.core.NamedMap;
import edu.kaiseran.structuregrader.core.wrapper.ParameterWrapper;
import lombok.NonNull;

/**
 * An interface that allows wrappers to return their parameters in a NamedMap.
 */
public interface Parameterized {
	/**
	 * @return the parameters for the Parameterized instance wrapped in ParameterWrappers in a NamedMap.
	 */
	@NonNull NamedMap<ParameterWrapper> getParameters();
}
