package com.test.tray;

import java.util.Locale;
import java.util.ResourceBundle;

public class Propertity {
	public static void main(String[] args) {
		
		
		
		System.out.println(Locale.getDefault());
		
		
		ResourceBundle labels = ResourceBundle.getBundle("com.test.tray.Propertity", Locale.getDefault());
		
		
	}
}
