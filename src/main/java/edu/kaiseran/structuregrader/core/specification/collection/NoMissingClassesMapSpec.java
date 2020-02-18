package edu.kaiseran.structuregrader.core.specification.collection;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kaiseran.structuregrader.core.NamedMap;
import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.visitor.ClassHierarchyVisitor;
import edu.kaiseran.structuregrader.core.visitor.ClassHierarchyVisitorFactory;
import edu.kaiseran.structuregrader.core.wrapper.ClassWrapper;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.annotation.CheckForNull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Enforces that the specified collection has all of the classes specified. Can visit ClassWrappers (which will result
 * in visiting the class's ClassMaps) or ClassMaps directly.
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NoMissingClassesMapSpec extends NoMissingMapSpec<ClassWrapper> implements ClassHierarchyVisitor {

	@JsonCreator
	public static NoMissingClassesMapSpec jsonCreateClass(
			@NonNull @JsonProperty("parentName") final String parentName,
			@NonNull @JsonProperty("expectedItemNames") final Set<String> expectedItemNames,
			@NonNull @JsonProperty("itemTypePlural") final String itemTypePlural,
			@NonNull @JacksonInject("noncomplianceConsumer") final Consumer<Noncompliance> noncomplianceConsumer
	) {
		return NoMissingClassesMapSpec.builder()
				.itemTypePlural(itemTypePlural)
				.parentName(parentName)
				.expectedItemNames(expectedItemNames)
				.noncomplianceConsumer(noncomplianceConsumer)
				.build();
	}

	@Override
	public void visit(@CheckForNull final ClassWrapper classWrapper) {
		super.visit(classWrapper != null ? classWrapper.getDeclaredClasses() : null);
	}

	/**
	 * Creates NoMissingClassesSpecs which mandate the presence of the classes in the specified class map.
	 * Has no state/configuration.
	 */
	public static class NoMissingClassesSpecFactory implements ClassHierarchyVisitorFactory<NoMissingClassesMapSpec> {

		/**
		 * @return a pre-configured instance for consumers of NoMissingClassesSpecFactory to use.
		 */
		public static NoMissingClassesSpecFactory getDefaultInst() {
			return new NoMissingClassesSpecFactory();
		}

		@Override
		public NoMissingClassesMapSpec buildFromCollection(
				@NonNull final NamedMap<ClassWrapper> namedMap,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Set<String> declaredClassNames = new HashSet<>(namedMap.getItems().keySet());

			return NoMissingClassesMapSpec.builder()
					.expectedItemNames(declaredClassNames)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.itemTypePlural("classes")
					.build();
		}

		@Override
		public NoMissingClassesMapSpec buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return buildFromCollection(classWrapper.getDeclaredClasses(), classWrapper.getName(), noncomplianceConsumer);
		}
	}
}
