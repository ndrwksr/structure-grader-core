package com.github.ndrwksr.structuregrader.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ndrwksr.structuregrader.core.visitor.Spec;
import com.github.ndrwksr.structuregrader.core.property.Named;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class NamedSpecMap<K, SPEC extends Spec> extends AbstractMap<K, SPEC> implements Named {

	@NonNull
	private final Map<K, SPEC> items;

	@NonNull
	private final String name;

	@Builder
	@JsonCreator
	public NamedSpecMap(
			@NonNull @JsonProperty final Map<K, SPEC> items,
			@NonNull @JsonProperty final String name
	) {
		this.items = items;
		this.name = name;
	}

	@Override
	public @NonNull String getName() {
		return name;
	}

	@Override
	public Set<Entry<K, SPEC>> entrySet() {
		return items.entrySet();
	}
}
