package com.bwc.common.vo;

import org.apache.commons.collections4.map.ListOrderedMap;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : HMap.java
 * @Description : Able HMap
 * @author BW1005
 * @since 2023. 10. 11.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2023. 10. 11.     BW1005         최초 생성
 * </pre>
 */
public class HMap extends ListOrderedMap {
	private static final long serialVersionUID = 1L;

	public String getString(String key) {
		String result = "";
		if (null != this.get(key)) {
			result = (String)this.get(key);
		}

		return result;
	}

	public Object put(Object key, Object value) {
		return super.put(convert2CamelCase((String)key), value);
	}

	public Object put2(Object key, Object value) {
		return super.put(convert3CamelCase((String)key), value);
	}

	public Object put3(Object key, Object value) {
		return super.put(key, value);
	}

	public static String convert2CamelCase(String underScore) {
		if (underScore.indexOf(95) < 0 && Character.isLowerCase(underScore.charAt(0))) {
			return underScore;
		} else {
			StringBuilder result = new StringBuilder();
			boolean nextUpper = false;
			int len = underScore.length();

			for (int i = 0; i < len; ++i) {
				char currentChar = underScore.charAt(i);
				if (currentChar == '_') {
					nextUpper = true;
				} else if (nextUpper) {
					result.append(Character.toUpperCase(currentChar));
					nextUpper = false;
				} else {
					result.append(Character.toLowerCase(currentChar));
				}
			}

			return result.toString();
		}
	}

	public static String convert3CamelCase(String underScore) {
		if (underScore.indexOf(95) < 0) {
			return underScore;
		} else {
			StringBuilder result = new StringBuilder();
			boolean nextUpper = false;
			int len = underScore.length();

			for (int i = 0; i < len; ++i) {
				char currentChar = underScore.charAt(i);
				if (currentChar == '_') {
					nextUpper = true;
				} else if (nextUpper) {
					result.append(Character.toUpperCase(currentChar));
					nextUpper = false;
				} else {
					result.append(currentChar);
				}
			}

			return result.toString();
		}
	}
}
