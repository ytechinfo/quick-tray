/**
(#)AppConfigForm.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : AppConfigForm
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :    
*/
package com.quick.tray.form;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.quick.tray.ad.TrayAdInfo;
import com.quick.tray.bean.AppConfigBean;
import com.quick.tray.config.TrayConfig;
import com.quick.tray.config.TrayConfigurationConstants;
import com.quick.tray.constants.TrayKeyConstants;
import com.quick.tray.data.TrayAppDataControl;
import com.quick.tray.data.TrayUserDataControl;
import com.quick.tray.entity.DataEntity;
import com.quick.tray.lang.ResourceControl;
import com.quick.tray.main.TrayMain;
import com.quick.tray.ui.UiUtil;
import com.quick.tray.utils.TrayUtils;
import com.quick.util.TrayUtil;

public class AppConfigForm {
	
	Display g_display;
	
    FileDialog g_fileDialog;  
  
    File g_file; //  
    
    Label processLabel , deleteStrLbl;
    
    Text nameTxt;
    Text processTxt;
    
    Combo combo;
    Button fileOpenBtn=null;
    //오픈 창 사이즈
    //int windowW=370, windwoH=280;
    int windowW=380, windwoH=390;
    
    int inputSize=140;
    
    int btnW = 74 , btnH =23, btnHalfSize =btnW/2 ;
    
    int labelH = 15;
    int textH = 15;
    
    int comboListW=123;
    int topPadding = -3;
    int inputFormTopPadding = 2;
    
    //left padding
    FormAttachment cLeft =new FormAttachment(0, 5);
    FormAttachment labelTextLeft =new FormAttachment(0, 10);
    
    //process list
    List selectProcessList;
    
    //사용자 user list
    List selectAppList;
    private String selectComboTxtStr="";
    
    Composite processComposite = null;
    Image g_image = null;
    
    final String[] typeArr ={"app","web"};

    boolean sortFlag = false;
    
    boolean changeFlag = false; 
    
    final private DataEntity  resource = ResourceControl.getInstance().getResource();

	public AppConfigForm(Display display) {
		g_display=display;
	}

	/** 
     * shell 을 만들고 shell을 반환한다. 
     */
    public Shell open(Shell shell) { 
    	changeFlag = false; 
    	final Shell g_shell = new Shell(shell ,SWT.CLOSE);
    	
    	//new TrayInsertMenu().menuGrid(display,g_shell , this);
    	g_shell.setData(this.getClass().getName());
    	
    	g_shell.setText(resource.getString("top_menu_appconfig", "app config")); 
        g_shell.setSize(windowW, windwoH); // 두께, 높이  
        
        try {
			g_shell.setImage(TrayMain.ICON_IMAGE);
		} catch (Exception e) {
			
		}
    	
        g_shell.setLayout(new FillLayout());
		
		Composite top = new Composite(g_shell, SWT.NONE);
		top.setLayout(new FormLayout());
		top.setSize(g_shell.getSize());
		
		FormData data = new FormData();
		
	    //왼쪽 그리기.
		Composite topChildLeft = new Composite(top, SWT.LEFT);
		topChildLeft.setLayout(new RowLayout(SWT.VERTICAL));
		topChildLeftGrid(topChildLeft);
		
	    data.top = new FormAttachment(0, 0);
	    data.left = new FormAttachment(0, 0);
	    topChildLeft.setLayoutData(data);
		
		//오른쪽 그리기
		Composite topChildRight = new Composite(top, SWT.SHADOW_IN|SWT.LEFT);
		topChildRight.setLayout(new FormLayout());
		topChildRightGrid(topChildRight);
		
		data = new FormData();
	    data.top = new FormAttachment(0, 3);
	    data.left = new FormAttachment(topChildLeft,0);
	    topChildRight.setLayoutData(data);
	    
		Composite topChildBottom = new Composite(top, SWT.SHADOW_IN|SWT.LEFT);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		
		topChildBottom.setLayout(gridLayout);
		bottomAdGrid(topChildBottom,g_display);
		
		data = new FormData();
	    data.top = new FormAttachment(topChildLeft, -5);
	    data.left = new FormAttachment(0,-3);
	    data.width=380;
	    data.height=80;
	    topChildBottom.setLayoutData(data);		
		
	    g_shell.setFocus();
        // 메뉴 관리폼 중간에 띄우기. 시작 
        g_shell.setLocation (TrayUtil.getCenterPoint(g_display, g_shell));
        // 메뉴 관리폼 중간에 띄우기 끝
        
        g_shell.open();
        
        g_shell.addListener(SWT.Close, new Listener(){ 
           public void handleEvent(Event event){
        	  if(g_image!=null){
        		  g_image.dispose();
        	  }
        	  
        	  String message = "\""+resource.getString("menu_mgmt", "Menu Management")+"\" restart";
  			
        	  if(changeFlag){
        		  MessageDialog.openWarning(g_shell, "Warning",message);
        	  }
  			
              g_shell.dispose();
           } 
        }); 
	    
        return g_shell;  
    }  
    
