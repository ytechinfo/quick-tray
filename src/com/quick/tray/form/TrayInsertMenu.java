/**
(#)TrayInsertMenu.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayInsertMenu
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/
package com.quick.tray.form;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
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
import com.quick.tray.utils.TrayUtils;

public class TrayInsertMenu {
	
	private static Logger logger = Logger.getLogger(TrayConfig.class);
	
	Display g_display;

	Shell g_shell;
	
	boolean initFlag = false; 
	
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
		
		dataExportImportForm = new DataExportImportForm(g_display, newTrayInfoInsertForm);
		
		if(initFlag==false){
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

		fileImportItem = new MenuItem(fileMenu, SWT.CASCADE);
		fileImportItem.setText(resource.getString("top_menu_import", "&Import"));
		
		final Menu subfileImportItem = new Menu(g_shell, SWT.DROP_DOWN);
		fileImportItem.setMenu(subfileImportItem);
		
		
		final MenuItem fileImport = new MenuItem(subfileImportItem, SWT.PUSH);
		fileImport.setText(resource.getString("top_menu_import_file", "&File Import"));
		fileImport.setData("importType","file");
				
		final MenuItem remoteImport = new MenuItem(subfileImportItem, SWT.PUSH);
		remoteImport.setText(resource.getString("top_menu_import_remote", "&Remote Import"));
		remoteImport.setData("importType","remote");
		

		fileExportItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExportItem.setText(resource.getString("top_menu_export", "&Export"));
		
		fileAppConfigItem = new MenuItem(fileMenu, SWT.PUSH);
		fileAppConfigItem.setText(resource.getString("top_menu_appconfig", "app config"));
				
		
		fileImport.addSelectionListener(importItemListener);
		remoteImport.addSelectionListener(importItemListener);
		
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
		FileDialog g_fileDialog;
		InputDialog inputDialog ;
		
		public void widgetSelected(SelectionEvent event) {
			
			String importType = String.valueOf(event.widget.getData("importType"));
			
			if("remote".equals(importType)){
				
				String remoteURL =(String) TrayConfig.getInstance().getProperties().get("tray.remote.url");
				inputDialog = new InputDialog(g_shell, resource.getString("remote_input_tit_dialog", "remote url"), resource.getString("remote_input_msg_dialog", "remote url input") , remoteURL, null);
				
				validCheck(inputDialog);
				
				return ;
			}
			
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
					MessageDialog.openError(g_shell, "Error","\n"+ e.getMessage());
					return ; 
				}
				
				dataExportImportForm.open("import", g_shell,tmpData);
			}
		}
		
		public void validCheck(InputDialog inputDialog2){
			if(inputDialog2.open() ==  Window.OK){
				String inputVal =inputDialog2.getValue(); 
				String result = isValid(inputVal);
				
				if(result != null){
					MessageDialog.openError(g_shell, "Error","\n"+ result);
					validCheck(inputDialog2);
				}else{
					HttpURLConnection conn = null; 
					FileOutputStream fos =null;
					InputStream in = null;
					try {
						URL url = new URL (inputVal);
						conn = (HttpURLConnection)url.openConnection(); 
						
						conn.setConnectTimeout(3000);
						conn.setReadTimeout(3000);
						
						if(conn.getResponseCode() ==HttpURLConnection.HTTP_OK){
							
							File fileData = new File("./fileData",TrayUtils.UUID()+".xml");
							
							
							File parentDir = new File(fileData.getParent());
							
							if(!parentDir.exists()){
								parentDir.mkdir();
							}
							
							in = conn.getInputStream();
							fos = new FileOutputStream(fileData);
							
							byte[] buffer  = new byte[1024];
							int readLen = -1;
							
							while((readLen = in.read(buffer)) > 0){
								fos.write(buffer ,0 ,readLen );
							}
							fos.close();
							
							in.close();
							
							logger.info(" fileData : "+fileData.getAbsolutePath());
							
							List<DataEntity> tmpData =null;
							try {
								ImportDataControl idc= new ImportDataControl(fileData);
								tmpData =idc.getItemList();
								idc=null;
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
								MessageDialog.openError(g_shell, "Error","\n"+ e.getMessage());
								validCheck(inputDialog2);
							}
							
							fileData.delete();
							
							logger.info(tmpData);
							
							dataExportImportForm.open("import", g_shell,tmpData);
						}
						conn.disconnect();
					} catch (MalformedURLException e) {
						logger.error(e.getMessage(), e);
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}finally{
						if(fos!=null)try{ fos.close(); }catch(Exception e){}
						if(in !=null)try{ in.close(); }catch(Exception e){}
						if(conn!=null) conn.disconnect();
					}
				}
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
	
	
	public String isValid(String text) {
		
		HttpURLConnection conn = null; 
		try {
			URL url = new URL (text);
			conn = (HttpURLConnection)url.openConnection(); 
			
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			
			if(conn.getResponseCode() ==HttpURLConnection.HTTP_OK){
				
			}else{
				return "not valid";
			}
			conn.disconnect();
			
			try {
				TrayConfig.getInstance().getProperties().put("tray.remote.url",text);
				TrayConfig.getInstance().store();
			} catch (Exception errMsg) {
				errMsg.printStackTrace();
			}
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			
			return e.getMessage();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return e.getMessage();
		}finally{
			if(conn!=null) conn.disconnect();
		}
		
		return null;
	}
		
	
}
