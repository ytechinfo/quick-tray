/**
(#)TrayConfig.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayConfig
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/

package com.quick.tray.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class TrayConfig extends TrayConfiguration{
	private static String jdf_file_directory = null;
	private static File out_file = null;
	private static String jdf_file_name = null;
	protected static TrayProperties props = null;
	
	public static List<java.util.Map> AD_LIST  = new ArrayList<java.util.Map>();
	
	static {
		jdf_file_directory=TrayConfigurationConstants.DEFAULT_CONFIG_PATH;
		jdf_file_name=TrayConfigurationConstants.CONFIG_FILE_NAME;
	}
	
	private TrayConfig(){
		super();
		try {
			props = new TrayProperties();
			initialize();
		} catch (TrayConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private static class EpConfigHolder{
        private static final TrayConfig instance = new TrayConfig();
    }
	
	public static TrayConfig getInstance() {
		return EpConfigHolder.instance;
    }
	
	protected void initialize() throws TrayConfigurationException {
		synchronized(lock){	
			try{
				File currentConfigPath = new File("./config",jdf_file_name);
				
				File jdf_file = null;
				boolean firstSaveFlag = false;
				
				InputStream is = null;
				if(currentConfigPath.canRead()){
					jdf_file = currentConfigPath;
					is =new FileInputStream(jdf_file); 
				}else{
					firstSaveFlag = true; 
					is = TrayConfig.class.getClassLoader().getResourceAsStream(jdf_file_directory+jdf_file_name);
					
					if (is ==null){
						throw new TrayConfigurationException( this.getClass().getName() + " - Can't open jdf configuration file path: [" + jdf_file_directory+jdf_file_name +"]");
					}
				}
				
				out_file =jdf_file;
				props.load(new java.io.BufferedInputStream(is));
				
				if(firstSaveFlag==true){
					File parentDir = new File(currentConfigPath.getParent());
					
					if(!parentDir.exists()){
						parentDir.mkdir();
					}
					
					out_file =currentConfigPath;
					store();
				}
				
				is.close();
			}catch(TrayConfigurationException e) {
				throw new TrayConfigurationException( this.getClass().getName() +  e.getMessage());
			}catch(Exception e){
				throw new TrayConfigurationException( this.getClass().getName() + e.getLocalizedMessage()+"\n"+ e.getMessage());
			}
		} // end of sunchronized(lock);
	}
	
	public TrayProperties getProperties() {
		return props;
	}
	
	public void refresh(){
		try {
			initialize();
		} catch (TrayConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void store() throws FileNotFoundException, IOException{
		props.store(new OutputStreamWriter(new FileOutputStream(out_file)),"quicktray");
	}
}
