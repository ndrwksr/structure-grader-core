package com.github.ndrwksr.structuregrader.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ndrwksr.structuregrader.core.visitor.Spec;

public interface HasChildMap<K> {
	@JsonIgnore
	NamedSpecMap<K, ? extends Spec> getChildMap();
}