    /**
     * 이미지 그리기.
     * @param topChildBottom
     * @param display
     */
	private void bottomAdGrid(Composite topChildBottom, Display display) {
		//왼쪽 이미지
		final Label adImageLbl = UiUtil.createLabel(topChildBottom,"" ,SWT.NONE );
		adImageLbl.setAlignment(SWT.CENTER|SWT.VERTICAL);
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		adImageLbl.setBackground(white);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.heightHint = 70;
		gridData.verticalSpan = 3;
		
		adImageLbl.setLayoutData(gridData);
		try {
			java.util.List<java.util.Map> adList = TrayConfig.AD_LIST;
			int size= adList.size();
			InputStream is = null;
			String callUrl = "";
			if(size > 0){
				Random oRandom = new Random();

			    int randomIdx= oRandom.nextInt(size);
			    
			    Object obj =adList.get(randomIdx).get(TrayKeyConstants.AD_STREAM);
			    
			    if(obj !=null){
			    	is =(InputStream)obj;
			    	callUrl = String.valueOf(adList.get(randomIdx).get(TrayKeyConstants.AD_URL));
			    }else{
			    	for (int i = 0; i < size; i++) {
						if(randomIdx!=i){
							obj =adList.get(i).get(TrayKeyConstants.AD_STREAM);
							is = obj!=null?(InputStream)obj:is;
							callUrl = String.valueOf(adList.get(randomIdx).get(TrayKeyConstants.AD_URL));
						}
					}
			    }
			}
			adImageLbl.setData("url",callUrl);
			if(is==null){
				adImageLbl.setText("\nQUICK TRAY");
			}else{
				Cursor handCursor = new Cursor(display, SWT.CURSOR_HAND);
				g_image = new Image(display, is);
				adImageLbl.setImage(g_image);
				adImageLbl.setCursor(handCursor);
				adImageLbl.addMouseListener(new MouseAdapter() {
					public void mouseDown(MouseEvent event) {
						String url = String.valueOf(event.widget.getData("url"));
						try {
							Desktop.getDesktop().browse(new URI(url));
						} catch (Exception e) {}
					}
				});
			}
		} catch (Exception e) {
			adImageLbl.setText("QUICK TRAY");
		}
		new Thread(new TrayAdInfo()).start();
	}

	/**
     * 왼쪽 그리기
     * @param topChildLeft
     */
    private void topChildLeftGrid(Composite topChildLeft) {
    	selectUserInfoBoxProcess(topChildLeft);
	}
    
