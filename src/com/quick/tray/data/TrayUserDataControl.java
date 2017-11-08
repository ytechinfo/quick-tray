/**
(#)TrayUserDataControl.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayUserDataControl
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/

package com.quick.tray.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import com.quick.tray.bean.AppConfigBean;
import com.quick.tray.config.TrayConfigurationConstants;
import com.quick.tray.constants.TrayKeyConstants;
import com.quick.tray.entity.DataEntity;


public class TrayUserDataControl {
	// 설정 item node명
	
	private static Document doc = null;
	private static Element root = null;
	
	private static File xml_dom_file = null;
	private static boolean USER_DATA_MODIFY_FLAG= false;
	
	private static List<DataEntity> itemList;
	private static Map<String , DataEntity> itemMap;
	
	private static class TrayUserDataControlHolder{
        private static final TrayUserDataControl instance = new TrayUserDataControl();
    }
	
	public static TrayUserDataControl newIntance() {
		return TrayUserDataControlHolder.instance;
    }
	
	private TrayUserDataControl(){
		init();
	}
	
	private void init() {
		try {
			xml_dom_file = new File(TrayConfigurationConstants.DEFAULT_XML_PATH+TrayConfigurationConstants.TRAY_USER_FILE_NAME);
			
			if(!xml_dom_file.canRead()){
				URL url = TrayUserDataControl.class.getClassLoader().getResource(TrayConfigurationConstants.DEFAULT_XML_PATH+TrayConfigurationConstants.TRAY_USER_FILE_NAME);
				xml_dom_file = new File(java.net.URLDecoder.decode(url.getPath(),"utf-8"));
			}
			
			SAXBuilder builder = new SAXBuilder();
			
			if(xml_dom_file.canRead()) {
				doc =builder.build(new FileInputStream(xml_dom_file));
				root = doc.getRootElement();
			}else{
				doc = new Document(); 
				root = new Element(TrayKeyConstants.ROOT_NM);
				doc.setRootElement(root);
			}
			loadItemList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TrayUserDataControl udc=TrayUserDataControl.newIntance();
	}
	
	/**
	 * tray user item list
	 * @return
	 */
	public List<DataEntity> getItemList() {
		
		if(itemList==null){
			return loadItemList();
		}
		
		return itemList ; 
	}
	
	public DataEntity getItemMap(String key) {
		
		if(itemMap==null){
			loadItemList();
		}
		return itemMap.containsKey(key) ?itemMap.get(key) : new DataEntity(); 
	}
	
	private List<DataEntity> loadItemList() {
		itemList = new ArrayList<DataEntity>();
		itemMap = new HashMap<String , DataEntity> ();
		List<Element>  list = doc.getRootElement().getChildren(); // 자식 추출 . 
		
		Element sEle = null;
		DataEntity item = null;
		
		int eleSize = list.size();
		
		for (int i = 0; i <eleSize ; i++) {
			sEle = list.get(i);
			String entryId = sEle.getAttribute(TrayKeyConstants.ENTRY_ATTR_ID).getValue(); 
			item =new DataEntity();
			item.put(TrayKeyConstants.ENTRY_ATTR_ID, entryId);
			item.put(TrayKeyConstants.ITEM_TYPE, sEle.getChild(TrayKeyConstants.ITEM_TYPE).getText());
			item.put(TrayKeyConstants.ITEM_NAME, sEle.getChild(TrayKeyConstants.ITEM_NAME).getText());
			item.put(TrayKeyConstants.ITEM_COMMAND, sEle.getChild(TrayKeyConstants.ITEM_COMMAND).getText());
			
			itemList.add(item);
			itemMap.put(entryId, item);
		}
		
		return itemList;
	}

	/***
	 * item 수정.
	 * @param entity
	 * @throws JDOMException
	 */
	public void modify(DataEntity entity) throws JDOMException {
		String XPATH_ENTRY= new StringBuilder().append("//")
		.append(TrayKeyConstants.ENTRY_NM)
		.append("[@entryUniqueId='")
		.append(entity.getString(TrayKeyConstants.ENTRY_ATTR_ID))
		.append("']")
		.toString();
		
		Element e = (Element)XPath.selectSingleNode(doc, XPATH_ENTRY); 
		
		if(e != null){
			Element e1= e.getChild(TrayKeyConstants.ITEM_NAME)
			,e2 = e.getChild(TrayKeyConstants.ITEM_TYPE)
			,e3 = e.getChild(TrayKeyConstants.ITEM_COMMAND);
			
			e1.removeContent(0);
			e1.addContent(new CDATA(entity.getString(TrayKeyConstants.ITEM_NAME)));
			
			e2.removeContent(0);
			e2.addContent(entity.getString(TrayKeyConstants.ITEM_TYPE));
			
			e3.removeContent(0);
			e3.addContent(new CDATA(entity.getString(TrayKeyConstants.ITEM_COMMAND)));
			
		}
	}
	
	/**
	 * tray info xml 생성.
	 * @param name
	 * @param type
	 * @param command
	 */
	public void createItem(DataEntity entity) {
		Element entryItem = new Element(TrayKeyConstants.ENTRY_NM);
		entryItem.setAttribute(TrayKeyConstants.ENTRY_ATTR_ID, System.currentTimeMillis()+""+(Math.random()+100000));
		entryItem.addContent(new Element(TrayKeyConstants.ITEM_NAME).addContent(new CDATA(entity.getString(TrayKeyConstants.ITEM_NAME))));
		entryItem.addContent(new Element(TrayKeyConstants.ITEM_TYPE).setText(entity.getString(TrayKeyConstants.ITEM_TYPE)));
		entryItem.addContent(new Element(TrayKeyConstants.ITEM_COMMAND).addContent(new CDATA(entity.getString(TrayKeyConstants.ITEM_COMMAND))));
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
		
		loadItemList();
		setModifyFlag(true);
	}
	
	/**
	 * 노드 삭제 . 
	 * @param id
	 * @throws JDOMException
	 */
	public void deleteItem(String id) throws JDOMException{
		String XPATH_ENTRY= new StringBuilder().append("//")
		.append(TrayKeyConstants.ENTRY_NM)
		.append("[@entryUniqueId='")
		.append(id)
		.append("']")
		.toString();
		
		Element e = (Element)XPath.selectSingleNode(doc, XPATH_ENTRY); 
		
		if(e != null) doc.getRootElement().removeContent(e);
		
		setModifyFlag(true);
	}
	
	/**
	 * 정렬한 정보 생성. 
	 * @param dataSortIndex
	 */
	public void dataSort(List<String> dataSortIndex) {
		doc.getRootElement().removeChildren(TrayKeyConstants.ENTRY_NM);
		
		for (int i = 0; i < dataSortIndex.size(); i++) {
			createItem(itemMap.get(dataSortIndex.get(i)));
		}
	}
	
	public void setModifyFlag(boolean flag){
		USER_DATA_MODIFY_FLAG =flag;
	}
	
	public boolean getModifyFlag(){
		return USER_DATA_MODIFY_FLAG;
	}
}
