package edu.kaiseran.structuregrader;

import edu.kaiseran.structuregrader.specifications.MemberSpec;
import edu.kaiseran.structuregrader.specifications.MemberSpec.MemberSpecFactory;
import edu.test.proj1.SuperClass;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MemberSpecTest {
	private Member publicMember;
	private Member privateMember;
	private MemberSpecFactory<Member> factory;

	@Before
	public void setup() {
		publicMember = SuperClass.class.getDeclaredMethods()[0];
		privateMember = SuperClass.class.getDeclaredFields()[0];
		factory = new MemberSpecFactory<>();
		noncompliances.clear();
	}

	private final List<Noncompliance> noncompliances = new ArrayList<>();

	private final Consumer<Noncompliance> noncomplianceConsumer = (noncompliance) -> {
		noncompliances.add(noncompliance);
		System.out.println(this.getClass().getSimpleName() + ": " + noncompliance);
	};

	@Test
	public void privateWherePrivateExpected() {
		MemberSpec spec = factory.buildFromItem(
				privateMember,
				SuperClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		spec.visit(privateMember);

		assert noncompliances.isEmpty();
	}

	@Test
	public void publicWherePublicExpected() {
		MemberSpec spec = factory.buildFromItem(
				publicMember,
				SuperClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		spec.visit(publicMember);

		assert noncompliances.isEmpty();
	}

	@Test
	public void privateWherePublicExpected() {
		MemberSpec spec = factory.buildFromItem(
				publicMember,
				SuperClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		spec.visit(privateMember);

		assert noncompliances.size() == 1;
	}

	@Test
	public void publicWherePrivateExpected() {
		MemberSpec spec = factory.buildFromItem(
				privateMember,
				SuperClass.class.getSimpleName(),
				noncomplianceConsumer
		);

		spec.visit(publicMember);

		assert noncompliances.size() == 1;
	}
}
