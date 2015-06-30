/**
(#)TrayConfigurationException.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayConfigurationException
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/

package com.quick.tray.config;

public class TrayConfigurationException extends Exception {

	/**
	 * 
	 */
	public TrayConfigurationException() {
		super();
	}

	/**
	 * @param s java.lang.String
	 */
	public TrayConfigurationException(String s) {
		super(s);
	}
	/**
	 * @param s java.lang.String
	 */
	public TrayConfigurationException(String s , Exception exeception) {
		super(s,exeception);
	}
}