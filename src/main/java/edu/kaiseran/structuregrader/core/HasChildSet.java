package edu.kaiseran.structuregrader.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface HasChildSet {
	@JsonIgnore
	NamedSpecSet getChildSet();
}
