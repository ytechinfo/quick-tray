/**
(#)FileUI.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : FileUI
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/
package com.quick.tray.ui;

import java.awt.FileDialog;
import java.awt.Frame;

public class FileUI {
	public static void main(String[] args) {
		//System.out.println(new FileUI().fileUiOpen());

	}
	
	/**
	 * 파일 콜 url
	 * @return
	 */
	public String fileUiOpen(){
		Frame f = new Frame("Parent");
		f.setSize(0,0);

		FileDialog fileOpen = new FileDialog(f, "파일열기", FileDialog.LOAD);

		f.setVisible(false);
		fileOpen.setDirectory("/");
		fileOpen.setVisible(true);

		//파일을선택한다음, FileDialog의열기버튼을누르면,
		//getFile()과getDirectory()를이용해서파일이름과위치한디렉토리를얻을수있다.
		
		return fileOpen.getDirectory() + fileOpen.getFile();
        
	}
}
