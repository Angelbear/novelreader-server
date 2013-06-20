package cn.com.sae.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DataValidator {

	private Map<String, String> rules = new HashMap<String, String>();
	private boolean strictMode = false;

	public DataValidator(Map<String, String> rules) {
		this.rules = rules;
	}

	public DataValidator(Map<String, String> rules, boolean strict) {
		this(rules);
		this.strictMode = strict;
	}

	private boolean _validateField(String src, String rule) {
		return false;
	}
	
	public Map<String, String> validateHttpParamData(Map<String, String[]> data) {
		Iterator<Entry<String, String>> itr = rules.entrySet().iterator();
		Map<String, String> result = new HashMap<String, String>();
		while (itr.hasNext()) {
			Map.Entry<String, String> pair = (Map.Entry<String, String>) itr
					.next();
			String key = pair.getKey();
			String value = pair.getValue();
			if (data.get(key) == null || data.get(key).length == 0) {
				return null;
			} else {
				result.put(key, data.get(key)[0]);
				data.remove(key);
			}
		}

		if (this.strictMode) {

		}
		return result;
	}

}
