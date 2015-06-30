/**
(#)TrayConfiguration.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayConfiguration
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/

package com.quick.tray.config;


public abstract class TrayConfiguration {
	protected static Object lock = new Object();
	
	/**
	 * @return java.util.Properties
	 */
	public abstract TrayProperties getProperties();

}
