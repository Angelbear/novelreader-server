package cn.com.sae.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidator {

	private Map<String, String[]> rules = new HashMap<String, String[]>();
	private boolean strictMode = false;

	public DataValidator(String[][] rules) {
		for (int i = 0; i < rules.length; i++) {
			this.rules.put(rules[i][0],
					Arrays.copyOfRange(rules[i], 1, rules[i].length));
		}
	}

	public DataValidator(String[][] rules, boolean strict) {
		this(rules);
		this.strictMode = strict;
	}

	private boolean _validateField(String src, String[] rules) {
		String rule = rules[0];
		System.out.println(src + " " + rule);
		if (rule.equals("Str")) {
			if (rules.length < 2)
				return true;
			String extra = rules[1];
			if (extra.equals("NotEmpty")) {
				return src != null && src.length() > 0;
			}
			return true;
		} else if (rule.equals("Int")) {
			try {
				Integer.parseInt(src);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else if (rule.equals("Bool")) {
			return (src.equals("0") || src.equals("1"));
		} else if (rules.equals("Regex")) {
			String pattern = rules[1];
			System.out.println(src + " " + pattern);
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(src);
			return m.matches();
		}
		return false;
	}

	public Map<String, String> validateHttpParamData(Map<String, String[]> data) {
		Iterator<Entry<String, String[]>> itr = rules.entrySet().iterator();
		Map<String, String> result = new HashMap<String, String>();
		while (itr.hasNext()) {
			Map.Entry<String, String[]> pair = (Map.Entry<String, String[]>) itr
					.next();
			String key = pair.getKey();
			String[] rules = pair.getValue();
			String param = data.get(key)[0];
			if (data.get(key) == null || data.get(key).length == 0
					|| !_validateField(param, rules)) {
				return null;
			} else {
				result.put(key, data.get(key)[0]);
			}
		}

		if (this.strictMode) {
			// TODO
		}
		return result;
	}

}
