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

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Enforces that the specified collection has only the classes specified. Can visit ClassWrappers (which will result
 * in visiting the class's inner ClassMap) or ClassMaps directly.
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NoExtraClassesMapSpec extends NoExtraMapSpec<ClassWrapper> implements ClassHierarchyVisitor {

	@JsonCreator
	public static NoExtraClassesMapSpec jsonCreateClass(
			@NonNull @JsonProperty("parentName") final String parentName,
			@NonNull @JsonProperty("expectedItemNames") final Set<String> expectedItemNames,
			@NonNull @JsonProperty("itemTypePlural") final String itemTypePlural,
			@NonNull @JacksonInject("noncomplianceConsumer") final Consumer<Noncompliance> noncomplianceConsumer
	) {
		return NoExtraClassesMapSpec.builder()
				.itemTypePlural(itemTypePlural)
				.parentName(parentName)
				.expectedItemNames(expectedItemNames)
				.noncomplianceConsumer(noncomplianceConsumer)
				.build();
	}

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		super.visit(classWrapper != null ? classWrapper.getDeclaredClasses() : null);
	}

	/**
	 * Creates NoExtraClassesSpecs which only permits the classes present in the provided class collection.
	 * Has no state/configuration.
	 */
	public static class NoExtraClassesSpecFactory implements ClassHierarchyVisitorFactory<NoExtraClassesMapSpec> {

		/**
		 * @return a pre-configured instance for consumers of NoExtraClassesSpecFactory to use.
		 */
		public static NoExtraClassesSpecFactory getDefaultInst() {
			return new NoExtraClassesSpecFactory();
		}

		@Override
		public NoExtraClassesMapSpec buildFromCollection(
				@NonNull final NamedMap<ClassWrapper> classCollection,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final Set<String> declaredClassNames = new HashSet<>(classCollection.getItems().keySet());

			return NoExtraClassesMapSpec.builder()
					.expectedItemNames(declaredClassNames)
					.parentName(parentName)
					.noncomplianceConsumer(noncomplianceConsumer)
					.itemTypePlural("classes")
					.build();
		}

		@Override
		public NoExtraClassesMapSpec buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return buildFromCollection(classWrapper.getDeclaredClasses(), classWrapper.getName(), noncomplianceConsumer);
		}
	}
}
