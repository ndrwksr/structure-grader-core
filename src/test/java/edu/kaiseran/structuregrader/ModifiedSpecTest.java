package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.core.Noncompliance;
import edu.kaiseran.structuregrader.core.property.Modified;
import edu.kaiseran.structuregrader.core.specification.common.ModifiedSpec;
import edu.kaiseran.structuregrader.core.specification.common.ModifiedSpec.ModifiedSpecFactory;
import edu.test.proj1.SuperClass;
import lombok.NonNull;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModifiedSpecTest {
	private Member publicMember;
	private Member privateMember;
	private Modified privateModified;
	private Modified publicModified;
	private ModifiedSpecFactory<Modified> factory;

	@Before
	public void setup() {
		publicMember = SuperClass.class.getDeclaredMethods()[0];
		privateMember = SuperClass.class.getDeclaredFields()[0];
		publicModified = memberToModified(publicMember);
		privateModified = memberToModified(privateMember);
		factory = new ModifiedSpecFactory<>();
		noncompliances.clear();
	}

	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};

	private Modified memberToModified(final Member member) {
		return new Modified() {
			@Override
			public int getModifiers() {
				return member.getModifiers();
			}

			@Override
			public boolean isSynthetic() {
				return member.isSynthetic();
			}

			@Override
			public @NonNull String getName() {
				return member.getName();
			}
		};
	}


	@Test
	public void privateWherePrivateExpected() {
		Modified modified = memberToModified(privateMember);
		ModifiedSpec spec = factory.buildFromItem(
				modified,
				SuperClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		spec.visit(modified);

		assert noncompliances.isEmpty();
	}

	@Test
	public void publicWherePublicExpected() {
		Modified modified = memberToModified(privateMember);

		ModifiedSpec spec = factory.buildFromItem(
				publicModified,
				SuperClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		spec.visit(publicModified);

		assert noncompliances.isEmpty();
	}

	@Test
	public void privateWherePublicExpected() {
		ModifiedSpec spec = factory.buildFromItem(
				publicModified,
				SuperClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		spec.visit(privateModified);

		assert noncompliances.size() == 1;
	}

	@Test
	public void publicWherePrivateExpected() {
		ModifiedSpec spec = factory.buildFromItem(
				privateModified,
				SuperClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		spec.visit(publicModified);

		assert noncompliances.size() == 1;
	}
}
