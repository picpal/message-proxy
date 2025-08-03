package com.bwc.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import com.bwc.bluepay.zomm.eai.data.DataSet; // 삭제된 의존성
import com.bwc.common.constant.SendStatus;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : Converter.java
 * @Description : 클래스 설명을 기술합니다.
 * @author BWC072
 * @since 2018. 8. 9.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2018. 8. 9.     BWC101         최초 생성
 * </pre>
 */

public class Converter {

	protected static Logger logger = LoggerFactory.getLogger("Converter.class");

	/**
	 * Object를 -> DataSet 변환
	 * Statements
	 *
	 * @param obj
	 * @return
	 */
	// TODO: DataSet 의존성 제거로 인해 비활성화
	/*
	public static DataSet convertObjectToMap(Object obj) {
		DataSet dataSet = new DataSet();

		Field[] fields = obj.getClass().getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);

			try {

				String fildNm = changeToMap(fields[i].getName());
				//System.out.println("fildNm ::" + fildNm);

				String fildValue = (String)fields[i].get(obj);
				if (fildValue == null || "".equals(fildValue)) {
					fildValue = "";
				}
				//System.out.println("fildValue ::" + fildValue);

				dataSet.put(fildNm, fildValue);
			} catch (Exception e) {
				logger.error("convertObjectToMap Err :" + e);
			}
		}
		return dataSet;
	}
	*/

	/**
	 * DataSet -> Object 로 변환
	 * Statements
	 *
	 * @param dataSet
	 * @param obj
	 * @return
	 */
	/*
	public static Object convertObjectToObject(DataSet dataSet, Object obj) {
		String keyAttribute = null;
		String setMethodString = "set";
		String methodString = null;

		Iterator<String> itr = dataSet.keySet().iterator();

		while (itr.hasNext()) {
			keyAttribute = (String)itr.next();
			//System.out.println("keyAttribute ::" + keyAttribute);

			String key = changeToVO(keyAttribute);

			methodString = setMethodString + key.substring(0, 1).toUpperCase() + key.substring(1);
			//System.out.println("methodString ::" + methodString);

			Method[] methods = obj.getClass().getDeclaredMethods();

			for (int i = 0; i < methods.length; i++) {

				if (methodString.equals(methods[i].getName())) {

					//System.out.println(methodString +":" + methods[i].getName());

					try {
						methods[i].invoke(obj, dataSet.get(keyAttribute));
					} catch (Exception e) {
						logger.error("convertObjectToObject Err :" + e);
					}
				}
			}
		}

		return obj;
	}
	*/

	public static Object convertObject(Object mapObj, Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		Object result = null;
		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);

