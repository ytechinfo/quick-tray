package com.quick.tray.lang;

import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import com.quick.tray.config.TrayConfig;
import com.quick.tray.constants.TrayKeyConstants;
import com.quick.tray.entity.DataEntity;

public class ResourceControl {
	
	private static Locale DEFAULT_LANG;
	private static DataEntity LANG_RESOURCE= new DataEntity();
	
	static {
		
		String lang= "";
		
		String usrLang = TrayConfig.getInstance().getProperties().getString(TrayKeyConstants.TRAY_LANG, "");
		if("".equals(usrLang)){
			Locale locale = Locale.getDefault();
			if(locale != null){
				usrLang= locale.toString();
			}
		}
		
		if(usrLang.startsWith("ko")){
			lang = "ko";
		}else if(usrLang.startsWith("ja")){
			lang = "ja";
		}else if(usrLang.startsWith("zh")){
			lang = "zh";
		}else {
			lang = "en";
		}
		
		DEFAULT_LANG = new Locale(lang);
	}
	
	private static class ResourceHolder{
		private static final ResourceControl instance = new ResourceControl();
	}

	public static ResourceControl getInstance() {
		return ResourceHolder.instance;
	}
	
	private ResourceControl(){
		initialize();
	}
	
	private void initialize(){
		ResourceBundle rb =  ResourceBundle.getBundle("com.quick.tray.lang.Quick_Language", DEFAULT_LANG);
		
		Iterator<String> iter = rb.keySet().iterator();
		String tmpKey = "";
		while(iter.hasNext()){
			tmpKey =iter.next();
			LANG_RESOURCE.put(tmpKey, rb.getString(tmpKey));
		}
	}
	
	public DataEntity getResource() {
		return LANG_RESOURCE;
	}
	
	/**
	 * 사용자 선택 언어로 변경.
	 * @param lang
	 * @return
	 */
	public DataEntity getResource(String lang) {
		if(!DEFAULT_LANG.toString().startsWith(lang)){
			DEFAULT_LANG=new Locale(lang);
			initialize();
		}
		
		return LANG_RESOURCE;
	}
	
	/**
	 * 기본 언어 . 
	 * @return
	 */
	public Locale getDefaultLang(){
		return DEFAULT_LANG;
	}
	
	/**
	 * 사용자 선택 언어로 변경.
	 * @param lang
	 * @return
	 */
	public void setLanguage(String lang) {
		if(!DEFAULT_LANG.toString().startsWith(lang)){
			DEFAULT_LANG=new Locale(lang);
			initialize();
		}
		
	}
}