    /**
     * 오른쪽 그리기.
     * @param topChildRight
     */
    private void topChildRightGrid(Composite topChildRight) {
    	
    	RowData btnRowData = UiUtil.getRowData(btnW, btnH);
    	RowData btnHalfRowData = UiUtil.getRowData(btnHalfSize-2,btnH );
    	
    	Composite btn1Line = new Composite(topChildRight, SWT.SHADOW_IN|SWT.LEFT);
    	btn1Line.setLayout(new RowLayout(SWT.HORIZONTAL));
    	
    	addBtnInit(btn1Line,btnRowData);
    	closeBtnInit(btn1Line,btnRowData);
    	
    	FormData data = new FormData();
	    data.left = cLeft;
	    btn1Line.setLayoutData(data);
    	
    	Composite btn2Line = new Composite(topChildRight, SWT.SHADOW_IN|SWT.LEFT);
    	btn2Line.setLayout(new RowLayout(SWT.HORIZONTAL));
    	delBtnInit(btn2Line,btnRowData);
    	saveBtnInit(btn2Line,btnRowData);
    	
    	data = new FormData();
	    data.top = new FormAttachment(btn1Line, topPadding);
	    data.left = cLeft;
	    btn2Line.setLayoutData(data);
    	
    	Composite btn3Line = new Composite(topChildRight, SWT.SHADOW_IN|SWT.LEFT);
    	btn3Line.setLayout(new RowLayout(SWT.HORIZONTAL));
    	
    	sortTopBtninit(btn3Line,btnHalfRowData);
    	sortBottomBtnInit(btn3Line,btnHalfRowData);
    	
    	data = new FormData();
	    data.top = new FormAttachment(btn2Line, topPadding);
	    data.left = cLeft;
	    btn3Line.setLayoutData(data);
	    
	    //-----------------------------------버튼 끝----------------------------------------------
	    
	    inputFormInit(topChildRight, btn3Line);
	    
	}
    
