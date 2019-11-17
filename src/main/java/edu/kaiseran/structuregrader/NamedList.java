package edu.kaiseran.structuregrader;


import edu.kaiseran.structuregrader.property.Named;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NamedList<ITEM> implements Named {
	private String name;
	private List<ITEM> items;
}
