/**
(#)TrayInsertMenu.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayInsertMenu
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :  
*/
package com.quick.tray;

import java.awt.Desktop;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.quick.tray.config.TrayConfig;
import com.quick.tray.config.TrayConfigurationConstants;
import com.quick.tray.data.ExportDataControl;
import com.quick.tray.data.ImportDataControl;
import com.quick.tray.data.TrayUserDataControl;
import com.quick.tray.entity.DataEntity;
import com.quick.tray.lang.ResourceControl;
import com.quick.tray.main.TrayMain;
import com.quick.tray.ui.UiUtil;
import com.quick.util.StringUtil;
import com.quick.util.TrayUtil;

public class TrayInsertMenu {
	
	Display g_display;

	Shell g_shell;
	
	FileDialog g_fileDialog;
	
	private Vector ranges = new Vector();
	
	StyledText text;
	
	private Cursor handCursor, arrowCursor;
	
	final int dataGridW = 260 , dataGridH = 320;
	
	final int helpW = 400 , helpH = 220;
	
	final int btnW = 70, btnH = 25;
	
	Image g_image = null;
	
	Table dataTable;
	
	NewTrayInfoInsertForm g_newTrayInfoInsertForm =null; 
	
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
	public void menuGrid(Display display, Shell shell, NewTrayInfoInsertForm newTrayInfoInsertForm) {
		g_shell = shell;
		g_display=display;
		g_newTrayInfoInsertForm =newTrayInfoInsertForm;
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
		
		helpGetHelpItem.addSelectionListener(new helpGetHelpItemListener());
		
	}
	/**
	 * file menu
	 * @param fileMenu
	 */
	private void createFileMenu(Menu fileMenu) {
		
		MenuItem fileImportItem, fileExportItem, fileLangItem;

		fileImportItem = new MenuItem(fileMenu, SWT.PUSH);
		fileImportItem.setText(resource.getString("top_menu_import", "&Import"));

		fileExportItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExportItem.setText(resource.getString("top_menu_export", "&Export"));
				
		
		fileImportItem.addSelectionListener(new importItemListener());
		fileExportItem.addSelectionListener(new exportItemListener());
		
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
		
		langKo.addSelectionListener(new langItemListener());
		langEn.addSelectionListener(new langItemListener());
		//langZh.addSelectionListener(new langItemListener());
		//langJp.addSelectionListener(new langItemListener());
	}

	/**
	 * 가져오기
	 * @author ytkim
	 *
	 */
	class importItemListener implements SelectionListener {
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
				
