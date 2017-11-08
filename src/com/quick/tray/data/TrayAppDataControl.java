/**
(#)TrayAppDataControl.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayAppDataControl
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
import com.quick.tray.utils.TrayUtils;


public class TrayAppDataControl {
	// 설정 item node명
	
	private Document doc = null;
	private Element root = null;
	
	private File xml_dom_file = null;
	
	private List<AppConfigBean> itemList;
	
	private Map<String,AppConfigBean> appKeyMap;
	private String[] appType;
	
	private static class TrayUserDataControlHolder{
        private static final TrayAppDataControl instance = new TrayAppDataControl();
    }
	
	public static TrayAppDataControl newIntance() {
		return TrayUserDataControlHolder.instance;
    }
	
	private TrayAppDataControl(){
		init();
	}
	
	private void init() {
		try {
			xml_dom_file = new File(TrayConfigurationConstants.DEFAULT_XML_PATH+TrayConfigurationConstants.TRAY_APP_FILE_NAME);
			
			if(!xml_dom_file.canRead()){
				URL url = TrayAppDataControl.class.getClassLoader().getResource(TrayConfigurationConstants.DEFAULT_XML_PATH+TrayConfigurationConstants.TRAY_APP_FILE_NAME);
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
		TrayAppDataControl udc=TrayAppDataControl.newIntance();
		System.out.println(udc.getItemList());
	}
	
	/**
	 * tray user item list
	 * @return
	 */
	public List<AppConfigBean> getItemList() {
		if(itemList==null){
			loadItemList();
		}
		
		return itemList ; 
		
	}
	
	public AppConfigBean getItemMap(String key) {
		if(appKeyMap==null){
			loadItemList();
		}
		return appKeyMap.containsKey(key) ?appKeyMap.get(key) : new AppConfigBean(); 
		
	}
	
	private void loadItemList() {
		
		List<Element>  list = doc.getRootElement().getChildren(); // 자식 추출 . 
		
		Element sEle = null;
		int eleSize = list.size();
		
		itemList = new ArrayList<AppConfigBean>();
		appKeyMap =new HashMap<String,AppConfigBean>();
		appType = new String[eleSize];
		
		String entryKey ,entryName;
		
		for (int i = 0; i <eleSize ; i++) {
			sEle = list.get(i);
			entryKey = sEle.getAttribute(TrayKeyConstants.ENTRY_ATTR_ID).getValue(); 
			entryName = sEle.getChild(TrayKeyConstants.ITEM_NAME).getText(); 
			
			AppConfigBean appConfigBean = new AppConfigBean();
			
			appConfigBean.setEntryId(entryKey);
			appConfigBean.setType(sEle.getChild(TrayKeyConstants.ITEM_TYPE).getText());
			appConfigBean.setName(entryName);
			appConfigBean.setCommand(sEle.getChild(TrayKeyConstants.ITEM_COMMAND).getText());
			
			itemList.add(appConfigBean);
			appKeyMap.put(entryKey, appConfigBean);
			appType[i] = entryName;
			
		}
	}
	
	private String getEntryXpath(AppConfigBean appConfigBean){
		return getEntryXpath(appConfigBean.getEntryId());
	}
	
	private String getEntryXpath(String entryId){
		return new StringBuilder().append("//")
				.append(TrayKeyConstants.ENTRY_NM)
				.append("[@entryUniqueId='")
				.append(entryId)
				.append("']")
				.toString();
	}
	
	public boolean isElement(AppConfigBean appConfigBean) throws JDOMException{
		Element e = (Element)XPath.selectSingleNode(doc, getEntryXpath(appConfigBean)); 
		
		return e == null? false : true;
	}
	
	public boolean isElement(String name) throws JDOMException{
		Element e = (Element)XPath.selectSingleNode(doc, getEntryXpath(getUID(name))); 
		return e == null? false : true;
	}

	/***
	 * item 수정.
	 * @param entity
	 * @throws JDOMException
	 */
	public void modify(AppConfigBean appConfigBean) throws JDOMException {
		Element e = (Element)XPath.selectSingleNode(doc, getEntryXpath(appConfigBean)); 
		
		if(e != null){
			Element e1= e.getChild(TrayKeyConstants.ITEM_NAME)
			,e2 = e.getChild(TrayKeyConstants.ITEM_TYPE)
			,e3 = e.getChild(TrayKeyConstants.ITEM_COMMAND);
			
			System.out.println("e11 : "+ e1);
			System.out.println("e21 : "+ e2);
			System.out.println(e3.getText()+" e31 : "+ e3 +appConfigBean.getCommand());
			
			e1.removeContent(0);
			e1.addContent(new CDATA(appConfigBean.getName()));
			
			
			e2.removeContent(0);
			e2.addContent(appConfigBean.getType());
			
			e3.removeContent(0);
			e3.addContent(new CDATA(appConfigBean.getCommand()));
		}
	}
	
	public String getUID(String name){
		return TrayUtils.UUID(name);
	}
	
	/**
	 * tray info xml 생성.
	 * @param name
	 * @param type
	 * @param command
	 */
	public void createItem(AppConfigBean appConfigBean) {
		Element entryItem = new Element(TrayKeyConstants.ENTRY_NM);
		entryItem.setAttribute(TrayKeyConstants.ENTRY_ATTR_ID,getUID(appConfigBean.getName()) );
		entryItem.addContent(new Element(TrayKeyConstants.ITEM_NAME).addContent(appConfigBean.getName()));
		entryItem.addContent(new Element(TrayKeyConstants.ITEM_TYPE).setText(appConfigBean.getType()));
		entryItem.addContent(new Element(TrayKeyConstants.ITEM_COMMAND).addContent(new CDATA(appConfigBean.getCommand())));
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
		
	}
	
	/**
	 * 노드 삭제 . 
	 * @param id
	 * @throws JDOMException
	 */
	public void deleteItem(String id) throws JDOMException{
		List eList = XPath.selectNodes(doc, getEntryXpath(id));
		
		if(eList != null) {
			for(int i=0 ;i < eList.size();i++){
				Element e = (Element)eList.get(i);
				doc.getRootElement().removeContent(e);
			}
		}
	}
	
	/**
	 * 정렬한 정보 생성. 
	 * @param entryKeyList
	 */
	public void dataSort(List<String> entryKeyList) {
		
		doc.getRootElement().removeChildren(TrayKeyConstants.ENTRY_NM);
		
		for (int i = 0; i < entryKeyList.size(); i++) {
			createItem(appKeyMap.get(entryKeyList.get(i)));
		}
	}

	public String[] getAppType() {
		return appType;
	}
}
