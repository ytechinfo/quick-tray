package com.quick.tray.form;

import java.io.IOException;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.quick.tray.config.TrayConfigurationConstants;
import com.quick.tray.constants.TrayKeyConstants;
import com.quick.tray.constants.UIConstants;
import com.quick.tray.data.ExportDataControl;
import com.quick.tray.data.TrayUserDataControl;
import com.quick.tray.entity.DataEntity;
import com.quick.tray.lang.ResourceControl;
import com.quick.tray.main.TrayMain;
import com.quick.tray.ui.UiUtil;
import com.quick.util.TrayUIUtil;

public class DataExportImportForm {
	Display g_display; 
	
	final int dataGridW = 260 , dataGridH = 320;
	
	final int btnW = UIConstants.BTN_W, btnH = UIConstants.BTN_H;
	
	Table dataTable;
	
	final private DataEntity  resource = ResourceControl.getInstance().getResource();
	
	private TrayInfoInsertForm trayInfoInsertForm;
	
	public DataExportImportForm(Display g_display ,TrayInfoInsertForm trayInfoInsertForm) {
		this.g_display = g_display;
		this.trayInfoInsertForm =trayInfoInsertForm; 
	}

	public void open(String mode,Shell g_shell, List<DataEntity> dataList){
		
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
		
        dialog.setLocation (TrayUIUtil.getCenterPoint(g_display, dialog));
		
		dialog.open();
        while (!dialog.isDisposed()) {
			if (!g_display.readAndDispatch()) {
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
	    		trayInfoInsertForm.refreshUserData();
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

}
