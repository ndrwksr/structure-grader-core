package com.github.ndrwksr.structuregrader;

import com.github.ndrwksr.structuregrader.core.NamedMap;
import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.property.Ordinal;
import com.github.ndrwksr.structuregrader.core.specification.collection.OrderedListSpec;
import com.github.ndrwksr.structuregrader.core.specification.collection.OrderedListSpec.OrderedListSpecFactory;
import com.github.ndrwksr.structuregrader.core.specification.collection.OrderedListSpec.OrdinalNoncompliance;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class OrderedListSpecTest {
	private static final String PARENT_NAME = "parent name";

	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};

	@Before
	public void setup() {
		noncompliances.clear();
	}

	private boolean noncomplianceOfTypeWasMade(
			final Class<? extends Noncompliance> noncomplianceClass
	) {
		return noncompliances.stream().anyMatch(noncomplianceClass::isInstance);
	}

	@AllArgsConstructor
	static class OrdinalImpl implements Ordinal {
		private final int index;

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public @NonNull String getName() {
			return "name";
		}

		public static OrdinalImpl of(final int index) {
			return new OrdinalImpl(index);
		}
	}

	private final NamedMap<Ordinal> baseMap = NamedMap.<Ordinal>builder()
			.items(Map.of(
					"0", OrdinalImpl.of(0),
					"1", OrdinalImpl.of(1),
					"2", OrdinalImpl.of(2)
			))
			.name("baseMap")
			.build();

	private final NamedMap<Ordinal> identicalMap = NamedMap.<Ordinal>builder()
			.items(Map.of(
					"0", OrdinalImpl.of(0),
					"1", OrdinalImpl.of(1),
					"2", OrdinalImpl.of(2)
			))
			.name("identicalMap")
			.build();

	private final NamedMap<Ordinal> outOfOrderMap = NamedMap.<Ordinal>builder()
			.items(Map.of(
					"0", OrdinalImpl.of(2),
					"1", OrdinalImpl.of(0),
					"2", OrdinalImpl.of(1)
			))
			.name("outOfOrderMap")
			.build();

	private final NamedMap<Ordinal> missingOneMap = NamedMap.<Ordinal>builder()
			.items(Map.of(
					"0", OrdinalImpl.of(0),
					"1", OrdinalImpl.of(1)
			))
			.name("missingOneMap")
			.build();

	private final NamedMap<Ordinal> extraOneMap = NamedMap.<Ordinal>builder()
			.items(Map.of(
					"0", OrdinalImpl.of(0),
					"1", OrdinalImpl.of(1),
					"2", OrdinalImpl.of(2),
					"3", OrdinalImpl.of(3)
			))
			.name("extraOneMap")
			.build();

	private final OrderedListSpecFactory<Ordinal> factory = OrderedListSpecFactory.getDefaultInst();

	@Test
	public void sameMap() {
		final OrderedListSpec<Ordinal> spec = factory.buildFromCollection(baseMap, PARENT_NAME, noncomplianceConsumer);
		spec.visit(baseMap);
		assert noncompliances.isEmpty();
	}

	@Test
	public void identicalMap() {
		final OrderedListSpec<Ordinal> spec = factory.buildFromCollection(baseMap, PARENT_NAME, noncomplianceConsumer);
		spec.visit(identicalMap);
		assert noncompliances.isEmpty();
	}

	@Test
	public void outOfOrderMap() {
		final OrderedListSpec<Ordinal> spec = factory.buildFromCollection(baseMap, PARENT_NAME, noncomplianceConsumer);
		spec.visit(outOfOrderMap);
		assert !noncompliances.isEmpty();
		assert noncomplianceOfTypeWasMade(OrdinalNoncompliance.class);
	}

	@Test
	public void missingOneMap() {
		final OrderedListSpec<Ordinal> spec = factory.buildFromCollection(baseMap, PARENT_NAME, noncomplianceConsumer);
		spec.visit(missingOneMap);
		assert noncompliances.isEmpty();
	}

	@Test
	public void extraOneMap() {
		final OrderedListSpec<Ordinal> spec = factory.buildFromCollection(baseMap, PARENT_NAME, noncomplianceConsumer);
		spec.visit(extraOneMap);
		assert noncompliances.isEmpty();
	}
}
