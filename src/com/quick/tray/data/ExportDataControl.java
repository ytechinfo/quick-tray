/**
(#)ExportDataControl.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : ExportDataControl // tray 데이타 내보내기.
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/

package com.quick.tray.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.Ostermiller.util.Base64;
import com.quick.tray.TrayKeyConstants;
import com.quick.tray.config.TrayConfigurationConstants;
import com.quick.tray.entity.DataEntity;
import com.quick.util.StringUtil;


public class ExportDataControl {
	// 설정 item node명
	
	private Document doc = null;
	private Element root = null;
	
	private File xml_dom_file = null;
	
	public ExportDataControl(File file){
		xml_dom_file = file;
		init();
	}
	
	public ExportDataControl(String file) {
		xml_dom_file = new File(file);
		init();
	}
	
	private void init() {
		doc = new Document();
		root = new Element(TrayKeyConstants.ROOT_NM);
		doc.setRootElement(root);
	}
	
	
	/**
	 * tray info xml 생성.
	 * @param name
	 * @param type
	 * @param command
	 */
	public void createItem(DataEntity entity) throws Exception {
		String type = entity.getString(TrayKeyConstants.ITEM_TYPE);
		String cmd = entity.getString(TrayKeyConstants.ITEM_COMMAND);
		Element entryItem = new Element(TrayKeyConstants.ENTRY_NM);
		entryItem.setAttribute(TrayKeyConstants.ENTRY_ATTR_ID, System.currentTimeMillis()+""+(Math.random()+100000));
		entryItem.addContent(new Element(TrayKeyConstants.ITEM_NAME).addContent(entity.getString(TrayKeyConstants.ITEM_NAME)));
		entryItem.addContent(new Element(TrayKeyConstants.ITEM_TYPE).setText(type));
		entryItem.addContent(new Element(TrayKeyConstants.ITEM_COMMAND).addContent(new CDATA(cmd)));
		
		if("app".equals(type)){
			String [] tmpCmdArr= StringUtil.split(cmd, TrayConfigurationConstants.DELIMETER);
			cmd = tmpCmdArr[tmpCmdArr.length-1];
			entryItem.addContent(new Element(TrayKeyConstants.ITEM_COMMAND).addContent(new CDATA(cmd)));
			File file  = new File(cmd);
			
			if(file.exists()){
				
				BufferedInputStream is = null;
	            ByteArrayOutputStream out = null;
				try {
					is = new BufferedInputStream(new FileInputStream(file));
		            out = new ByteArrayOutputStream();
		            
		            // 맨 마지막 Parameter인 boolean은 BASE64의 76자마다 Line Break를 넣을 것인가를 지정
		            Base64.encode(is,out,true);
					entryItem.addContent(new Element(TrayKeyConstants.ITEM_DATA).addContent(new CDATA(out.toString())));
					
					if(is !=null) is.close();
					if(out !=null) out.close();
				}finally{
					if(is !=null) try{ is.close();}catch(Exception e){};
					if(out !=null) try{ out.close();}catch(Exception e){};
				}
				
			}
		}
		
		root.addContent(entryItem);
	}
	
	/**
	 * user tray info save
	 * @throws IOException
	 */
	public void store() throws IOException{
		XMLOutputter serializer = new XMLOutputter();

		Format f = serializer.getFormat();
		f.setEncoding("UTF-8");
		//encoding 타입을 UTF-8 로 설정
		f.setIndent(" ");
		f.setLineSeparator("\r\n");
		f.setTextMode(Format.TextMode.TRIM);
		serializer.setFormat(f);
		
		FileOutputStream out = new FileOutputStream(xml_dom_file);
		serializer.output(doc, out);
		out.flush();
		out.close();
	}
}
