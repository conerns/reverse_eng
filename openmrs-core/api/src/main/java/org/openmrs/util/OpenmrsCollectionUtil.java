package org.openmrs.util;

import java.util.*;

public class OpenmrsCollectionUtil {
	/**
	 * Compares origList to newList returning map of differences
	 * 
	 * @param origList
	 * @param newList
	 * @return [List toAdd, List toDelete] with respect to origList
	 */
	public static <E> Collection<Collection<E>> compareLists(Collection<E> origList, Collection<E> newList) {	
		Collection<Collection<E>> returnList = new ArrayList<>();
		
		Collection<E> toAdd = new LinkedList<>();
		Collection<E> toDel = new LinkedList<>();
		
		// loop over the new list.
		for (E currentNewListObj : newList) {
			// loop over the original list
			boolean foundInList = false;
			for (E currentOrigListObj : origList) {
				// checking if the current new list object is in the original
				// list
				if (currentNewListObj.equals(currentOrigListObj)) {
					foundInList = true;
					origList.remove(currentOrigListObj);
					break;
				}
			}
			if (!foundInList) {
				toAdd.add(currentNewListObj);
			}
			
			// all found new objects were removed from the orig list,
			// leaving only objects needing to be removed
			toDel = origList;
			
		}
		
		returnList.add(toAdd);
		returnList.add(toDel);
		
		return returnList;
	}

	/**
	 * @param collection
	 * @param elements
	 * @return Whether _collection_ contains any of _elements_
	 */
	public static <T> boolean containsAny(Collection<T> collection, Collection<T> elements) {
		for (T obj : elements) {
			if (collection.contains(obj)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Allows easy manipulation of a Map&lt;?, Set&gt;
	 */
	public static <K, V> void addToSetMap(Map<K, Set<V>> map, K key, V obj) {
		Set<V> set = map.computeIfAbsent(key, k -> new HashSet<>());
		set.add(obj);
	}

	public static <K, V> void addToListMap(Map<K, List<V>> map, K key, V obj) {
		List<V> list = map.computeIfAbsent(key, k -> new ArrayList<>());
		list.add(obj);
	}

	/**
	 * Loops over the collection to check to see if the given object is in that collection. This
	 * method <i>only</i> uses the .equals() method for comparison. This should be used in the
	 * patient/person objects on their collections. Their collections are SortedSets which use the
	 * compareTo method for equality as well. The compareTo method is currently optimized for
	 * sorting, not for equality. A null <code>obj</code> will return false
	 * 
	 * @param objects collection to loop over
	 * @param obj Object to look for in the <code>objects</code>
	 * @return true/false whether the given object is found
	 * <strong>Should</strong> use equals method for comparison instead of compareTo given List collection
	 * <strong>Should</strong> use equals method for comparison instead of compareTo given SortedSet collection
	 */
	public static boolean collectionContains(Collection<?> objects, Object obj) {
		if (obj == null || objects == null) {
			return false;
		}
		
		for (Object o : objects) {
			if (o != null && o.equals(obj)) {
				return true;
			}
		}
		
		return false;
	}
}
