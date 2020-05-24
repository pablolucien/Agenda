package org.pclg.misc;

import static org.pclg.Globals.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING;

class ToStringHashCodeTest {
	public static void main(final String[] args) {
		final ToStringHashCodeTest plainInstance = new ToStringHashCodeTest();
		final ToStringHashCodeTest overridedInstance = new ToStringHashCodeTest() {
			@Override
			public int hashCode() {
				return THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING;
			}
		};
		System.out.printf("%s\t%s\n", plainInstance.toString(), Integer.toHexString(plainInstance.hashCode()));
		System.out.printf("%s\t%s\n", overridedInstance.toString(), Integer.toHexString(overridedInstance.hashCode()));
	}
}