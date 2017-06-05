package com.universal.runner.srd;

import java.util.LinkedHashMap;
import java.util.Map;

public class SqlParserHelper {

	// sqlParserHelper.setMapKeySequnceValue(Map<String, Integer> map, String key);
	public void setMapKeySequnceValue(Map<String, Integer> map, String key) {
		if(map == null) {
			map = new LinkedHashMap<String, Integer>();
		}
		
		if(key != null) {
			
			if(map.get(key) != null) {
				map.put(key, ((Integer) map.get(key)) + 1);
			}
			else {
				map.put(key, 1);
			}
		}
	}
}
