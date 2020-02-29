package edu.kaiseran.structuregrader.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kaiseran.structuregrader.core.property.Named;
import edu.kaiseran.structuregrader.core.visitor.Spec;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class NamedSpecSet<SPEC extends Spec> extends AbstractSet<SPEC> implements Named {

	@NonNull
	private final String name;

	@NonNull
	private final Set<SPEC> items;

	@Builder
	@JsonCreator
	public NamedSpecSet(
			@NonNull @JsonProperty("name") final String name,
			@NonNull @JsonProperty("items") final Set<SPEC> items
	) {
		this.name = name;
		this.items = items;
	}

	@Override
	public @NonNull String getName() {
		return name;
	}

	@Override
	public Iterator<SPEC> iterator() {
		return items.iterator();
	}

	@Override
	public int size() {
		return items.size();
	}
}
