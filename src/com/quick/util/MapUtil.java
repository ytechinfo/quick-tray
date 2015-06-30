/**
(#)MapUtil.java

Copyright (c) 2012 qis
All rights reserved.
 
CLASS_NAME : java map util
프로그램 생성정보 :  2012-03-12 / 김영태
프로그램 수정정보 :  
*/
package com.quick.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapUtil {
	/**
	 * METHOD_NAME : 맵 정보 보기. 
	 * @param defaultInfo
	 * @param substituteKey
	 * @param delim
	 * @return
	 * @throws Exception
	 */
	public static  Object getMapInfo(Map defaultInfo, String substituteKey, String delim) throws Exception {
		String [] subsKey = split(substituteKey, delim);
		Object obj = defaultInfo;
		
		for (int i = 0; i < subsKey.length; i++) {
			obj = getMapSubData(obj, subsKey[i]);
		}
		
		return obj;
	}
	
	/**
	 * METHOD_NAME : 맵 안에 정보 꺼내기 
	 * @param obj
	 * @param key
	 * @return
	 */
	private static Object getMapSubData(Object obj, String key) {
		return obj instanceof Map?((Map)obj).get(key):obj;
	}
	
	/**
	 * METHOD_NAME : 문자열 분리
	 * @param a
	 * @param delim
	 * @return
	 * @throws Exception
	 */
	private static String[] split(String a, String delim) throws Exception{
		if("".equals(a)) return null;
		int position=0;
		int delimiterIdx = 0; 
		int strLen = a.length();
		List<String> resultList = new ArrayList<String>();
		
		int len = delim.length();
		while(position <= strLen){
			delimiterIdx = a.indexOf(delim,position);
			if(delimiterIdx > -1){
				resultList.add(a.substring(position, delimiterIdx));
			} else {
				resultList.add(a.substring(position, strLen));
				break;
			}
			position = delimiterIdx+len;
		}
		return resultList.size() < 1?null:(String[]) resultList.toArray(new String[]{});
	}
}
