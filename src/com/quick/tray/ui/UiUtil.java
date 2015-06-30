/**
(#)UiUtil.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : UiUtil
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/
package com.quick.tray.ui;

import java.util.List;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class UiUtil {
	
	private UiUtil(){
		
	}
	/**
	 * 콤보 그리기
	 * @param shell
	 * @param comboArr
	 * @return
	 */
	public static Combo createCombo(Composite shell, List comboList , int swtPosition){
		return createCombo(shell,(String[]) comboList.toArray(new String[]{}),swtPosition);
	}
	
	/**
	 * 버튼 사이즈 데이타 얻기.
	 * @param shell
	 * @param comboList
	 * @param swtPosition
	 * @return
	 */
	public static RowData getRowData(int w, int h){
		RowData data = new RowData();
	    data.width = w; 
	    data.height = h; 
	    
		return data;
	}
	
	/**
	 * 콤보 그리기
	 * @param shell
	 * @param comboArr
	 * @return
	 */
	public static Combo createCombo(Composite shell, String [] comboArr, int swtPosition){
		Combo combo = new Combo(shell, swtPosition);
		
		for (int i = 0; i < comboArr.length; i++) {
			combo.add(comboArr[i]);
		}
		
		return combo;
	}
	
	/**
	 * 라벨 만들기.
	 * @param shell
	 * @param labelNm
	 * @return
	 */
	public static Label createLabel(Composite composite, String nm , int swtPosition){
		Label label = new Label(composite, swtPosition);
		
        label.setText(nm);
        
        FontData fd = Display.getCurrent().getSystemFont().getFontData()[0];
        fd.setHeight(9);
        Font font = new Font(Display.getDefault(),fd);
        
        label.setFont(font);
        
        return label;
	}
	
	/**
	 * 버튼 만들기 
	 * @param shell
	 * @param nm
	 * @param swtPosition
	 * @return
	 */
	public static Button createButton(Composite shell, String nm, int swtPosition) {
		Button button = new Button(shell,swtPosition);
		button.setText(nm);
		return button;
	}
	
	/**
	 * text 만들기 
	 * @param shell
	 * @param nm
	 * @param swtPosition
	 * @return
	 */
	public static Text createText(Composite shell, String nm, int swtPosition) {
		Text txt = new Text(shell,swtPosition);
		txt.setText(nm);
		return txt;
	}
	
	/**
	 * form data value 
	 * @param formAttachment
	 * @param labelTextLeft
	 * @param comboListW
	 * @param textH
	 * @return
	 */
	public static FormData getFormData(FormAttachment formAttachment,
			FormAttachment labelTextLeft, int comboListW, int textH) {
		FormData data = new FormData();
 	   	data.top = formAttachment;
 	    data.left = labelTextLeft;
 	    data.width = comboListW;
 	    data.height = textH;
 	    
 	    return data; 
	}
}
