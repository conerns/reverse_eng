package org.openmrs.util;

import java.sql.Timestamp;
import java.util.Date;

public class OpenmrsCompareUtil {
	public static <Arg1, Arg2 extends Arg1> boolean nullSafeEquals(Arg1 d1, Arg2 d2) {
		if (d1 == null) {
			return d2 == null;
		} else if (d2 == null) {
			return false;
		}
		return (d1 instanceof Date && d2 instanceof Date) ? compare((Date) d1, (Date) d2) == 0 : d1.equals(d2);
	}

	/**
	 * Compares two java.util.Date objects, but handles java.sql.Timestamp (which is not directly
	 * comparable to a date) by dropping its nanosecond value.
	 */
	public static int compare(Date d1, Date d2) {
		if (d1 instanceof Timestamp && d2 instanceof Timestamp) {
			return d1.compareTo(d2);
		}
		if (d1 instanceof Timestamp) {
			d1 = new Date(d1.getTime());
		}
		if (d2 instanceof Timestamp) {
			d2 = new Date(d2.getTime());
		}
		return d1.compareTo(d2);
	}

	/**
	 * Compares two Date/Timestamp objects, treating null as the earliest possible date.
	 */
	public static int compareWithNullAsEarliest(Date d1, Date d2) {
		if (d1 == null && d2 == null) {
			return 0;
		}
		if (d1 == null) {
			return -1;
		} else if (d2 == null) {
			return 1;
		} else {
			return compare(d1, d2);
		}
	}

	/**
	 * Compares two Date/Timestamp objects, treating null as the earliest possible date.
	 */
	public static int compareWithNullAsLatest(Date d1, Date d2) {
		if (d1 == null && d2 == null) {
			return 0;
		}
		if (d1 == null) {
			return 1;
		} else if (d2 == null) {
			return -1;
		} else {
			return compare(d1, d2);
		}
	}

	public static <E extends Comparable<E>> int compareWithNullAsLowest(E c1, E c2) {
		if (c1 == null && c2 == null) {
			return 0;
		}
		if (c1 == null) {
			return -1;
		} else if (c2 == null) {
			return 1;
		} else {
			return c1.compareTo(c2);
		}
	}

	public static <E extends Comparable<E>> int compareWithNullAsGreatest(E c1, E c2) {
		if (c1 == null && c2 == null) {
			return 0;
		}
		if (c1 == null) {
			return 1;
		} else if (c2 == null) {
			return -1;
		} else {
			return c1.compareTo(c2);
		}
	}
}
