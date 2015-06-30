/**
(#)TrayMain.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayMain
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/
package com.quick.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
	/**
	 * string 구분
	 * @param a
	 * @param delim
	 * @return
	 */
	public static String[] split(String a, String delim){
		
		if("".equals(a)) return new String[0];
		
		int position=0;
		int delimiterIdx = 0; 
		int strLen = a.length();
		List<String> resultList = new ArrayList<String>();
		
		int len = delim.length();
		while(position <= strLen){
			delimiterIdx = a.indexOf(delim,position);
			if(delimiterIdx > -1){
				resultList.add(a.substring(position, delimiterIdx));
			}else {
				resultList.add(a.substring(position, strLen));
				break;
			}
			position = delimiterIdx+len;
		}
				
		return (String[]) resultList.toArray(new String[]{});
	}
}
