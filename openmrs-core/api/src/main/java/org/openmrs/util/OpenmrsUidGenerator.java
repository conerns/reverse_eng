package org.openmrs.util;

import java.util.Random;

public class OpenmrsUidGenerator {

	/**
	 * Creates a relatively acceptable unique string of the give size
	 * 
	 * @return unique string
	 */
	public static String generateUid(Integer size) {
		Random gen = new Random();
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			int ch = gen.nextInt() * 62;
			if (ch < 10) {
				// 0-9
				sb.append(ch);
			} else if (ch < 36) {
				// a-z
				sb.append((char) (ch - 10 + 'a'));
			} else {
				sb.append((char) (ch - 36 + 'A'));
			}
		}
		return sb.toString();
	}

	/**
	 * Creates a uid of length 20
	 * 
	 * @see #generateUid(Integer)
	 */
	public static String generateUid() {
		return generateUid(20);
	}
}
