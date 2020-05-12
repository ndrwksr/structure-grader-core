package com.github.ndrwksr.structuregrader.core.wrapper;

import java.lang.reflect.Constructor;

/**
 * A wrapper for constructors which extends from ExecutableWrapper. Has a method that returns the signature of the
 * constructor.
 */
public class ConstructorWrapper extends ExecutableWrapper<Constructor> {
	/**
	 * @param sourceExecutable The constructor that this wrapper represents.
	 */
	public ConstructorWrapper(final Constructor sourceExecutable) {
		super(sourceExecutable);
	}

	/**
	 * @return the signature of this constructor. This method starts with the generic name of the constructor
	 * (ex. "public edu.example.Class(java.lang.String)", and removes the name of the class and the package prefix
	 * (ex. "public (java.lang.String)"). If the constructor is package private, the string "\<package private\>" is used
	 * as the modifier instead of leaving the modifier blank.
	 */
	public String getSignature() {
		// "public edu.example.Class(java.lang.String)"
		final String genericString = sourceExecutable.toGenericString();
    // ["public", "edu.example.Class(java.lang.String)"]
		final String[] splitGenericTokens = genericString.split(" ");

		// "public"
		final String modifier;
		// "edu.example.Class(java.lang.String)"
		final String prefixedSignature;

		if (splitGenericTokens.length == 1) {
			modifier = "<package private>";
			prefixedSignature = splitGenericTokens[0];
		} else {
			modifier = splitGenericTokens[0];
			prefixedSignature = splitGenericTokens[1];
		}

		// ["edu.example.Class", "java.lang.String)"]
		final String[] signatureTokens = prefixedSignature.split("\\(");
		// "(java.lang.String)"
		final String signature = '(' + signatureTokens[1];

		// "public (java.lang.String)"
		return modifier + " " + signature;
	}
}