			try {

				String fildNm = changeToMap(fields[i].getName());
				System.out.println("fildNm ::" + fildNm);

				String fildValue = (String)fields[i].get(obj);
				if (fildValue == null || "".equals(fildValue)) {
					fildValue = "";
				}
				System.out.println("fildValue ::" + fildValue);

				Method[] methods = mapObj.getClass().getDeclaredMethods();

				for (Method method : methods) {
					if ("put".equals(method.getName())) {

						Class[] args = method.getParameterTypes();

						//System.out.println("tv :::" + args.length);

						if (args.length == 2) {

							//System.out.println("method1 ::" + method.getName());
							try {
								result = method.invoke(mapObj, new Object[] {fildNm, fildValue});
							} catch (Exception e) {
								logger.error("convertObject Err :" + e);
							}
						}
					}
				}

			} catch (Exception e) {
				logger.error("convertObject Err2 :" + e);
			}
		}
		return mapObj;
	}

	/**
	 * JSONObject --> obj 변환
	 * Statements
	 *
	 * @param mapObj
	 * @param obj
	 * @return
	 */
	public static JSONObject convertObjectFromJSonData(Object obj) {

		JSONObject mapObj = new JSONObject();
		Field[] fields = obj.getClass().getDeclaredFields();
		Object result = null;
		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);

			try {

				String fildNm = fields[i].getName();

				String fildValue = (String)fields[i].get(obj);
				if (fildValue == null || "".equals(fildValue)) {
					fildValue = "";
				}
				Method[] methods = mapObj.getClass().getMethods();

				for (Method method : methods) {
					//System.out.println("method.getName()::" + method.getName());
					if ("put".equals(method.getName())) {

						Class[] args = method.getParameterTypes();

						//System.out.println("tv :::" + args.length);

						if (args.length == 2) {

							//System.out.println("method1 ::" + method.getName());
							try {
								result = method.invoke(mapObj, new Object[] {fildNm, fildValue});
							} catch (Exception e) {
								logger.error("convertObject Err :" + e);
							}
						}
					}
				}

			} catch (Exception e) {
				logger.error("convertObject Err2 :" + e);
			}
		}
		return mapObj;
	}

	public static String changeToMap(String str) {
		if (str == null || "".equals(str)) {
			return "";
		}
		String conStr = "";
		boolean loop = true;
		int i = 0;
		while (loop) {

			char ch = str.charAt(i++);
			//System.out.println("ch:" + i  + ":" + ch);

			if (Character.isUpperCase(ch)) {
				conStr = "_" + ch;

				String fStr = str.substring(0, i - 1);
				String eStr = str.substring(i, str.length());

				str = fStr + conStr + eStr;

				i++;

			}

			if (i >= str.length()) {
				loop = false;
			}

		}

		return str.toUpperCase();
	}

	/**
	 * JSONObject --> Object
	 * Statements
	 *
	 * @param jsonData
	 * @param obj
	 * @return
	 */
	public static Object convertFromJsonToObject(JSONObject jsonData, Object obj) {
		String keyAttribute = null;
		String setMethodString = "set";
		String methodString = null;

		Iterator<String> itr = jsonData.keySet().iterator();

		while (itr.hasNext()) {
			keyAttribute = (String)itr.next();
			//System.out.println("keyAttribute ::" + keyAttribute);

			methodString = setMethodString + keyAttribute.substring(0, 1).toUpperCase() + keyAttribute.substring(1);
			//System.out.println("methodString ::" + methodString);

			Method[] methods = obj.getClass().getDeclaredMethods();

			for (int i = 0; i < methods.length; i++) {

				if (methodString.equals(methods[i].getName())) {

					//System.out.println(methodString +":" + methods[i].getName());

					try {
						methods[i].invoke(obj, jsonData.get(keyAttribute));
					} catch (Exception e) {
						logger.error("convertObjectToObject Err :" + e);
					}
				}
			}
		}

		return obj;
	}

	/**
	 * 메시지 발송 상태를 업체 형태에서 공통 형태로 변환
	 *
	 * @param String status
	 * */
	public static String convertMessageStatus(String vender, String status, String resultCode) {
		// set default
		if (StrUtil.isNVL(status) || StrUtil.isNVL(vender)) {
			return "01";
		}

		if ("lguV2".equals(vender) && "1000".equals(StrUtil.nullToStr(resultCode))) {
			return "05";
		}
		return SendStatus.convertStatusCode(status);
	}

	/**
	 * 다날 결과 데이터 파싱
	 * Statements
	 *
	 * @param str
	 * @return
	 */
	public static Map<String, String> setParamParse(String str) {
		//RETURNCODE=1110&RETURNMSG=[인증실패] 입력하신 정보가 올바르지 않습니다. 다시 입력하여 주십시오.&TID=201808130930569928962010

		Map<String, String> retMap = new HashMap<String, String>();

		if (str == null || "".equals(str)) {
			return null;
		}

		String[] array = str.split("&");

		for (int i = 0; i < array.length; i++) {
			String[] param = array[i].split("=");
			String name = param[0];
			String value = param[1];

			retMap.put(name, value);

		}

		return retMap;
	}

	public static String changeToVO(String str) {

		if (str == null || "".equals(str)) {
			return "";
		}

		boolean loop = true;
		str = str.toLowerCase();

		int i = 0;
		while (loop) {

			char ch = str.charAt(i++);

			if (ch == '_') {
				String fStr = str.substring(0, i - 1);
				String mStr = str.substring(i, i + 1).toUpperCase();
				String eStr = str.substring(i + 1);
				str = fStr + mStr + eStr;
			}

			if (i >= str.length()) {
				loop = false;
			}

		}

		return str;
	}

}
