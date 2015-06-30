/**
(#)TrayMain.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayMain
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/
package com.quick.tray.main;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.Widget;

import com.quick.tray.NewTrayInfoInsertForm;
import com.quick.tray.TrayKeyConstants;
import com.quick.tray.ad.TrayAdInfo;
import com.quick.tray.config.TrayConfig;
import com.quick.tray.config.TrayConfigurationConstants;
import com.quick.tray.data.TrayUserDataControl;
import com.quick.tray.entity.DataEntity;
import com.quick.tray.lang.ResourceControl;
import com.quick.util.StringUtil;
import com.quick.util.TrayUtil;

public class TrayMain {
	public static Color WHITE = null;
	public static Image ICON_IMAGE = null;
	private DataEntity TRAY_COFNIG = TrayConfig.getInstance().getProperties();
	Display display=null;
	Menu trayMenu = null;
	public static void main(String[] args){
		try{
			new Thread(new TrayAdInfo()).start();
			TrayMain main = new TrayMain();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public TrayMain() throws Exception{
		display = new Display();
		WHITE= display.getSystemColor(SWT.COLOR_WHITE);
		try{
			ICON_IMAGE = new Image(display, TrayUtil.getTrayImage("menuQuickTray.png"));
		}catch(Exception e){}
		
		final Tray tray = display.getSystemTray();
		final Shell shell= new Shell(display);
		shell.setText("Quick Tray Main");
		shell.setData("TrayMain");
		try {
			Image image = new Image(display, TrayUtil.getTrayImage());
			
			if(tray == null){
				System.err.println("System tray is currently not supported.");
			}else{
				final TrayItem item = new TrayItem(tray, SWT.NONE);
				item.setToolTipText(ResourceControl.getInstance().getResource().getString("app_name","Quick Tray"));
				
				item.addListener(SWT.Selection,new Listener(){
					public void handleEvent (Event event){
						return; 
					}
				});

				item.addListener(SWT.MenuDetect,new Listener(){
					public void handleEvent(Event event){
						Menu menu= getMenuData(shell);
				    	menu.setVisible(true);
					}
				});
				item.setImage(image);
				
				shell.setBounds(0,0,0,0);
				shell.open();

				shell.setVisible(false);

				while(!shell.isDisposed()){
					if(!display.readAndDispatch()) display.sleep();
				}

				image.dispose();
				ICON_IMAGE.dispose();
				shell.dispose();
				display.dispose();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * tray 메뉴 데이터 생성.
	 * @param shell
	 * @return
	 */
	protected Menu getMenuData(Shell shell) {
		Menu reMenu = null;
		if(TrayUserDataControl.newIntance().getModifyFlag() || trayMenu == null){
			reMenu = new Menu(shell, SWT.POP_UP);
			TrayUserDataControl udc=TrayUserDataControl.newIntance();
	    	List<DataEntity> menuList = udc.getItemList();
	    	//팝업 메뉴에 등록 
	    	DataEntity tmpMenu = null;
	    	
	    	MenuItem mi =null;
	    	for (int i = 0; i < menuList.size(); i++) {
	    		tmpMenu = menuList.get(i);
	    		
	        	mi = new MenuItem(reMenu, SWT.PUSH);
	        	mi.setText(tmpMenu.getString(TrayKeyConstants.ITEM_NAME));
	        	mi.setData(i+"");
	        	mi.addListener(SWT.Selection, new Listener(){
					public void handleEvent(Event event){
						menuClickEventProcess(event.widget);
					}
				});
	        	
			}
	    	new MenuItem(reMenu, SWT.SEPARATOR);
	    	
	    	MenuItem menuMamt = new MenuItem(reMenu, SWT.PUSH);
	    	menuMamt = createInsertMenuItem(menuMamt);
	    	reMenu.setDefaultItem(menuMamt);
	    	
	    	new MenuItem(reMenu, SWT.SEPARATOR);
	    	createExitMenuItem(new MenuItem(reMenu, SWT.PUSH));
	    	trayMenu = reMenu;
	    	
	    	TrayUserDataControl.newIntance().setModifyFlag(false);
	    	
		}else{
			reMenu=trayMenu;
	    }
    	return reMenu;
		
	}
	
	/**
	 * 메뉴 관리 메뉴 생성.
	 * @param mi
	 */
	protected MenuItem createInsertMenuItem(MenuItem mi) {
		mi.setText(ResourceControl.getInstance().getResource().getString("menu_mgmt", "Menu Setting"));
		mi.setData("insert");
		
		mi.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event){
				menuClickEventProcess(event.widget);
			}
		});
		return mi;
	}
	
	/**
     * exit 메뉴 만들기.
     * @param menu
     * @return
     */
    private MenuItem createExitMenuItem(MenuItem mi){
    	mi.setText(ResourceControl.getInstance().getResource().getString("menu_exit", "Exit"));
		mi.setData("exit");
		
		mi.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event){
				menuClickEventProcess(event.widget);
			}
		});
		return mi;
    }
    
    /**
     * tray 메뉴 클릭
     * @param widget
     */
	private void menuClickEventProcess(Widget widget) {
		String menuIdx =String.valueOf(widget.getData());
		
		StringBuilder errMsg = new StringBuilder();
		if("exit".equals(menuIdx)){
			boolean answer = MessageDialog.openQuestion(display.getActiveShell()
					,ResourceControl.getInstance().getResource().getString("menu_exit", "Exit")
					,ResourceControl.getInstance().getResource().getString("menu_exit_alert", "Are you sure you want to quit?"));
			
			if(answer)	System.exit(0);
			
		}else if("insert".equals(menuIdx)){
			inFormView();
		}else{
			DataEntity clickEnt= TrayUserDataControl.newIntance().getItemList().get(Integer.parseInt(menuIdx));
			String type= clickEnt.getString(TrayKeyConstants.MENU_TYPE);
			String exeCmd= clickEnt.getString(TrayKeyConstants.MENU_COMMAND);
			errMsg.append(ResourceControl.getInstance().getResource().getString("menu_name", "Menu : "))
			.append(clickEnt.getString(TrayKeyConstants.MENU_NM))
			.append("\n")
			.append(ResourceControl.getInstance().getResource().getString("menu_path", "Menu Path :"))
			.append(exeCmd)
			.append("\n");
			
			try {
				final Runtime rt= Runtime.getRuntime();
				
				String cmdType = "tray"+"."+TrayKeyConstants.MENU_COMMAND+"."+type;
				if(TRAY_COFNIG.containsKey(cmdType)){
					String [] exeCommandArr= new String[2];
					exeCommandArr[0] = TRAY_COFNIG.getString(cmdType);
					exeCommandArr[1] = exeCmd;
					rt.exec(exeCommandArr);
				}else{
					if("web".equals(type)){
						if(exeCmd.indexOf("http://") < 0){
							exeCmd = "http://"+exeCmd ;
						}
						Desktop.getDesktop().browse(new URI(exeCmd));
					}else{
						String [] exeCommandArr = StringUtil.split(exeCmd, TrayConfigurationConstants.DELIMETER);
						if(exeCommandArr.length > 1){
							rt.exec(exeCommandArr);
						}else{
							Desktop.getDesktop().open(new File(exeCmd));
						}
					}
				}
			} catch (Exception e1) {
				MessageDialog.openError(display.getActiveShell(), "Error", errMsg.toString());
			}
		}
	}
	
	public void inFormView(){
		NewTrayInfoInsertForm tii = new NewTrayInfoInsertForm();  
		tii.open(display);
	}
}
