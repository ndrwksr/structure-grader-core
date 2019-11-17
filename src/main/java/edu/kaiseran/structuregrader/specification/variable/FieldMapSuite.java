package edu.kaiseran.structuregrader.specification.variable;

import com.google.common.collect.ImmutableSet;
import edu.kaiseran.structuregrader.NamedMap;
import edu.kaiseran.structuregrader.Noncompliance;
import edu.kaiseran.structuregrader.property.Named;
import edu.kaiseran.structuregrader.specification.MapSuite;
import edu.kaiseran.structuregrader.specification.quantity.NoExtraSpec;
import edu.kaiseran.structuregrader.specification.quantity.NoMissingSpec;
import edu.kaiseran.structuregrader.specification.variable.FieldSuite.FieldSuiteFactory;
import edu.kaiseran.structuregrader.visitor.ClassVisitor;
import edu.kaiseran.structuregrader.visitor.ClassVisitorFactory;
import edu.kaiseran.structuregrader.visitor.MapVisitor;
import edu.kaiseran.structuregrader.visitor.MapVisitorFactory;
import edu.kaiseran.structuregrader.wrapper.ClassWrapper;
import edu.kaiseran.structuregrader.wrapper.FieldWrapper;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Specifies a map of fields, both specifying the collection as a whole and each individual element.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FieldMapSuite extends MapSuite<FieldWrapper, FieldSuite>
		implements ClassVisitor {

	/**
	 * @param collectionSpecs The specifications for the collection as a whole (missing, extra, etc.).
	 * @param itemSuites      The specifications for the individual fields.
	 * @param parentName      The name of the parent element (the class to which the fields belong).
	 */
	@Builder
	protected FieldMapSuite(
			@NonNull final Set<MapVisitor<FieldWrapper>> collectionSpecs,
			@NonNull final Map<String, FieldSuite> itemSuites,
			@NonNull final String parentName
	) {
		super(collectionSpecs, itemSuites, parentName, Named::getName);
	}

	@Override
	public void visit(@CheckForNull final NamedMap<FieldWrapper> collection) {
		if (collection != null) {
			super.collectionSpecs.forEach(spec -> spec.visit(collection));
			super.itemSuites.forEach((name, suite) -> suite.visit(collection.getItems().get(name)));
		}
	}

	@Override
	public void visit(@CheckForNull final ClassWrapper classWrapper) {
		if (classWrapper != null) {
			visit(classWrapper.getFields());
		}
	}

	/**
	 * A factory for FieldMapSuites. Has a FieldSuiteFactory to make suites for the individual fields in the product
	 * FieldMapSuite, and has a set of MapVisitorFactories to produce specifications for the collection of fields as a
	 * whole.
	 */
	public static class FieldMapSuiteFactory implements ClassVisitorFactory<FieldMapSuite> {
		/**
		 * The set of factories used to make specifications for the field map as a whole.
		 */
		private final Set<MapVisitorFactory<FieldWrapper, ?>> collectionVisitorFactories;

		/**
		 * The factory to use to make suites for the individual fields in the field map.
		 */
		private final FieldSuiteFactory fieldSuiteFactory;

		/**
		 * @return the set of default MapVisitorFactories for populating collectionVisitorFactories.
		 */
		private ImmutableSet<MapVisitorFactory<FieldWrapper, ?>> getDefaultCollectionVisitorFactories() {
			return ImmutableSet.of(
					NoExtraSpec.NoExtraSpecFactory.getDefaultInst(),
					NoMissingSpec.NoMissingSpecFactory.getDefaultInst()
			);
		}

		/**
		 * Default constructor. If collectionVisitorFactories is null, getDefaultVisitorFactories() will be used instead.
		 *
		 * @param collectionVisitorFactories The set of factories to be used for making collection specs.
		 * @param fieldSuiteFactory          The factory to be used to build suites for the individual fields.
		 */
		public FieldMapSuiteFactory(
				@CheckForNull final Set<MapVisitorFactory<FieldWrapper, ?>> collectionVisitorFactories,
				@CheckForNull final FieldSuiteFactory fieldSuiteFactory
		) {
			this.collectionVisitorFactories = collectionVisitorFactories != null ?
					collectionVisitorFactories :
					getDefaultCollectionVisitorFactories();

			this.fieldSuiteFactory = fieldSuiteFactory != null ?
					fieldSuiteFactory :
					FieldSuiteFactory.getDefaultInst();
		}

		/**
		 * @return a pre-configured instance for consumers of FieldMapSuiteFactory to use.
		 */
		public static FieldMapSuiteFactory getDefaultInst() {
			return new FieldMapSuiteFactory(null, null);
		}

		@Override
		public FieldMapSuite buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			// Specify the field map as a whole
			final NamedMap<FieldWrapper> fields = classWrapper.getFields();
			final String childrenNames = parentName + "%fields";
			final Set<MapVisitor<FieldWrapper>> collectionSpecs = collectionVisitorFactories.stream()
					.map(factory -> factory.buildFromCollection(fields, childrenNames, noncomplianceConsumer))
					.collect(Collectors.toSet());

			// Specify the individual fields
			final Map<String, FieldSuite> fieldSuites = fields.getItems().entrySet().stream()
					.collect(Collectors.toMap(
							Map.Entry::getKey,
							entry -> fieldSuiteFactory.buildFromItem(entry.getValue(), childrenNames, noncomplianceConsumer))
					);

			return FieldMapSuite.builder()
					.collectionSpecs(collectionSpecs)
					.itemSuites(fieldSuites)
					.parentName(parentName)
					.build();
		}
	}
}
