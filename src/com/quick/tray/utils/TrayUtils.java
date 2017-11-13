package com.quick.tray.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TrayUtils {
	private TrayUtils(){}
	
	public static void main(String[] args) {
		System.out.println(TrayUtils.UUID());
		//25400724d7373b0ba9c9369d9af3dd21
		//

	}
	
	public static String UUID(){
	    return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String UUID(String uuidKey) {
		return UUID.nameUUIDFromBytes(uuidKey.getBytes()).toString().replaceAll("-", "");
	}
	
	public static String[] split(String str, String delim){
		return split( str,"" ,delim);
	}
	
	/**
	 * @param key
	 * @param initVal 
	 * @param delim
	 * @return String[]
	 */
	public static String[] split(String str, String initVal, String delim){
		
		if("".equals(str)) return new String[0];
		
		int position=0;
		int delimiterIdx = 0; 
		int strLen =str.length();
		List<String> resultList = new ArrayList<String>();
		
		int len = delim.length();
		while(position <= strLen){
			delimiterIdx =str.indexOf(delim,position);
			if(delimiterIdx > -1){
				resultList.add(str.substring(position, delimiterIdx));
			}else {
				resultList.add(str.substring(position, strLen));
				break;
			}
			position = delimiterIdx+len;
		}
				
		return (String[]) resultList.toArray(new String[]{});
	}

}
