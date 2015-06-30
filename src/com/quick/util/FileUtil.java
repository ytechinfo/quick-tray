package com.quick.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {
	public static boolean isExt(File file , String ext){
		String fileStr = file.getName();
		String tmpExt = fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.length());
		
		return ext.equals(tmpExt);
	}
	/**
	 *확장자 체크. 
	 */
	public static String getExtension(File file){
		String fileStr = file.getName();
		return fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.length());
	}
	
	/**
	 * 확장자 삭제 하고 이름 받기. 
	 * @param file
	 * @return
	 */
	public static String getDelExtension(File file){
		String fileStr = file.getName();
		
		return fileStr.substring(0,fileStr.lastIndexOf("."));
	}
	
	/**
	 * 파일의 절대 경로 찾기. 
	 * @return
	 */
	public static String getResourceAbsoultePath(){
		try {
			URL url = FileUtil.class.getClassLoader().getResource("./");
			File fl = new File(url.getFile());
			return java.net.URLDecoder.decode(fl.getAbsolutePath(),"UTF-8"); 
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 파일 리스트 
	 * @param chk_file
	 * @return
	 */
	public static File[] getFileList(File chk_file){
		return chk_file.listFiles();
	}
	
	/**
	 * 파일 삭제. 
	 * @param chk_file
	 * @return
	 */
	public static boolean delete(File chk_file){
		return chk_file.delete();
	}
	
	/**
	 * 파일 작성. 
	 */
	public static void write(String filePath, String fileNm, String content){
		OutputStream os  = null;
		
		try{
			File tmpFilePath  = new File(filePath);
			
			if(!tmpFilePath.isDirectory()){
				tmpFilePath.mkdirs();
			}
			
			int len = filePath.length();
			
			int lastIdx = filePath.lastIndexOf('/');
			int fileFirstIdx = fileNm.indexOf('/');
			
			String tmpFileNm = filePath+fileNm;
			if(len+1 !=lastIdx && fileFirstIdx != 0) tmpFileNm =filePath+"/"+fileNm;
			
			os = new FileOutputStream(tmpFileNm);
			
			os.write(content.toString().getBytes());
			
			os.close(); 
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(os !=null){try{ os.close(); }catch(Exception e){}	}
		}
	}
	
	/**
	 * 파일 복사. 
	 * @param filePath
	 * @param sourceFile
	 */
	public static void write(String filePath,File sourceFile){
		String lastDelimeter = filePath.substring(filePath.length()-1 , filePath.length());
		
		filePath = filePath+("\\".equals(lastDelimeter)||"/".equals(lastDelimeter)?"":"/");
		
		//복사 대상이 되는 파일 생성 
		//스트림, 채널 선언
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		FileChannel fcin = null;
		FileChannel fcout = null;
		String fileNm= sourceFile.getName().replace("[1]", "");
		try {
			
			File tmpFilePath  = new File(filePath);
			if(!tmpFilePath.isDirectory()){
				tmpFilePath.mkdirs();
			}
			//스트림 생성
			inputStream = new FileInputStream(sourceFile);
			
			File testTmpFile = new File(filePath+fileNm);
			delete(testTmpFile);
			//System.out.println("write FileNm : [" + filePath+fileNm+"]");
			
			outputStream = new FileOutputStream(new File(filePath+fileNm));
			//채널 생성
			fcin = inputStream.getChannel();
			fcout = outputStream.getChannel();
	
			//채널을 통한 스트림 전송
			long size = fcin.size();
			fcin.transferTo(0, size, fcout);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//자원 해제
			try{fcout.close();}catch(IOException ioe){}
			try{fcin.close();}catch(IOException ioe){}
			try{outputStream.close();}catch(IOException ioe){}
			try{inputStream.close();}catch(IOException ioe){}
		}
	}
	
	/**
	 * 파일 쓰기.
	 * @param createFile
	 * @param content
	 */
	public static void write(File createFile, String content) {
		OutputStream os  = null;
		try{
			os = new FileOutputStream(createFile);
			os.write(content.getBytes());
			os.close(); 
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(os !=null){try{ os.close(); }catch(Exception e){}	}
		}
	}
	
	/**
	 * 이어쓰기 
	 * @param filePath
	 * @param content
	 */
	public static void appendWrite(String filePath , String content) {
        try {
            BufferedWriter buf_writer = new BufferedWriter(new FileWriter(filePath, true));
            PrintWriter print_writer = new PrintWriter(buf_writer,true);
            print_writer.println (content);

            if (print_writer.checkError ()) {
                System.out.println ("An output error occurred!" );
            }
        }
        catch (IOException ioe){
            System.out.println("IO Exception");
            ioe.printStackTrace();
        }
    }
	
	/**
	 * 파일 한줄로 읽기
	 * @param filePath
	 * @return
	 */
	public static String getLangString(File filePath){
		StringBuffer sb = new StringBuffer();
		try{
			InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath)); 
			
			BufferedReader br = new BufferedReader(isr);
			String read_data ="";
			
			while((read_data =br.readLine()) != null){
				sb.append(read_data+"|");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString(); 
	}
	
	/**
	 * 파일 내용 map으로 보기. 
	 * @param filePath
	 * @param delimeter
	 * @return
	 */
	public static Map getLangMap(File filePath,String delimeter){
		Map reVal = new HashMap();
		try{
			InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath)); 
			
			BufferedReader br = new BufferedReader(isr);
			String read_data ="";
			
			String [] tmpStr = null;
			while((read_data =br.readLine()) != null){
				tmpStr= StringUtil.split(read_data,delimeter);
				reVal.put(tmpStr[0], tmpStr);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return reVal; 
	}
	
	/**
	 * 한주한줄 list로 보기.
	 * @param filePath
	 * @return
	 */
	public static List getLangList(File filePath){
		List contentList = new ArrayList();
		try{
			InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath)); 
			
			BufferedReader br = new BufferedReader(isr);
			String read_data ="";
			
			while((read_data =br.readLine()) != null){
				contentList.add(read_data);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return contentList; 
	}
	
	/**
	 * 중복 제거값 리턴.
	 * @param filePath
	 * @return
	 */
	public static List getDuplicateClear(File filePath) {
		List list = getLangList(filePath);
		Map tmp = new HashMap();
		String str = "";
		List reVal = new ArrayList();
		for (int i = 0; i < list.size(); i++) { 
			str =(String) list.get(i);
			
			if(!tmp.containsKey(str)){
				tmp.put(str, "");
				reVal.add(str);
			}
		}
		return reVal;
		
	}
}
