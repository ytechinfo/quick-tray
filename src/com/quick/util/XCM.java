/**
(#)XCM.java

Copyright (c) 2012 qis
All rights reserved.
 
CLASS_NAME : xml -> map convert
프로그램 생성정보 :  2012-03-12 / 김영태
프로그램 수정정보 :  
*/
package com.quick.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class XCM {
	
	public static Map convert(URL buildsource) throws Exception{
		Element ele = getDocument(buildsource);
		
		Map dataMap = getNodeValue(ele);
		
		return dataMap;
	}
	
	/**
	 * METHOD_NAME : 정보 꺼내기
	 * @param buildsource
	 * @return
	 * @throws Exception
	 */
	public static Element getDocument(URL buildsource) throws Exception{
		Element ele=null;

		Document doc=null;
		SAXBuilder builder = new SAXBuilder();
		
		doc =builder.build(buildsource);
		
		return doc.getRootElement();
	}
	
    /**
     * METHOD_NAME : 노드 데이터 추출. 
     * @param p_el
     * @param dataMap
     */
	private static Map getNodeValue(Element el) {
    	Map dataMap = new HashMap();
    	List list = el.getChildren(); // 자식 추출 . 
		
		Iterator<Element> it = list.iterator();
		
		Map childMap = null; // 자식 데이터를 담을 맵
		List childList =null; // 자식 리스트를 담기 위해 
		Object tmpChildObj = null; // 자식 체크 하기 위한 obj
		int childNodeLen = -1;
		
		Element sEle = null;
		String nodeName =null;
		List sChildList = null;
		
		while(it.hasNext()) {
			
			childMap = new HashMap();
			
			sEle = it.next(); // xml element 추출
			nodeName= sEle.getName();
			sChildList = sEle.getChildren();
			childNodeLen = sChildList.size();
			
			if (childNodeLen != 0) childMap=getNodeValue(sEle); // 자식 노드가 존재 할때  
			
			if(dataMap.containsKey(nodeName)) {
				tmpChildObj= dataMap.get(nodeName);
				if(!( tmpChildObj instanceof List)) {
					childList = new ArrayList();
					childList.add(tmpChildObj);
				}else{
					childList=(List)tmpChildObj;
				}
				childList.add(childMap);
				dataMap.put(nodeName, childList);
			}else {
				dataMap.put(nodeName, childNodeLen > 0? childMap: sEle.getValue());
			}
		} 
		return dataMap;
    }
}
