package com.duangframework.utils;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator implements Serializable {
	private static final long serialVersionUID = -8832409930574867162L;
	private final Pattern[] patterns;

	public RegexValidator(String regex) {
		this(regex, true);
	}

	public RegexValidator(String regex, boolean caseSensitive) {
		this(new String[] { regex }, caseSensitive);
	}

	public RegexValidator(String[] regexs) {
		this(regexs, true);
	}

	public RegexValidator(String[] regexs, boolean caseSensitive) {
		if ((regexs == null) || (regexs.length == 0)) {
			throw new IllegalArgumentException("Regular expressions are missing");
		}
		patterns = new Pattern[regexs.length];
		int flags = caseSensitive ? 0 : 2;
		for (int i = 0; i < regexs.length; i++) {
			if ((regexs[i] == null) || (regexs[i].length() == 0)) {
				throw new IllegalArgumentException("Regular expression[" + i + "] is missing");
			}
			patterns[i] = Pattern.compile(regexs[i], flags);
		}
	}

	public boolean isValid(String value) {
		if (value == null) {
			return false;
		}
		for (int i = 0; i < patterns.length; i++) {
			if (patterns[i].matcher(value).matches()) {
				return true;
			}
		}
		return false;
	}

	public String[] match(String value) {
		if (value == null) {
			return null;
		}
		for (int i = 0; i < patterns.length; i++) {
			Matcher matcher = patterns[i].matcher(value);
			if (matcher.matches()) {
				int count = matcher.groupCount();
				String[] groups = new String[count];
				for (int j = 0; j < count; j++) {
					groups[j] = matcher.group(j + 1);
				}
				return groups;
			}
		}
		return null;
	}

	public String validate(String value) {
		if (value == null) {
			return null;
		}
		for (int i = 0; i < patterns.length; i++) {
			Matcher matcher = patterns[i].matcher(value);
			if (matcher.matches()) {
				int count = matcher.groupCount();
				if (count == 1) {
					return matcher.group(1);
				}
				StringBuffer buffer = new StringBuffer();
				for (int j = 0; j < count; j++) {
					String component = matcher.group(j + 1);
					if (component != null) {
						buffer.append(component);
					}
				}
				return buffer.toString();
			}
		}
		return null;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("RegexValidator{");
		for (int i = 0; i < patterns.length; i++) {
			if (i > 0) {
				buffer.append(",");
			}
			buffer.append(patterns[i].pattern());
		}
		buffer.append("}");
		return buffer.toString();
	}
}
