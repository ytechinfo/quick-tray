package com.test.tray;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.jdom.CDATA;
import org.jdom.Element;

import com.Ostermiller.util.Base64;
import com.quick.tray.config.TrayConfigurationConstants;
import com.quick.util.StringUtil;


public class BasicWindow {
	public static void main(String[] args) {
		String tmp ="asdf";
		
		String [] tmpCmdArr= StringUtil.split(tmp, TrayConfigurationConstants.DELIMETER);
		
		
		System.out.println(tmpCmdArr[tmpCmdArr.length-1]);
		
		if(true) return ; 
		
		BasicWindow bw = new BasicWindow();
		
		// xml에 파일 컨텐츠 쓰기
		String fileContent = bw.binaryWrite();
		
		// xml 파일 컨텐츠 읽기
		bw.binaryRead(fileContent);
	}

	public void binaryRead(String fileContent) {
		
		System.out.println(fileContent);
		
		BufferedOutputStream os = null;
		
		// 파일로 만들 파일 명.
		File file = new File("c:/zzz/testOut.txt");
		
		try {
			os = new BufferedOutputStream(new FileOutputStream(file));
			byte[] buf = Base64.decodeToBytes(fileContent);
			
			os.write(buf, 0, buf.length);
			os.flush();

			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(os!=null) try{os.close();}catch(Exception e){}
		}
		
		
	}

	public String binaryWrite() {
		// xml에 추가할 파일 컨텐츠 경로. 
		File file = new File("c:/zzz/test.txt");
		
		BufferedInputStream is = null;
		ByteArrayOutputStream out = null;
		String fileContent ="";
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			out = new ByteArrayOutputStream();
			
			Base64.encode(is,out,true);
			
			// fileContent 부분을 추가할 곳에 넣으면된다. 
			fileContent = out.toString();
			new Element("file_data").addContent(new CDATA(fileContent));
			// 아래 부분에서  개발자 가 xml에 추가할 부분을 추가하면 된다. 
						
			if(is !=null) is.close();
			if(out !=null) out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(is !=null) try{ is.close();}catch(Exception e){};
			if(out !=null) try{ out.close();}catch(Exception e){};
		}
		
		return fileContent; 
	}
}



