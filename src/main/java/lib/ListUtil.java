package lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class ListUtil {
	public static <R, T extends Collection<R>> List<R> subset(T elements, int start, int count) {
		if (start < 0) 
			return new ArrayList<>();
		List<R> subset = new ArrayList<>(Math.min(elements.size(), count));
		Iterator<R> iterator = elements.iterator();
		int i=0;
		while (iterator.hasNext()) {
			R element = iterator.next();
			if (start <= i && i < start+count) subset.add(element);
			else if (i >= start+count) break;
			i++;
		}
		return subset;
	}
	
	public static <T extends Comparable<T>> Comparator<T> ascendingOrder() {
		return new Comparator<T>() {
			@Override
			public int compare(T i1, T i2) {
				return i1.compareTo(i2);	// reverse order
			}
		};
	}
	
	public static <T extends Comparable<T>> Comparator<T> descendingOrder() {
		return new Comparator<T>() {
			@Override
			public int compare(T i1, T i2) {
				return -i1.compareTo(i2);	// reverse order
			}
		};
	}
	
	public static <T> int max(List<T> elements, Function<T, Integer> mapper) {
		int max = 0, pos = -1;
		for (int i=0, current; i<elements.size(); i++) {
			current = mapper.apply(elements.get(i));
			if (current > max) {
				max = current;
				pos = i;
			}
		}
		return pos;
	}
	
	public static void main(String[] args) {
		List<Integer> nums = Arrays.asList(new Integer[] {0,1,2,3,4,5,6});
		System.out.println(Arrays.toString(subset(nums, 5, 4).toArray()));
	}
}
