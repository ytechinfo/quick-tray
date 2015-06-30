package com.quick.tray.ad;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.quick.tray.TrayKeyConstants;
import com.quick.tray.config.TrayConfig;
import com.quick.util.XCM;

public class TrayAdInfo implements Runnable {
	final static private String QUICK_AD_URL = "https://quick-tray.googlecode.com/files/quickAdInfo.xml";

	/**
	 * 
	 */
	public TrayAdInfo() {
	}

	/**
	 * run
	 */
	public void run() {
		try {
			Map fileInfoMap = XCM.convert(new URL(QUICK_AD_URL));

			if (fileInfoMap.containsKey("quick-item")) {
				Object tray_item = fileInfoMap.get("quick-item");

				if (tray_item instanceof java.util.List) {
					TrayConfig.AD_LIST = new ArrayList<java.util.Map>();
					List tmpList = (List) fileInfoMap.get("quick-item");
					int size = tmpList.size();
					Map tmpMap = null;
					for (int i = 0; i < size; i++) {
						tmpMap =(java.util.Map)tmpList.get(i);
						tmpMap.put(TrayKeyConstants.AD_STREAM, getImageStream(tmpMap));
						TrayConfig.AD_LIST.add(tmpMap);
					}
				} else if (tray_item instanceof java.util.Map) {
					TrayConfig.AD_LIST = new ArrayList<java.util.Map>(1);
					Map tmpMap = (java.util.Map) tray_item;
					tmpMap.put(TrayKeyConstants.AD_STREAM,getImageStream((java.util.Map) tray_item));
					TrayConfig.AD_LIST.add(tmpMap);
				}
			}
			fileInfoMap = null;
		} catch (Exception e) {

		}
	}

	/**
	 * url 이미지 가져오기.
	 * 
	 * @param tray_item
	 * @return
	 */
	private InputStream getImageStream(Map tray_item) {
		String imagePath = String.valueOf(tray_item
				.get(TrayKeyConstants.AD_IMG));
		
		return getImageStream(imagePath);
	}

	private InputStream getImageStream(String imagePath) {
		URL ocu;
		BufferedInputStream bis = null;
		try {
			ocu = new URL(imagePath);
			bis = new BufferedInputStream(ocu.openStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bis;
	}
	
	public static void main(String[] args) {
		 Runnable trayAd = new TrayAdInfo();
		 trayAd.run();
		//System.out.println(getImageStream("https://quick-tray.googlecode.com/files/quick_contact.png"));
	}
}
