package edu.kaiseran.structuregrader;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
public class NamedCollection<ITEM> {
	protected final String name;
	protected final Map<String, ITEM> items;
}
