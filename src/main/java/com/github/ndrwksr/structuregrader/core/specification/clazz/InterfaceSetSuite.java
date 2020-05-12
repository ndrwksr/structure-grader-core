package com.github.ndrwksr.structuregrader.core.specification.clazz;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ndrwksr.structuregrader.core.HasChildSet;
import com.github.ndrwksr.structuregrader.core.NamedSet;
import com.github.ndrwksr.structuregrader.core.NamedSpecSet;
import com.github.ndrwksr.structuregrader.core.Noncompliance;
import com.github.ndrwksr.structuregrader.core.specification.collection.NoExtraSetSpec.NoExtraSetSpecFactory;
import com.github.ndrwksr.structuregrader.core.specification.collection.NoMissingSetSpec;
import com.github.ndrwksr.structuregrader.core.visitor.ClassVisitor;
import com.github.ndrwksr.structuregrader.core.visitor.ClassVisitorFactory;
import com.github.ndrwksr.structuregrader.core.visitor.SetVisitor;
import com.github.ndrwksr.structuregrader.core.visitor.SetVisitorFactory;
import com.github.ndrwksr.structuregrader.core.wrapper.ClassWrapper;
import com.google.common.collect.ImmutableSet;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Specifies the set of names of the interfaces that a class is expected to implement.
 */
@Builder
@Getter
public class InterfaceSetSuite implements SetVisitor<ClassWrapper>, ClassVisitor, HasChildSet {
	/**
	 * The visitors to the specified set of interface names.
	 */
	@NonNull
	private final Set<SetVisitor<String>> setVisitors;

	/**
	 * The name of the class this suite specifies.
	 */
	@NonNull
	private final String parentName;

	public InterfaceSetSuite(
			@NonNull @JsonProperty("setVisitors") final Set<SetVisitor<String>> setVisitors,
			@NonNull @JsonProperty("parentName") final String parentName
	) {
		this.setVisitors = setVisitors;
		this.parentName = parentName;
	}

	@Override
	public void visit(@CheckForNull final NamedSet<ClassWrapper> interfaces) {
		if (interfaces != null) {
			NamedSet<String> actualNameSet = NamedSet.<String>builder()
					.name(interfaces.getName())
					.items(interfaces.getItems().stream()
							.map(ClassWrapper::getName)
							.collect(Collectors.toSet())
					)
					.build();

			setVisitors.forEach(setVisitor -> setVisitor.visit(actualNameSet));
		}
	}

	@Override
	public void visit(@CheckForNull final ClassWrapper classWrapper) {
		if (classWrapper != null) {
			this.visit(classWrapper.getInterfaces());
		}
	}

	@Override
	public NamedSpecSet getChildSet() {
		return NamedSpecSet.<SetVisitor<String>>builder()
				.items(setVisitors)
				.name(".setVisitors")
				.build();
	}

	@Override
	public String getSpecDescription() {
		return "A suite that dictates the interfaces a class must implement.";
	}

	/**
	 * Creates new InterfaceMapSuites.
	 */
	public static class InterfaceMapSuiteFactory implements SetVisitorFactory<ClassWrapper, InterfaceSetSuite>,
			ClassVisitorFactory<InterfaceSetSuite> {

		public static final String ITEM_TYPE_PLURAL = "interfaces";
		/**
		 * The factories used to make SetVisitors to populate InterfaceMapSuite.setVisitors.
		 */
		private final ImmutableSet<SetVisitorFactory<String, ?>> setVisitorFactories;

		/**
		 * @param setVisitorFactories The factories used to make SetVisitors to populate InterfaceMapSuite.setVisitors. If
		 *                            null, the defaults will be used.
		 */
		@Builder
		public InterfaceMapSuiteFactory(
				@Nullable final ImmutableSet<SetVisitorFactory<String, ?>> setVisitorFactories
		) {
			this.setVisitorFactories = setVisitorFactories != null ?
					setVisitorFactories :
					getDefaultSetVisitorFactories();
		}

		/**
		 * @return the default SetVisitorFactories to populate setVisitorFactories.
		 */
		private static ImmutableSet<SetVisitorFactory<String, ?>> getDefaultSetVisitorFactories() {
			return ImmutableSet.of(
					NoMissingSetSpec.NoMissingSetSpecFactory.getDefaultInst(ITEM_TYPE_PLURAL),
					NoExtraSetSpecFactory.getDefaultInst(ITEM_TYPE_PLURAL)
			);
		}

		/**
		 * @return a pre-configured instance for consumers of InterfaceMapSuiteFactory to use.
		 */
		public static InterfaceMapSuiteFactory getDefaultInst() {
			return new InterfaceMapSuiteFactory(null);
		}

		@Override
		public InterfaceSetSuite buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			return buildFromCollection(classWrapper.getInterfaces(), parentName, noncomplianceConsumer);
		}

		@Override
		public InterfaceSetSuite buildFromCollection(
				@NonNull final NamedSet<ClassWrapper> namedSet,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final NamedSet<String> expectedNames = NamedSet.<String>builder()
					.name(parentName)
					.items(namedSet.getItems().stream()
							.map(ClassWrapper::getName)
							.collect(Collectors.toSet()))
					.build();

			final Set<SetVisitor<String>> setVisitors = setVisitorFactories.stream()
					.map(factory -> factory.buildFromCollection(expectedNames, parentName, noncomplianceConsumer))
					.collect(Collectors.toSet());

			return InterfaceSetSuite.builder()
					.setVisitors(setVisitors)
					.parentName(parentName)
					.build();
		}
	}
}
