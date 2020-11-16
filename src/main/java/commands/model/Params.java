package commands.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.StringLib;

public class Params {
	private static final String EQUALS = "=";
	public final String[] unnamed;
	public final Map<String, String> named;
	public final String[] all;
	
	public Params(List<String> namedParams, List<String> unnamedParams) {
		this.unnamed = unnamedParams.toArray(new String[unnamedParams.size()]);
		this.named = new HashMap<>();
		fillNamed(namedParams);
		all = all();
	}
	
	private void fillNamed(List<String> namedParams) {
		int index;
		String name, value;
		for (String namedParam : namedParams) {
			index = namedParam.indexOf(EQUALS);
			if (index != StringLib.NOT_FOUND) {
				name = namedParam.substring(0, index);
				value = namedParam.substring(index+1);
			} else {
				name = namedParam;
				value = "";
			}
			this.named.put(name,StringLib.unQuote(value));
		}
	}
	
	private String[] all() {
		List<String> params = new ArrayList<>();
		params.addAll(Arrays.asList(unnamed));
		params.addAll(named.keySet());
		return params.toArray(new String[params.size()]);
	}
	
	public String toString() {
		return new StringBuilder()
			.append(super.toString())
			.append("\nUnnamed="+Arrays.toString(unnamed))
			.append("\nNamed keys="+Arrays.toString(named.keySet().toArray()))
			.append("\nNamed vals="+Arrays.toString(named.values().toArray()))
			.toString();
	}
}