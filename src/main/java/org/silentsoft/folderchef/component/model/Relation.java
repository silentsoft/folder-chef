package org.silentsoft.folderchef.component.model;

import java.util.HashMap;
import java.util.Map;

public class Relation {

	private Map<String, Map<String, Integer>> map;
	
	public Relation() {
		map = new HashMap<String, Map<String, Integer>>();
	}
	
	public Map<String, Integer> getRelation(String keyword) {
		return map.get(keyword);
	}
	
	public void setRelation(String target, String keyword) {
		int count = 1;
		Map<String, Integer> relation;
		
		if (map.containsKey(target)) {
			relation = map.get(target);
			Integer value = relation.get(keyword);
			count += (value == null) ? 0 : value;
		} else {
			relation = new HashMap<String, Integer>();
		}
		
		relation.put(keyword, count);
		map.put(target, relation);
	}
}
