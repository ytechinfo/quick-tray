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


public class JavaTest {
	public static void main(String[] args) {
		File file = new File("./config", "c.txt");
		
		
		
		System.out.println(file.getAbsolutePath());
		
	}
}