    /**
     * 
     * @param topChildRight
     * @param btn3Line
     */
    private void inputFormInit(Composite topChildRight, Composite btn3Line) {
    	
    	FormData data = new FormData();
	   
    	Label label = UiUtil.createLabel(topChildRight, resource.getString("type_txt", "Type : "), SWT.LEFT);
    	
    	data.top = new FormAttachment(btn3Line, inputFormTopPadding);
 	    data.left = labelTextLeft;
 	    label.setLayoutData(data);
    	
 	 //타입
    	combo = UiUtil.createCombo(topChildRight, typeArr, SWT.LEFT|SWT.READ_ONLY);
    	combo.setLayoutData(UiUtil.getFormData(new FormAttachment(label, inputFormTopPadding)
 	    , labelTextLeft
 	    , comboListW
 	    , textH));
    	combo.select(0);
    	
    	combo.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    			String comboTxt = combo.getItem(combo.getSelectionIndex());
    			
    			if(selectComboTxtStr.equals(comboTxt))return ; 
    			selectComboTxtStr = comboTxt;
    		};
    	});
    	
    	Label nameLabel = UiUtil.createLabel(topChildRight, resource.getString("app_name_txt", "App Name : "), SWT.LEFT);
    	nameLabel.setLayoutData(UiUtil.getFormData(new FormAttachment(combo, inputFormTopPadding)
 	    , labelTextLeft
 	    , comboListW
 	    , textH));
        // 트레이명 txt 필드  
    	
        nameTxt = new Text(topChildRight, SWT.LEFT|SWT.BORDER); 
        nameTxt.setLayoutData(
        		UiUtil.getFormData(new FormAttachment(nameLabel, inputFormTopPadding)
         	    , labelTextLeft
         	    , inputSize
         	    , textH));
        
        nameTxt.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				if("New Name".equals(nameTxt.getText())){
					nameTxt.setText("");
				}
			}
		});
        
        processComposite = new Composite(topChildRight, SWT.LEFT);
        processComposite.setLayout(new FormLayout());
        
        data = new FormData();
	    data.top = new FormAttachment(nameTxt, inputFormTopPadding);
	    data.left = new FormAttachment(0, 10);
	    data.width = 200;
	    data.height = 100;
        processComposite.setLayoutData(data);
        
        //프로세스 라벨
		processLabel = UiUtil.createLabel(processComposite, "URL : ", SWT.LEFT);
		data = new FormData();
	    data.top = new FormAttachment(0, 3);
	    data.width = 60;
	    data.height = labelH;
		processLabel.setLayoutData(data);
        processTxtProcess(processComposite);
      //프로세스 라벨
      	comboSelectProcess("app");
        
	}
    
	/**
     * 위 버튼 처리
	 * @param btnHalfRowData 
     * @param shell
     */
    private void sortTopBtninit(Composite btn3Line, RowData btnHalfRowData) {
    	Button topBtn = UiUtil.createButton(btn3Line,resource.getString("top_btn", "Top") ,SWT.PUSH);
    	topBtn.setLayoutData(btnHalfRowData);
    	
    	topBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		int sIdx = selectAppList.getSelectionIndex();
	    		
	    		if(sIdx < 0) return ;
	    		
	    		String [] item  = selectAppList.getItems();
	    		
	    		if(0==sIdx) return ;
	    		
	    		sortFlag = true;
	    		
	    		selectAppList.setItem(sIdx-1, item[sIdx]);
	    		selectAppList.setItem(sIdx, item[sIdx-1]);
	    		selectAppList.setSelection(sIdx-1);
	    		
	    	}
    	});
	}
    
    /**
     * 아래 버튼 처리.
     * @param btn3Line
     * @param btnHalfRowData
     */
    private void sortBottomBtnInit(Composite btn3Line, RowData btnHalfRowData) {
    	
    	Button bottomBtn = UiUtil.createButton(btn3Line,resource.getString("bottom_btn", "Bottom"),SWT.PUSH);
    	bottomBtn.setLayoutData(btnHalfRowData);
    	
    	bottomBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		
	    		int sIdx = selectAppList.getSelectionIndex();
	    		
	    		if(sIdx < 0) return ;
	    		
	    		String [] item  = selectAppList.getItems();
	    		int itemLen = item.length;
	    		
	    		if(itemLen-1==sIdx) return ;
	    		
	    		sortFlag = true; 
	    		
	    		selectAppList.setItem(sIdx+1, item[sIdx]);
	    		selectAppList.setItem(sIdx, item[sIdx+1]);
	    		selectAppList.setSelection(sIdx+1);
	    		
	    	}
    	});
	}
    
    /**
     * 저장 버튼.
     * @param btn2Line
     * @param btnRowData 
     */
    private void saveBtnInit(final Composite btn2Line, RowData btnRowData) {
    	Button saveBtn = UiUtil.createButton(btn2Line,resource.getString("save_btn", "Save") ,SWT.PUSH);
    	
    	saveBtn.setLayoutData(btnRowData);
    	
    	saveBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
		    	String tmpProcess = "";
		    	AppConfigBean inInfo = new AppConfigBean();
		    	
		    	int sIdx = selectAppList.getSelectionIndex();
		    	
		    	if(sIdx >-1){
					inInfo = getSelectItem(sIdx);
				}
		    	
				String [] item = selectProcessList.getItems();
				StringBuilder procSb = new StringBuilder();
		    	for (int i = 0; i < item.length; i++) {
			    	procSb.append(i==0?"":TrayConfigurationConstants.DELIMETER);
			    	procSb.append(item[i]);
		    	}
		    	tmpProcess= procSb.toString();
		    	
		    	String name = nameTxt.getText();
		    	
		    	if("".equals(name)){
		    		MessageDialog.openWarning(btn2Line.getShell(), "Warning",resource.getString("name_alert", "Enter a name"));
		    		return ; 
		    	}
		    	
		    	if("".equals(tmpProcess)){
		    		MessageDialog.openWarning(btn2Line.getShell(), "Warning",resource.getString("process_alert", "Enter a process"));
		    		return ; 
		    	}
		    	
		    	inInfo.setName(name);
		    	inInfo.setType(combo.getItem(combo.getSelectionIndex()));
		    	inInfo.setCommand(tmpProcess);
		    	
		    	try {
					if(sIdx >-1){
						TrayAppDataControl.newIntance().modify(inInfo);
						selectAppList.setItem(sIdx, name);
					}else{
						if(TrayAppDataControl.newIntance().isElement(inInfo.getName())){
							MessageDialog.openWarning(btn2Line.getShell(), "Warning",resource.getString("app_duplicate_alert", "app name duplicate"));
							return ; 
						}
						
			    		TrayAppDataControl.newIntance().createItem(inInfo );
			    		int itenLen = selectAppList.getItems().length;
			    		selectAppList.add(name,itenLen);
					}
				
					TrayAppDataControl.newIntance().store();
					changeFlag = true; 
					inputDataClear("insert");
				} catch (Exception e) {
					// TODO 자동 생성된 catch 블록
					e.printStackTrace();
				}
	    	}
    	});
	}
    
    /**
     * 삭제 버튼.
     * @param btn2Line
     * @param btnRowData 
     */
	private void delBtnInit(Composite btn2Line, RowData btnRowData) {
		Button delBtn = UiUtil.createButton(btn2Line, resource.getString("delete_btn", "Delete") ,SWT.PUSH);
		
		delBtn.setLayoutData(btnRowData);
    	
    	delBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	
		    	int sIdx= selectAppList.getSelectionIndex();
	    		
	    		if(sIdx> -1){
	    			AppConfigBean appConfigBean = getSelectItem(sIdx);
		
			    	try {
			    		TrayAppDataControl.newIntance().deleteItem(appConfigBean.getEntryId());
						TrayAppDataControl.newIntance().store();
						changeFlag = true; 
						
						selectAppList.remove(sIdx);
						int itemCnt = selectAppList.getItemCount();
						
						if(sIdx == itemCnt){
							selectAppList.setSelection(sIdx-1);
						}else{
							selectAppList.setSelection(sIdx);
						}
						inputDataClear("del");
					} catch (Exception e) {
						// TODO 자동 생성된 catch 블록
						e.printStackTrace();
					}
	    		}
	    	}
    	});
	}

	/**
     * 추가 버튼. 
     * @param btnFirst
	 * @param btnRowData 
     */
    private void addBtnInit(Composite btnFirst, RowData btnRowData) {
    	Button addBtn = UiUtil.createButton(btnFirst, resource.getString("add_btn", "Add") ,SWT.PUSH);	
	    
	    addBtn.setLayoutData(btnRowData);
    	
    	addBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		selectAppList.deselectAll();
	    		inputDataClear("add");
	    	}
    	});
	}
    
    /**
     * 닫기 버튼. 
     * @param btnFirst
     * @param btnRowData 
     */
    private void closeBtnInit(final Composite btnFirst, RowData btnRowData) {
    	Button btn = UiUtil.createButton(btnFirst,resource.getString("done_btn", "Done") ,SWT.PUSH);
    	
    	btn.setLayoutData(btnRowData);
    	
    	btn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
				try {
					if(sortFlag){
						java.util.List<String> entryKeyList = new ArrayList<String>();
						
						int len = selectAppList.getItemCount();
						for(int i =0 ; i <len ;i++){
							entryKeyList.add(getSelectItem(i).getEntryId());
						}
						
						TrayAppDataControl.newIntance().dataSort(entryKeyList);
						TrayAppDataControl.newIntance().store();
						changeFlag = true; 
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	    		btnFirst.getShell().close();
	    	}
    	});
		
	}

    /**
     * 프로세스 텍스트 박스
     * @param topChildLeft
     */
    private void processTxtProcess(Composite topChildLeft) {
    	// 트레이명 txt 필드
    	if(processTxt != null){
    		if(!processTxt.isDisposed()){
    			processTxt.setText("");
    			return ; 
    		}
    	}
		processTxt = new Text(topChildLeft, SWT.LEFT|SWT.BORDER);  
		
		FormData data = new FormData();
	    data.top = new FormAttachment(processLabel, 0);
	    data.width = inputSize;
	    data.height = textH;
		
		processTxt.setLayoutData(data);
	}

	/**
     * 사용자 설정 목록
     * @param topChildLeft
     */
    private void selectUserInfoBoxProcess(final Composite topChildLeft) {
    	selectAppList = new List(topChildLeft, SWT.BORDER|SWT.V_SCROLL);
    	
    	RowData rd = new RowData();  
        rd.height = windwoH-130; 
        rd.width=170;
        selectAppList.setLayoutData(rd);
    	
    	setUserInfoBoxData(); // 사용자 tray 정보 셋팅.
    	
    	selectAppList.addListener(SWT.MouseDown, new Listener () {
			public void handleEvent (Event  e) {
				selectDataSetting(topChildLeft);
			}
		});
    	
    	selectAppList.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_DOWN ||e.keyCode == SWT.ARROW_UP) {
					selectDataSetting(topChildLeft);
				}
				return ; 
			}
			
			public void keyPressed(KeyEvent e) {
				return ; 
			}
		});
	}
    
    /**
     * 정보 선택시.
     * @param topChildLeft
     */
    protected void selectDataSetting(Composite topChildLeft) {
    	int sIdx = selectAppList.getSelectionIndex();
		if(sIdx >-1){
			
			AppConfigBean appConfigBean = getSelectItem(sIdx);
			
			String type = appConfigBean.getType();
			
			nameTxt.setText(appConfigBean.getName());
			
			String comboTxt = combo.getItem(combo.getSelectionIndex());
			
			selectComboTxtStr = type;
			
			//System.out.println("comboTxt ["+comboTxt + "] |selectComboTxtStr[ "+selectComboTxtStr+"]");
			
			if(!type.equals(comboTxt)){
				String [] items = combo.getItems();
				
				for (int i = 0; i < items.length; i++) {
					if(type.equals(items[i])){
						combo.select(i);
						break;
					}
				}
				comboSelectProcess(type);
			}
			
			type=getComboType(type);
			
			if(processTxt!=null) processTxt.setText("");
			selectProcessList.removeAll();
			selectProcessList.setItems(TrayUtils.split(appConfigBean.getCommand(),TrayConfigurationConstants.DELIMETER));
			
		}
	}
    
    /**
     * 사용자 정보 새로고침.
     */
    public void refreshUserData(){
    	setUserInfoBoxData();
    }

	/**
     * 사용자 tray 정보 셋팅.
     */
    final private void setUserInfoBoxData(){
    	TrayAppDataControl udc=TrayAppDataControl.newIntance();
    	java.util.List<AppConfigBean> menuList = udc.getItemList();
    	
    	//팝업 메뉴에 등록 
    	AppConfigBean tmpMenu = null;
    	int menuSize = menuList.size();
    	String [] userInfoArr = new String[menuSize];
    	for (int i = 0; i < menuSize; i++) {
    		tmpMenu = menuList.get(i);
    		userInfoArr[i]=tmpMenu.getName();
		}
    	
    	selectAppList.setItems(userInfoArr);
    }
    
    /**
     * 타입 체크. 
     * @param comboTxt
     * @return
     */
    final private String getComboType(String comboTxt){
    	if(comboTxt.toLowerCase().indexOf("web") >-1){
			comboTxt="web";
		}
    	return comboTxt;
    }
    
    /**
     * 타입 선택했을때 액션
     * @param comboTxt
     * @param topChildLeft
     */
	final protected void comboSelectProcess(String comboTxt) {
		comboTxt=getComboType(comboTxt);
		
		processLabel.setText(resource.getString("process_txt", "Process : "));
		
		if(processTxt != null){
			processTxt.setVisible(false);
		}
		
		if(selectProcessList !=null && !selectProcessList.isDisposed()){
			selectProcessList.setVisible(true);
			deleteStrLbl.setVisible(true);
		}else {
			selectBoxProcess(processComposite);
		}
		
	     // "파일 열기" 버튼 만들기
		if(fileOpenBtn !=null && !fileOpenBtn.isDisposed()){
			fileOpenBtn.setVisible(true);
		}else{
			fileOpenButton(processComposite,resource.getString("file_btn", "File"));
		}
		
		processComposite.pack();
	}

	/**
	 * 등록 데이터 필드 지우기. 
	 */
	final private void inputDataClear(String type) {
		String nameStr="New Name";
		if("del".equals(type)){
			nameStr="";
		}
		nameTxt.setText(nameStr);
		
		if(processTxt != null){
			if(!processTxt.isDisposed()){
				processTxt.setText("");
			}
		}
		if(selectProcessList != null){
			if(!selectProcessList.isDisposed())	selectProcessList.removeAll();
		}
	}

	/**
     * select box 프로세스 처리.
     * @param topChildLeft
     */
    final private void selectBoxProcess(Composite topChildLeft) {
    	selectProcessList = new List(topChildLeft, SWT.LEFT|SWT.BORDER|SWT.V_SCROLL);
    	
    	FormData data = new FormData();
	    data.top = new FormAttachment(processLabel, 3);
	    data.width = comboListW;
	    data.height = 40;
	    
		selectProcessList.setLayoutData(data);
		
		selectProcessList.addListener(SWT.MouseDoubleClick, new Listener () {
			public void handleEvent (Event  e) {
				int sIdx = selectProcessList.getSelectionIndex();
				if(sIdx >-1) selectProcessList.remove(selectProcessList.getSelectionIndex());
			}
		});
		
		selectProcessList.addListener(SWT.MouseDown, new Listener () {
			public void handleEvent (Event  e) {
				int sIdx = selectProcessList.getSelectionIndex();
				if(sIdx >-1) selectProcessList.setToolTipText(selectProcessList.getItem(sIdx));
			}
		});
		
		deleteStrLbl = UiUtil.createLabel(processComposite, resource.getString("delete_msg_txt", "※Double-click delete※"), SWT.CENTER);
		data = new FormData();
	    data.top = new FormAttachment(selectProcessList, 3);
	    data.width = inputSize;
	    data.height = 20;
		deleteStrLbl.setLayoutData(data);
	}
    
	/** 
     * 파일 열기 버튼을 
     */  
    final private Button fileOpenButton(Composite composite, String nm) {  
    	fileOpenBtn = UiUtil.createButton(composite, nm , SWT.PUSH);
        
        FormData data = new FormData();
	    data.top = new FormAttachment(0, 0);
	    data.left = new FormAttachment(processLabel,5);
	    data.width = 80;
	    data.height = 20;
	    
	    fileOpenBtn.setLayoutData(data);
	    
	    fileOpenBtn.addSelectionListener(new SelectionAdapter() {  
            public void widgetSelected(SelectionEvent event) {
                 g_fileDialog = new FileDialog(selectProcessList.getShell(), SWT.OPEN);
                   
                 g_fileDialog.setFilterExtensions(new String[] { "*.*" });  
                 String filePath = g_fileDialog.open();  
                 
                 String fileNm = g_fileDialog.getFileName();
                 
                 if(null != filePath){
                	 String beforeName ="";
                	 if(selectProcessList.getItemCount() > 0){
                		 beforeName = selectProcessList.getItem(0);
                		 selectProcessList.removeAll();
                	 }
                	 
                	 selectProcessList.add(filePath);
                	 String tnt = nameTxt.getText();
                	 
                	 if(beforeName.endsWith(tnt)){
                		 tnt = ""; 
                	 }
                	 
                	 if("New Name".equals(tnt) || "".equals(tnt)){
                		 nameTxt.setText(fileNm);
                	 }
                 }
            }  
        });  
        return fileOpenBtn;  
    }
    
    public AppConfigBean getSelectItem(int sIdx){
		return TrayAppDataControl.newIntance().getItemMap(TrayAppDataControl.newIntance().getUID(selectAppList.getItem(sIdx)));
    }
    
	public static void main(String[] args) {
		
		Display display = new Display();  
		final Shell g_shell = new Shell(display ,SWT.CLOSE);
		
		AppConfigForm tii = new AppConfigForm(display);  
		
        Shell shell = tii.open(g_shell);
        
        while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}  
}
