/**
(#)ImportDataControl.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : ImportDataControl  // tray 데이타 가져오기. 
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/

package com.quick.tray.data;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.Ostermiller.util.Base64;
import com.quick.tray.TrayKeyConstants;
import com.quick.tray.config.TrayConfigurationException;
import com.quick.tray.entity.DataEntity;
import com.quick.util.TrayUtil;


public class ImportDataControl {
	// 설정 item node명
	
	private Document doc = null;
	private Element root = null;
	
	private File xml_dom_file = null;
	
	public ImportDataControl(File file) throws Exception{
		xml_dom_file = file;
		init();
	}
	
	public ImportDataControl(String file) throws Exception{
		xml_dom_file = new File(file);
		init();
	}
	
	private void init() throws Exception {
		SAXBuilder builder = new SAXBuilder();
		
		if(xml_dom_file.canRead()) {
			doc =builder.build(xml_dom_file);
			root = doc.getRootElement();
		}else{
			doc = new Document(); 
			root = new Element(TrayKeyConstants.ROOT_NM);
			doc.setRootElement(root);
		}
		builder = null;
	}
	
	/**
	 * tray user item list
	 * @return
	 * @throws TrayConfigurationException 
	 * @throws UnsupportedEncodingException 
	 */
	public List<DataEntity> getItemList() throws UnsupportedEncodingException, TrayConfigurationException {
		List<DataEntity> itemList = new ArrayList<DataEntity>();
		
		Iterator<Element> eleIter = doc.getRootElement().getChildren(TrayKeyConstants.ENTRY_NM).iterator();
		
		String savePath = TrayUtil.getSavePath();
				
		File tmpFilePath  = new File(savePath);
		
		if(!tmpFilePath.isDirectory()){
			tmpFilePath.mkdirs();
		}
		
		BufferedOutputStream os = null;
		FileOutputStream fos = null;
		
		try{
			Element sEle = null;
			DataEntity item = null;
			String type="" , cmd = "", filedata="";
			while(eleIter.hasNext()){
				sEle = eleIter.next();
				item =new DataEntity();
				
				type = sEle.getChild(TrayKeyConstants.ITEM_TYPE).getText();
				cmd = sEle.getChild(TrayKeyConstants.ITEM_COMMAND).getText();
				
				item.put(TrayKeyConstants.ENTRY_ATTR_ID, sEle.getAttribute(TrayKeyConstants.ENTRY_ATTR_ID).getValue());
				item.put(TrayKeyConstants.ITEM_TYPE, type);
				item.put(TrayKeyConstants.ITEM_NAME, sEle.getChild(TrayKeyConstants.ITEM_NAME).getText());
				
				if("app".equals(type)){
					
					cmd = cmd.replaceAll("[\\\\]", "/");
					cmd =savePath+"/"+cmd.substring(cmd.lastIndexOf('/')+1); 
					item.put(TrayKeyConstants.ITEM_COMMAND, cmd);
					
					filedata = sEle.getChild(TrayKeyConstants.ITEM_DATA).getText(); 
					
					fos = new FileOutputStream(cmd);
					os = new BufferedOutputStream(fos);
					byte[] buf = Base64.decodeToBytes(filedata);
					
					os.write(buf, 0, buf.length);
					
					os.flush();
					buf =null;
					
					fos.close();
					os.close();
					
				}else{
					item.put(TrayKeyConstants.ITEM_COMMAND, cmd);
				}
				
				itemList.add(item);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			eleIter = null;
			root = null;
			doc = null;
			if(fos!=null) try{fos.close();}catch(Exception e){}
			if(os!=null) try{os.close();}catch(Exception e){}
		}
		
		return itemList;
	}
}
