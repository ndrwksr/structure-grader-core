package edu.kaiseran.structuregrader;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import edu.kaiseran.structuregrader.wrappers.ClassWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A collection of ClassStructure objects with its own name. Can be visited by any
 * ClassCollectionVisitors.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ClassCollection extends NamedCollection<ClassWrapper> implements Named {

	/**
	 * Factory method. Returns a ClassCollection containing all top-level classes in the provided package.
	 *
	 * @param pkg The name of the package to build the ClassCollection from.
	 * @return a ClassCollection containing all top-level classes in the provided package.
	 * @throws IOException If ClassPath cannot access the top level classes of pkg.
	 */
	@SuppressWarnings({"UnstableApiUsage", "WeakerAccess"})
	// ClassPath is marked as @Beta, so it's fine for applications
	public static ClassCollection buildFromPackage(
			@NonNull final String pkg
	) throws IOException {
		final ClassPath classPath = ClassPath.from(ClassCollection.class.getClassLoader());

		// Get a map of all of the classes in pkg, where the key is the class's name and the value is
		// a ClassStructure representing the class.
		final Map<String, ClassWrapper> declaredClasses =
				classPath.getTopLevelClassesRecursive(pkg).stream()
						.map(ClassInfo::load)
						.map(ClassWrapper::buildFrom)
						.collect(Collectors.toMap(
								ClassWrapper::getName, Function.identity())
						);

		return ClassCollection.builder().items(declaredClasses).name(pkg).build();
	}
}
