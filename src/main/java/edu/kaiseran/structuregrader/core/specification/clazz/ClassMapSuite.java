package edu.kaiseran.structuregrader.core.specification.clazz;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.core.NamedMap;
import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.specification.base.MapSuite;
import edu.kaiseran.structuregrader.core.visitor.*;
import edu.kaiseran.structuregrader.core.wrapper.ClassWrapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Contains specifications for a hierarchy of classes, including checking for missing/extra classes and specifications
 * for each class at the specified level in the hierarchy.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class ClassMapSuite extends MapSuite<ClassWrapper, ClassSuite> implements ClassHierarchyVisitor {

	/**
	 * @param collectionSpecs The specifications for the map of classes as a whole.
	 * @param itemSuites      The suites for the individual classes in the map.
	 * @param parentName      The name of the package or class to which these the specified map of classes belongs.
	 */
	@Builder
	@JsonCreator
	public ClassMapSuite(
			@NonNull @JsonProperty("collectionSpecs") final Set<MapVisitor<ClassWrapper>> collectionSpecs,
			@NonNull @JsonProperty("itemSuites") final Map<String, ClassSuite> itemSuites,
			@NonNull @JsonProperty("parentName") final String parentName
	) {
		super(collectionSpecs, itemSuites, parentName);
	}

	@Override
	public void visit(@Nullable final ClassWrapper classWrapper) {
		super.visit(classWrapper != null ? classWrapper.getDeclaredClasses() : null);
	}

	/**
	 * Factory class for ClassMapSuite. Has a list of ClassMapVisitorFactories provided by default to populate
	 * collectionSpecs, and uses the default ClassSuiteFactory to populate itemSuites.
	 */
	public static class ClassMapSuiteFactory extends MapSuiteFactory<ClassWrapper, ClassSuite> {

		/**
		 * @param collectionVisitorFactories The factories to be used to populate ClassMapSuite.collectionSpecs.
		 * @param itemSuiteFactory           The factories to be used to populate ClassMapSuite.itemSuites.
		 */
		@Builder
		public ClassMapSuiteFactory(
				@CheckForNull final ImmutableSet<MapVisitorFactory<ClassWrapper, ?>> collectionVisitorFactories,
				@CheckForNull final ItemVisitorFactory<ClassWrapper, ClassSuite> itemSuiteFactory
		) {
			super(collectionVisitorFactories, itemSuiteFactory, "classes");
		}

		/**
		 * A pre-made, default instance for consumers of ClassMapSuiteFactory to use.
		 */
		@NonNull
		public static ClassMapSuiteFactory getDefaultInst() {
			return ClassMapSuiteFactory.builder().build();
		}

		@Override
		protected ItemVisitorFactory<ClassWrapper, ClassSuite> getDefaultSuiteFactory() {
			return ClassSuite.ClassSuiteFactory.getDefaultInst(this);
		}

		@Override
		public ClassMapSuite buildFromCollection(
				@NonNull final NamedMap<ClassWrapper> classCollection,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final MapSuite<ClassWrapper, ClassSuite> superSuite = super.buildFromCollection(
					classCollection,
					parentName,
					noncomplianceConsumer
			);

			return new ClassMapSuite(
					superSuite.getCollectionSpecs(),
					superSuite.getItemSuites(),
					parentName
			);
		}
	}

	public static class JsonHelper {
		private static ObjectMapper getMapper(
				@Nullable final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final ObjectMapper objectMapper = new ObjectMapper()
					.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
					.registerModule(new GuavaModule());
			objectMapper.registerSubtypes(ClassMapSuite.class);

			final PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
					.allowIfSubType(List.class)
					.allowIfSubType(Set.class)
					.allowIfSubType(Map.class)
					.allowIfSubType(ItemVisitor.class)
					.allowIfSubType(MapVisitor.class)
					.allowIfSubType(SetVisitor.class)
					.build();
			objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);

			if (noncomplianceConsumer != null) {
				InjectableValues injectableValues = new InjectableValues.Std(
						Map.of("noncomplianceConsumer", noncomplianceConsumer)
				);
				objectMapper.setInjectableValues(injectableValues);
			}

			return objectMapper;
		}

		public static String toJson(
				@NonNull final ClassMapSuite classMapSuite
		) throws JsonProcessingException {
			return getMapper(null)
					.writerWithDefaultPrettyPrinter()
					.writeValueAsString(classMapSuite);
		}

		public static ClassMapSuite fromJson(
				@NonNull final String json,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) throws JsonProcessingException {
			return getMapper(noncomplianceConsumer).readValue(json, ClassMapSuite.class);
		}
	}
}
