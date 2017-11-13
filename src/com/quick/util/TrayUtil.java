/**
(#)TrayUtil.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayUtil
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/
package com.quick.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.quick.tray.config.TrayConfigurationException;

public class TrayUtil {
	private static String TAG = "TrayUtil";
	
	private TrayUtil(){}
	
	 /**
	 * tray 이미지 추출.
	 * @return
	 * @throws TrayConfigurationException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException 
	 */
	public static InputStream getTrayImage() throws TrayConfigurationException, UnsupportedEncodingException, FileNotFoundException {
		return getTrayImage("quickTray.png");
	}
	
	/**
	 * tray 이미지 추출.
	 * @return
	 * @throws TrayConfigurationException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException 
	 */
	public static InputStream getTrayImage(String image) throws TrayConfigurationException,FileNotFoundException {
		String imgPath = System.getProperty("tray.images.path");
		
		InputStream is = null; 
		if(imgPath!=null && !"".equals(imgPath)){
			File in_file = new File(imgPath);
			
			if (!in_file.canRead())throw new TrayConfigurationException( TAG + " - Can't open jdf configuration file path: [" + imgPath+"]");
			
			is = new FileInputStream(in_file);
		}else{
			is = TrayUtil.class.getClassLoader().getResourceAsStream("images/"+image);
		}
		
		return is;
	}
	
	/**
	 * 이미지 저장 경로 찾기.
	 * @return
	 * @throws TrayConfigurationException
	 * @throws UnsupportedEncodingException
	 */
	public static String getImagePath() throws UnsupportedEncodingException {
		URL url = TrayUtil.class.getClassLoader().getResource("images/");
		return java.net.URLDecoder.decode(url.getPath(),"utf-8");
	}
	
	/**
	 * 이미지 저장 경로 찾기.
	 * @return
	 * @throws TrayConfigurationException
	 * @throws UnsupportedEncodingException
	 */
	public static String getSavePath() throws UnsupportedEncodingException {
		URL url = TrayUtil.class.getClassLoader().getResource("./");
		String imgPath = java.net.URLDecoder.decode(url.getPath(),"utf-8");
		imgPath = imgPath +"FileData";
		if(!new File(imgPath).exists()){
			new File(imgPath).mkdirs();
		}
		return imgPath;
	}
	
	/**
	 * 가운데 포지션 찾기
	 * @param display
	 * @return
	 */
	public static Point getCenterPoint(Display display, Shell shell){
		Monitor primary = display.getPrimaryMonitor ();
        Rectangle bounds = primary.getBounds ();
        Rectangle rect = shell.getBounds ();
		int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 3;
        
        return new Point(x, y);
	}
}
