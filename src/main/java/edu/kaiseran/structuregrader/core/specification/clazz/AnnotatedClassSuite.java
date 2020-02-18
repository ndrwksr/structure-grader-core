package edu.kaiseran.structuregrader.core.specification.clazz;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.specification.base.MapSpec;
import edu.kaiseran.structuregrader.core.specification.common.AnnotatedSuite;
import edu.kaiseran.structuregrader.core.visitor.ClassVisitor;
import edu.kaiseran.structuregrader.core.visitor.ClassVisitorFactory;
import edu.kaiseran.structuregrader.core.wrapper.AnnotationWrapper;
import edu.kaiseran.structuregrader.core.wrapper.ClassWrapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A subclass of AnnotatedSuite whose type is ClassWrapper that also implements ClassVisitor. This is mostly just a type
 * alias to allow reuse of AnnotatedSuite.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class AnnotatedClassSuite extends AnnotatedSuite<ClassWrapper> implements ClassVisitor {
	/**
	 * Simple constructor to match super.
	 *
	 * @param collectionSpecs The specifications for the specified collection of annotations as a whole.
	 * @param parentName      The name of the parent element of the specified item.
	 */
	@JsonCreator
	public AnnotatedClassSuite(
			@NonNull @JsonProperty("collectionSpecs") final Set<MapSpec<AnnotationWrapper, String>> collectionSpecs,
			@NonNull @JsonProperty("parentName") final String parentName
	) {
		super(collectionSpecs, parentName);
	}

	/**
	 * A factory for making AnnotatedSuites for ClassWrappers. Can't just be an AnnotatedSuiteFactory\<ClassWrapper\>
	 * because it must implement ClassVisitorFactory. Uses an AnnotatedSuiteFactory instance to make AnnotatedSuites,
	 * then builds AnnotatedClassSuites from the returned AnnotatedSuites.
	 */
	public static class AnnotatedClassSuiteFactory implements ClassVisitorFactory<AnnotatedClassSuite> {
		/**
		 * The factory that this factory is essentially just a wrapper around. It is used to generate AnnotatedSuites whose
		 * fields are then used to make an AnnotatedClassSuite without any further modification.
		 */
		private final AnnotatedSuiteFactory<ClassWrapper> annotatedSuiteFactory;

		/**
		 * @return a pre-configured instance for consumers of AnnotatedClassSuiteFactory to use.
		 */
		public static AnnotatedClassSuiteFactory getDefaultInst() {
			return new AnnotatedClassSuiteFactory(null);
		}

		/**
		 * @param annotatedSuiteFactory The factory to use to make the AnnotatedSuites which are then returned as
		 *                              AnnotatedClassSuites. If null, the default (AnnotatedSuiteFactory.getDefaultInst())
		 *                              will be used.
		 */
		@Builder
		public AnnotatedClassSuiteFactory(@Nullable final AnnotatedSuiteFactory<ClassWrapper> annotatedSuiteFactory) {
			this.annotatedSuiteFactory = annotatedSuiteFactory != null ?
					annotatedSuiteFactory :
					AnnotatedSuiteFactory.getDefaultInst();
		}

		@Override
		public AnnotatedClassSuite buildFromItem(
				@NonNull final ClassWrapper classWrapper,
				@NonNull final String parentName,
				@NonNull final Consumer<Noncompliance> noncomplianceConsumer
		) {
			final AnnotatedSuite<ClassWrapper> annotatedSuite =
					annotatedSuiteFactory.buildFromItem(classWrapper, parentName, noncomplianceConsumer);
			return new AnnotatedClassSuite(
					annotatedSuite.getCollectionSpecs(),
					annotatedSuite.getParentName()
			);
		}
	}
}
