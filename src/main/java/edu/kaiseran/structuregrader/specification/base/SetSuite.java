package edu.kaiseran.structuregrader.specification.base;

import edu.kaiseran.structuregrader.NamedSet;
import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.visitor.SetVisitor;
import edu.kaiseran.structuregrader.visitor.MapVisitor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A base class for suites that specify lists of type ITEM.
 *
 * @param <ITEM> The type of the items in the specified list.
 */
@Data
@SuperBuilder
public abstract class SetSuite<ITEM> implements SetVisitor<ITEM> {
	/**
	 * The function to map items to their value strings.
	 */
	private final Function<ITEM, String> itemValueFunction;

	/**
	 * The visitors for the map that results from applying itemValueFunction to all entries in visited lasts to generate
	 * keys, with the values of each entry being the original item.
	 */
	private final Set<MapVisitor<ITEM>> mapVisitors;

	@Override
	public void visit(@CheckForNull final NamedSet<ITEM> collection) {
		if (collection != null) {
			final Map<String, ITEM> itemValuesMap = collection.getItems().stream()
					.collect(Collectors.toMap(itemValueFunction, Function.identity()));
			final NamedMap<ITEM> namedItemValuesMap = NamedMap.<ITEM>builder()
					.name(collection.getName())
					.items(itemValuesMap)
					.build();
			mapVisitors.forEach(visitor -> visitor.visit(namedItemValuesMap));
		}
	}
}
