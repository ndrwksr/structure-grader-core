package com.github.ndrwksr.structuregrader.core.property;

import com.github.ndrwksr.structuregrader.core.NamedMap;
import com.github.ndrwksr.structuregrader.core.wrapper.ParameterWrapper;
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
