/**
(#)TrayConfig.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayConfig
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/

package com.quick.tray.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.quick.tray.TrayKeyConstants;
import com.quick.util.FileUtil;


public class TrayConfig extends TrayConfiguration{
	private static long jdf_last_modified = 0;
	private static String jdf_file_directory = null;
	private static File out_file = null;
	private static String jdf_file_name = null;
	protected static TrayProperties props = null;
	private static boolean loadFlag = false;
	
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
				
				File jdf_file = new File(jdf_file_directory+jdf_file_name);
				
				if(!jdf_file.canRead()){
					URL url = TrayConfig.class.getClassLoader().getResource(jdf_file_directory+jdf_file_name);
					jdf_file = new File(java.net.URLDecoder.decode(url.getPath(),"utf-8"));
				}
				
				if ( ! jdf_file.canRead() ) 				
					throw new TrayConfigurationException( this.getClass().getName() + " - Can't open jdf configuration file path: [" + jdf_file_directory+jdf_file_name +"]");
				
				if ( (jdf_last_modified != jdf_file.lastModified()) ) {
					out_file =jdf_file;
					FileInputStream jdf_fin = new FileInputStream(jdf_file);
					props.load(new java.io.BufferedInputStream(jdf_fin));
					jdf_fin.close();
					jdf_last_modified = jdf_file.lastModified();
					loadFlag= true;
				} // end if
			}catch(TrayConfigurationException e) {
				jdf_last_modified = 0;
				throw new TrayConfigurationException( this.getClass().getName() +  e.getMessage());
			}catch(Exception e){
				jdf_last_modified = 0;
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
		InputStreamReader isr =null;
		BufferedReader br =null;
		try{
			isr = new InputStreamReader(new FileInputStream(out_file)); 
			br = new BufferedReader(isr);

			StringBuilder sb = new StringBuilder();
			String read_data = "";
			while((read_data =br.readLine()) != null){
				if(read_data.indexOf(TrayKeyConstants.TRAY_LANG) >-1){
					sb.append(TrayKeyConstants.TRAY_LANG).append('=').append(props.getString(TrayKeyConstants.TRAY_LANG)).append("\n");
				}else{
					sb.append(read_data).append("\n");
				}
			}
			br.close();
			isr.close();
			
			FileUtil.write(out_file, sb.toString());
		}finally{
			if(br !=null) try{br.close();}catch(Exception e){}
			if(isr !=null) try{isr.close();}catch(Exception e){}
		}		
		
	}
}
