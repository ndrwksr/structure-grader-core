package com.github.ndrwksr.structuregrader.core.specification.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.visitor.MapVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Abstract class which stores a Noncompliance consumer, the name of the parent item of this spec, and the set of
 * expected values for the collection. This set is often the set of names of the elements which should be present.
 * It is up to subclasses to choose what goes in the expected set and how to use it.
 *
 * @param <ITEM> The type of the item in the specified collection.
 * @param <EXPECTED> The type of the expected values in the collection, usually String.
 */
@Data
@SuperBuilder
@EqualsAndHashCode
public abstract class MapSpec<ITEM, EXPECTED> implements MapVisitor<ITEM> {
	/**
	 * Accepts any generated noncompliances, decoupling the consumption of noncompliances from their
	 * creation.
	 */
	@JsonIgnore
	protected final Consumer<Noncompliance> noncomplianceConsumer;

	/**
	 * The name of the parent of the specified element.
	 */
	protected final String parentName;

	/**
	 * The set of expected values for the collection.
	 */
	protected final Set<EXPECTED> expectedItemNames;
}
