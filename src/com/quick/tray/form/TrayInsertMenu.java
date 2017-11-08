/**
(#)TrayInsertMenu.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayInsertMenu
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/
package com.quick.tray.form;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.quick.tray.config.TrayConfig;
import com.quick.tray.config.TrayConfigurationConstants;
import com.quick.tray.data.ImportDataControl;
import com.quick.tray.data.TrayUserDataControl;
import com.quick.tray.entity.DataEntity;
import com.quick.tray.lang.ResourceControl;

public class TrayInsertMenu {
	
	Display g_display;

	Shell g_shell;
	
	boolean initFlag = false; 
	
	FileDialog g_fileDialog;
	
	Image g_image = null;
	
	DataExportImportForm dataExportImportForm;
	AppConfigForm appConfigForm;
	HelpForm helpForm;
	
	final private ImportItemListener importItemListener = new ImportItemListener();
	final private ExportItemListener exportItemListener = new ExportItemListener();
	final private AppConfigListener appConfigListener = new AppConfigListener();
	final private HelpGetHelpItemListener helpGetHelpItemListener = new HelpGetHelpItemListener();
	
	final private DataEntity  resource = ResourceControl.getInstance().getResource();
	
	public static void main(String[] args) {
		new TrayInsertMenu().execute();
	}
	
	private void execute() {
		g_display = new Display();
		g_shell = new Shell(g_display);
		g_shell.setText("Tray Menu");
		g_shell.setSize(300, 200);
		
        menuGrid(g_display,g_shell, null);
        g_shell.open();
        while (!g_shell.isDisposed()) {
			if (!g_display.readAndDispatch()) {
				g_display.sleep();
			}
		}
        g_display.dispose();
		
	}
	
	/**
	 * 메뉴 그리기
	 * @param display
	 * @param shell
	 * @param newTrayInfoInsertForm
	 */
	public void menuGrid(Display display, Shell shell, TrayInfoInsertForm newTrayInfoInsertForm) {
		g_shell = shell;
		g_display=display;
		
		if(initFlag==false){
			dataExportImportForm = new DataExportImportForm(g_display, newTrayInfoInsertForm);
			helpForm = new HelpForm(g_display);
			appConfigForm = new AppConfigForm(g_display);
			initFlag= true; 
		}
		
		Menu menuBar, fileMenu, helpMenu;

		MenuItem fileMenuHeader, helpMenuHeader;
		
		menuBar = new Menu(shell, SWT.BAR);
		
		// file menu
		fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		fileMenuHeader.setText(resource.getString("top_menu_file", "&File"));
		fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileMenuHeader.setMenu(fileMenu);
		
		createFileMenu(fileMenu);

		// help menu 
		helpMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		helpMenuHeader.setText(resource.getString("top_menu_help", "&Help"));
		
		helpMenu = new Menu(shell, SWT.DROP_DOWN);
		helpMenuHeader.setMenu(helpMenu);
		
		createHelpMenu(helpMenu);

		shell.setMenuBar(menuBar);

	}
	
	/**
	 * help menu
	 * @param helpMenu
	 */
	private void createHelpMenu(Menu helpMenu) {
		
		MenuItem helpGetHelpItem;
		
		helpGetHelpItem = new MenuItem(helpMenu, SWT.PUSH);
		helpGetHelpItem.setText(resource.getString("top_menu_help_tray", "about Quick Tray"));
		
		helpGetHelpItem.addSelectionListener(helpGetHelpItemListener);
		
	}
	/**
	 * file menu
	 * @param fileMenu
	 */
	private void createFileMenu(Menu fileMenu) {
		
		MenuItem fileImportItem, fileExportItem, fileAppConfigItem, fileLangItem;

		fileImportItem = new MenuItem(fileMenu, SWT.PUSH);
		fileImportItem.setText(resource.getString("top_menu_import", "&Import"));

		fileExportItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExportItem.setText(resource.getString("top_menu_export", "&Export"));
		
		fileAppConfigItem = new MenuItem(fileMenu, SWT.PUSH);
		fileAppConfigItem.setText(resource.getString("top_menu_appconfig", "app config"));
				
		
		fileImportItem.addSelectionListener(importItemListener);
		fileExportItem.addSelectionListener(exportItemListener);
		fileAppConfigItem.addSelectionListener(appConfigListener);
		
		fileLangItem = new MenuItem(fileMenu, SWT.CASCADE);
		fileLangItem.setText(resource.getString("top_menu_language", "&Language"));
		
		final Menu submenu = new Menu(g_shell, SWT.DROP_DOWN);
		fileLangItem.setMenu(submenu);
		
		final MenuItem langKo = new MenuItem(submenu, SWT.PUSH);
		langKo.setText(resource.getString("ko_lang", "&Korean"));
		langKo.setData("lang","ko");
				
		final MenuItem langEn = new MenuItem(submenu, SWT.PUSH);
		langEn.setText(resource.getString("en_lang", "&English"));
		langEn.setData("lang","en");
		
//		final MenuItem langZh = new MenuItem(submenu, SWT.PUSH);
//		langZh.setText(resource.getString("zh_lang", "&Chinese"));
//		langZh.setData("lang","zh");
//		
//		final MenuItem langJp = new MenuItem(submenu, SWT.PUSH);
//		langJp.setText(resource.getString("ja_lang", "&Japanese"));
//		langJp.setData("lang","ja");
		
		langKo.addSelectionListener(new LangItemListener());
		langEn.addSelectionListener(new LangItemListener());
		//langZh.addSelectionListener(new langItemListener());
		//langJp.addSelectionListener(new langItemListener());
	}

	/**
	 * 가져오기
	 * @author ytkim
	 */
	class ImportItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			if (g_fileDialog == null) {
				g_fileDialog = new FileDialog(g_shell, SWT.OPEN);
			}
			g_fileDialog.setFilterExtensions(new String[] { "*."+TrayConfigurationConstants.TRAY_DATA_EXT });
			String filePath = g_fileDialog.open();

			if (null != filePath) {
				
				List<DataEntity> tmpData =null;
				try {
					ImportDataControl idc= new ImportDataControl(filePath);
					tmpData =idc.getItemList();
					idc=null;
				} catch (Exception e) {
					MessageDialog.openError(g_shell, "Error","\n"+ e.toString());
					return ; 
				}
				
				dataExportImportForm.open("import", g_shell,tmpData);
			}
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			return ; 
		}
	}
	
	/**
	 * 내보내기
	 * @author ytkim
	 */
	class ExportItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			dataExportImportForm.open("export", g_shell,TrayUserDataControl.newIntance().getItemList());
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			return ; 
		}
	}
	
	/**
	 * app 설정. 
	 * @author ytkim
	 */
	class AppConfigListener implements SelectionListener {
		
		public void widgetSelected(SelectionEvent event) {
			appConfigForm.open(g_shell);
		}
		
		public void widgetDefaultSelected(SelectionEvent event) {
			return ; 
		}
	}
	
	/**
	 * 언어 설정.
	 * @author ytkim
	 */
	class LangItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			
			String selectLang = String.valueOf(event.widget.getData("lang"));
			
			if(selectLang.equals(ResourceControl.getInstance().getDefaultLang().toString())){
				return ; 
			}
			
			TrayConfig.getInstance().getProperties().put("tray.lang", selectLang);
			
			ResourceControl.getInstance().setLanguage(selectLang);
			
			boolean flag = true;
			try {
				TrayConfig.getInstance().store();
			} catch (Exception errMsg) {
				flag = false;
				MessageDialog.openError(g_shell, "Error", errMsg.toString());
			}
			
			String message = "\""+resource.getString("menu_mgmt", "Menu Management")+"\" restart";
			
			if(flag){
				MessageDialog.openWarning(g_shell, "Warning",message);
			}
		}
		
		public void widgetDefaultSelected(SelectionEvent event) {
			//System.out.println("widgetSelected : [" +event.item+"]");
		}
	}
	
	/**
	 * 도움말
	 * @author ytkim
	 */
	class HelpGetHelpItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			helpForm.open(g_shell);
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			return ; 
		}
	}
}