				dataGrid("import", tmpData);
			}
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			return ; 
		}
	}
	
	/**
	 * 내보내기
	 * @author ytkim
	 *
	 */
	class exportItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			dataGrid("export", TrayUserDataControl.newIntance().getItemList());
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			return ; 
		}
	}
	
	/**
	 * 언어 설정.
	 * @author ytkim
	 *
	 */
	class langItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			
			String selectLang = String.valueOf(event.widget.getData("lang"));
			
			if(selectLang.equals(ResourceControl.getInstance().getDefaultLang().toString())){
				return ; 
			}
			
			TrayConfig.getInstance().getProperties().put("tray.lang", selectLang);
			
			ResourceControl.getInstance().setLanguage(selectLang);
			
			TrayUserDataControl.newIntance().setModifyFlag(true);
			
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
	 *
	 */
	class helpGetHelpItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			viewHelpGrid();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			return ; 
		}
	}
	
	/**
	 * 가져오기 / 내보내기 그리기.
	 * @param mode
	 * @param dataList
	 */
	public void dataGrid(String mode, List<DataEntity> dataList) {
		
		final Shell dialog = new Shell(g_shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		dialog.setText("Select Data");
		
		int tmpH = dataGridH;
		if("export".equals(mode)){
			tmpH = dataGridH+40;
		}
		
	    dialog.setSize(dataGridW, tmpH);
	    dialog.setLayout(new FillLayout());
	    
	    try {
	    	dialog.setImage(TrayMain.ICON_IMAGE);
		} catch (Exception e) {
			
		}
	    
	    int listSize = dataList.size();
		DataEntity dataEntity = null;
		
		Composite top = new Composite(dialog, SWT.NONE);
		top.setLayout(new FormLayout());
		
		Composite allCheckLine = new Composite(top, SWT.SHADOW_IN);
		allCheckLine.setLayout(new FormLayout());
		allCheckLineGrid(allCheckLine);
		
		dataTable = new Table(top, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		
		for (int i = 0; i < listSize; i++) {
			dataEntity = dataList.get(i);
			TableItem item = new TableItem(dataTable, SWT.NONE);
			item.setText(dataEntity.getString(TrayKeyConstants.ITEM_NAME));
			item.setData(TrayKeyConstants.ENTRY_NM, dataEntity);
		}
		
		FormData data = new FormData();
		data.top = new FormAttachment(allCheckLine, 0);
		data.left = new FormAttachment(0, 3);
		data.width = dataGridW-35;
		data.height=dataGridH-110;
		dataTable.setLayoutData(data);
		
		RowData btnRowData = UiUtil.getRowData( btnW , btnH );
		Composite btn1Line = new Composite(top, SWT.SHADOW_IN);
    	btn1Line.setLayout(new RowLayout(SWT.HORIZONTAL));
    	
    	if("import".equals(mode)){
    		initImportBtn(btn1Line,btnRowData);
    	}else{
    		initExportBtn(btn1Line,btnRowData);
    	}
    	
    	closeBtnInit(btn1Line,btnRowData);
    	
    	data = new FormData();
    	data.top = new FormAttachment(dataTable, 3);
    	data.left = new FormAttachment(0, dataGridW/2-btnW-5);
		btn1Line.setLayoutData(data);
		
        dialog.setLocation (TrayUtil.getCenterPoint(g_display, dialog));
		
		dialog.open();
        while (!dialog.isDisposed()) {
			if (!g_display.readAndDispatch()) {
				g_display.sleep();
			}
		}
	}
	
	/**
	 * 도움말 그리기
	 */
	public void viewHelpGrid() {
		final Shell helpDialog = new Shell(g_shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		helpDialog.setText(resource.getString("app_name", "Quick Tray"));
		helpDialog.setSize(helpW, helpH);
		helpDialog.setLayout(new FillLayout());
		
		Color white = g_display.getSystemColor(SWT.COLOR_WHITE);
	    
		helpDialog.setBackground(white);
					
		try {
			helpDialog.setImage(TrayMain.ICON_IMAGE);
		} catch (Exception e) {}
		    
		Composite top = new Composite(helpDialog, SWT.NONE);
		top.setLayout(new FormLayout());
		
		Composite helpTop = new Composite(top, SWT.SHADOW_IN);
		helpTop.setLayout(new FormLayout());
		helpTop.setBackground(white);
		
		FormData data = new FormData();
		data.width=helpW;
		data.height=120;
		helpTop.setLayoutData(data);
		
		//왼쪽 이미지
		final Label helpImageLbl = UiUtil.createLabel(helpTop,"" ,SWT.NONE );
		helpImageLbl.setBackground(white);
		Image helpImg = null;
		try {
			helpImg=new Image(g_display, TrayUtil.getTrayImage("helpQuickTray.png"));
			
			helpImageLbl.setImage(helpImg);
		} catch (Exception e) {}
		
		data = new FormData();
		data.top= new FormAttachment(0,6);
		data.left= new FormAttachment(0,12);
		data.width=50;
		data.height=60;
		helpImageLbl.setLayoutData(data);
		
		
		GridLayout gridLayout = new GridLayout(4, false);
	    gridLayout.verticalSpacing = 8;
		
		Composite helpRight = new Composite(helpTop, SWT.NONE);
		helpRight.setLayout(gridLayout);
		helpRight.setBackground(white);
		
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(resource.getString("app_name", "Quick Tray")).append("\n")
		.append("email : ").append("qmffjem09@gmail.com").append("\n")
		.append("blog : ").append("http://qmffjem09.tistory.com/entry/%ED%80%B5-%ED%8A%B8%EB%A0%88%EC%9D%B4Quick-tray").append("\n")
		.append("퀵연락처 : ").append("https://play.google.com/store/apps/details?id=com.quick.call.activity").append("\n")	;
		
		text = new StyledText(helpRight, SWT.MULTI);
		text.setText(sb.toString());
		
		// init cursor
		handCursor = new Cursor(g_display, SWT.CURSOR_HAND);
		arrowCursor = new Cursor(g_display, SWT.CURSOR_ARROW);

		createHyperLink(text);
		text.addMouseListener(new HyperLinkMouseListener());
		text.addMouseMoveListener(new HyperLinkMouseMove());
		
		data = new FormData();
		data.top= new FormAttachment(0,6);
		data.left= new FormAttachment(helpImageLbl,5);
		data.width=helpW-50;
		data.height=100;
		
		helpRight.setLayoutData(data);
		
		Label ytkimLbl = UiUtil.createLabel(top, "ytkim", SWT.NONE);
		data = new FormData();
		data.top = new FormAttachment(helpTop, 10);
		data.left = new FormAttachment(0, helpW/2-20);
		data.width=50;
		data.height=20;
		ytkimLbl.setLayoutData(data);
		
		final Button exBtn = UiUtil.createButton(top,resource.getString("done_btn", "Done") ,SWT.PUSH);	
		data = new FormData();
		data.top = new FormAttachment(ytkimLbl, 0);
		data.left = new FormAttachment(0, helpW/2-btnW/2);
		data.width=btnW;
		data.height=btnH;
	    
		exBtn.setLayoutData(data);
    	
		exBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		exBtn.getShell().dispose();
	    	}
    	});
		
		helpDialog.setLocation (TrayUtil.getCenterPoint(g_display, helpDialog));
		
		helpDialog.open();
		
		
		helpDialog.open();
        while (!helpDialog.isDisposed()) {
			if (!g_display.readAndDispatch()) {
				if(helpImg!=null) helpImg.dispose();
				g_display.sleep();
			}
		}
	}

	/**
	 * 데이터 내보내기.
	 * @param btn1Line
	 * @param btnRowData
	 */
	private void initExportBtn(Composite btn1Line, RowData btnRowData) {
		
		
		final Button exBtn = UiUtil.createButton(btn1Line, resource.getString("export_btn", "Export"),SWT.PUSH);	
	    
		exBtn.setLayoutData(btnRowData);
    	
		exBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		
	    		FileDialog fileDialog = new FileDialog(exBtn.getShell(), SWT.SAVE);
				String filename = fileDialog.open();
				if (filename == null) {
					return;
				}
				String tmpExt = "."+TrayConfigurationConstants.TRAY_DATA_EXT;
				filename = filename.indexOf(tmpExt) < 0 ? filename+tmpExt:filename;
	    		ExportDataControl tdc= new ExportDataControl(filename);
	    		
	    		TableItem[] item = dataTable.getItems();
	    		int len = item.length;
	    		
	    		TableItem tmpItem = null;
	    		DataEntity tmpEntity = null;
	    		try {
		    		for (int i = 0; i < len; i++) {
		    			tmpItem = item[i];
						if(tmpItem.getChecked()){
							tmpEntity = (DataEntity)tmpItem.getData(TrayKeyConstants.ENTRY_NM);
							tdc.createItem(tmpEntity );
						};
					}
	    		
					tdc.store();
				} catch (Exception e) {
					MessageDialog.openError(exBtn.getShell(), "Error","\n"+ e.toString());
				}
	    		exBtn.getShell().dispose();
	    	}
    	});
		
	}
	
	/**
     * 데이타 가져오기. 
     * @param btnFirst
	 * @param btnRowData 
     */
    private void initImportBtn(Composite btnFirst, RowData btnRowData) {
    	final Button addBtn = UiUtil.createButton(btnFirst, resource.getString("import_btn", "Import"),SWT.PUSH);	
	    
	    addBtn.setLayoutData(btnRowData);
    	
    	addBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		TableItem[] item = dataTable.getItems();
	    		int len = item.length;
	    		TableItem tmpItem = null;
	    		DataEntity tmpEntity = null;
	    		for (int i = 0; i < len; i++) {
	    			tmpItem = item[i];
					if(tmpItem.getChecked()){
						tmpEntity = (DataEntity)tmpItem.getData(TrayKeyConstants.ENTRY_NM);
						TrayUserDataControl.newIntance().createItem(tmpEntity );
					};
				}
	    		try {
					TrayUserDataControl.newIntance().store();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    		g_newTrayInfoInsertForm.refreshUserData();
	    		addBtn.getShell().dispose();
	    	}
    	});
	}
    
    /**
     * 닫기 버튼. 
     * @param btnFirst
     * @param btnRowData 
     */
    private void closeBtnInit(final Composite btnFirst, RowData btnRowData) {
    	Button btn = UiUtil.createButton(btnFirst, resource.getString("cancle_btn", "Cancle") ,SWT.PUSH);
    	
    	btn.setLayoutData(btnRowData);
    	
    	btn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
				btnFirst.getShell().close();
	    	}
    	});
	}

	/**
	 * 모두 선택.
	 * @param allCheckLine
	 */
	private void allCheckLineGrid(Composite allCheckLine) {
		final Button allcheck = UiUtil.createButton(allCheckLine, resource.getString("all_select_check","All") ,SWT.CHECK);
		FormData data = new FormData();
		data.left= new FormAttachment(0,12);
		data.height=25;
		allcheck.setLayoutData(data);
    	
		allcheck.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		TableItem[] item = dataTable.getItems();
	    		int len = item.length;
	    		
	    		boolean checkFlag =allcheck.getSelection();
	    		
	    		for (int i = 0; i < len; i++) {
					item[i].setChecked(checkFlag);
				}
	    	}
    	});
	}
	
	/**
	 * 링크 생성.
	 * @param text
	 */
	private void createHyperLink(StyledText text) {
		String content = text.getText();
		
		String[] textLineArr = StringUtil.split(content, "\n");
		
		int len  = textLineArr.length;
		int idx = -1, totLen =-1, tmpLineLen=-1;
		String tmpLine; 
		HyperLinkRange range;
		int cnt = 1; 
		for (int i = 0; i < len; i++) {
			tmpLine = textLineArr[i];
			tmpLineLen = tmpLine.length();
			idx = tmpLine.indexOf("http");
			if(idx >-1 ){
				tmpLine = tmpLine.substring(idx);
				range =new HyperLinkRange();
				range.start = totLen+idx+(cnt+1);
				range.length = tmpLine.length();
				range.fontStyle = SWT.BOLD;
				range.foreground = g_display.getSystemColor(SWT.COLOR_BLUE);
				range.keyword = tmpLine;
				ranges.add(range);
				
				//System.out.println(tmpLine+" | "+idx+" | "+range.length);
				text.setStyleRange(range);
				cnt =cnt+1;
			}
			totLen = totLen+tmpLineLen;
		}
	}
	
	/**
	 * 하이퍼 링크 관리. 
	 * @param x
	 * @param y
	 * @return
	 */
	private HyperLinkRange getHyperLinkRange(int x, int y) {
		Point p = new Point(x, y);
		try {
			int offset = text.getOffsetAtLocation(p);
			for (int i = 0; i < ranges.size(); i++) {
				HyperLinkRange r = (HyperLinkRange) ranges.get(i);
				int offset1 = r.start;
				int offset2 = offset1 + r.length;
				if (offset >= offset1 && offset <= offset2) {
					return r;
				}
			}
		} catch (IllegalArgumentException ex) {
			// カーソルの位置によっては、Offsetが計算できず、커서의 위치에 있어서는 오프셋을 계산할수 없습니다.
			// Exceptionが発生する。したがってここでは何もしない。익셉션이 발생합니다.그리고 여기서는 아무것도 하지 않습니다.
		}
		return null;
	}
	
	/**
	 * 하이퍼 링크 마우스 액션
	 * @author ytkim
	 *
	 */
	class HyperLinkMouseListener implements MouseListener{

		public void mouseDoubleClick(MouseEvent arg0) {
			return;
		}

		public void mouseDown(MouseEvent event) {
			int x = event.x;
			int y = event.y;
			HyperLinkRange range = getHyperLinkRange(x, y);
			if (range != null) {
				try {
					Desktop.getDesktop().browse(new URI(range.keyword));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return;
		}

		public void mouseUp(MouseEvent arg0) {
			return;
		}
		
	}
	
	/**
	 * 하이퍼링크 마우스 움직임 링크.
	 * @author ytkim
	 *
	 */
	class HyperLinkMouseMove implements MouseMoveListener{

		public void mouseMove(MouseEvent event) {
			int x = event.x;
			int y = event.y;
			HyperLinkRange range = getHyperLinkRange(x, y);
			if (range != null) {
				text.setCursor(handCursor);
			} else {
				text.setCursor(arrowCursor);
			}
		}
	}

	class HyperLinkRange extends StyleRange {
		String keyword;
	}
}
