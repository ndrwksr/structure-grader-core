package edu.kaiseran.structuregrader.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.kaiseran.structuregrader.core.visitor.Spec;

public interface HasChildMap<K> {
	@JsonIgnore
	NamedSpecMap<K, ? extends Spec> getChildMap();
}
