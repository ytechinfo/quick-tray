/**
(#)TrayInfoInsertForm.java

Copyright (c) 2013 qis
All rights reserved.

CLASS_NAME : TrayInfoInsertForm
프로그램 생성정보 :  2013-02-15 / ytkim
프로그램 수정정보 :    
*/
package com.quick.tray;

import java.io.File;

import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.quick.tray.config.TrayConfig;
import com.quick.tray.config.TrayConfigurationConstants;
import com.quick.tray.data.TrayUserDataControl;
import com.quick.tray.entity.DataEntity;
import com.quick.tray.ui.UiUtil;

public class TrayInfoInsertForm {
	Shell g_shell;  
	Display g_display;
    FileDialog g_fileDialog;  
  
    File g_file; //  
    
    Label processLabel , deleteStrLbl;
    
    Text nameTxt;
    Text processTxt;
    Button fileOpenBtn;
    
    Combo combo;
    
    //추가 , 등록, 취소 버튼.
    Button addBtn, delBtn, inBtn, cancleBtn , topBtn, bottomBtn;
    
    //오픈 창 사이즈
    int windowW=370, windwoH=280;
    
    int leftPadding = 200, btnSize=74 , inputSize=150;
    
    int btnHalfSize=btnSize/2 ;
    
    //process list
    List selectProcessList;
    
    //사용자 user list
    List selectUserList;
    private String selectComboTxtStr="";
    
    final String[] typeArr =TrayConfig.getInstance().getProperties().split("tray.process.type",",");

	public TrayInfoInsertForm(){
		
	}

	/** 
     * shell 을 만들고 shell을 반환한다. 
     */  
    public Shell open(Display display) {  
    	
    	Shell [] allShell = display.getShells();
    	String classNm = this.getClass().getName();
    	Shell ts = null;

    	boolean viewFlag = false;
    	for (int i = 0; i < allShell.length; i++) {
    		ts = allShell[i];
    		if(classNm.equals(String.valueOf(ts.getData()))){
    			ts.open();
    			viewFlag= true; 
    			break; 
    		}
		}
    	
    	if(viewFlag) return ts;
    	
    	g_shell = new Shell(display ,SWT.CLOSE);
    	g_shell.setData(this.getClass().getName());
    	g_display = display;
    	
    	//g_shell.setImage(arg0)
        
    	g_shell.setText("메뉴 관리"); 
        g_shell.setSize(windowW, windwoH); // 두께, 높이  
        
        formView(g_shell);
        
        return g_shell;  
    }  
    
    /** 
     * shell 을 생성한다. 
     */  
    public void shellOpen() {  
        g_shell.setFocus();
        // 메뉴 관리폼 중간에 띄우기. 시작 
        Monitor primary = g_display.getPrimaryMonitor ();
        Rectangle bounds = primary.getBounds ();
        Rectangle rect = g_shell.getBounds ();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 3;
        g_shell.setLocation (x, y);
        // 메뉴 관리폼 중간에 띄우기 끝
        
        g_shell.open();
        
        g_shell.addListener(SWT.Close, new Listener(){ 
           public void handleEvent(Event event){ 
              g_shell.dispose(); 
           } 
        }); 
    } 
    
    /**
     * 메뉴 관리 ui 그리기.
     * @param shell
     */
    public void formView(final Shell shell) {
		selectUserInfoBoxProcess(shell);
		
		insertCancleBtnProcess(shell);
		Label label = UiUtil.createLabel(shell, "타입 : ", SWT.LEFT);
		//left, top, with, height
		label.setBounds(leftPadding, 50 ,120 ,15); 
		
		typeComboProcess(shell);
		
    	Label nameLabel = UiUtil.createLabel(shell, "트레이명 : ", SWT.LEFT);
    	nameLabel.setBounds(leftPadding,95, 120, 15); 
  
        // 트레이명 txt 필드  
        nameTxt = new Text(shell, SWT.LEFT|SWT.BORDER);  
        nameTxt.setBounds(leftPadding, 115, inputSize, 20);  
        
        //프로세스 라벨
		processLabel = UiUtil.createLabel(shell, "URL : ", SWT.LEFT);
		processLabel.setBounds(leftPadding, 135,60 ,15);
        
        processTxtProcess(shell);
        
        shellOpen();  // shell오픈 하기. 
	}
    
    /**
     * 프로세스 텍스트 박스
     * @param shell
     */
    private void processTxtProcess(Shell shell) {
    	// 트레이명 txt 필드
    	if(processTxt != null){
    		if(!processTxt.isDisposed()){
    			processTxt.setText("");
    			return ; 
    		}
    	}
		processTxt = new Text(shell, SWT.LEFT|SWT.BORDER);  
		processTxt.setBounds(leftPadding, 155, inputSize, 20);
	}

	/**
     * 사용자 설정 목록
     * @param shell
     */
    private void selectUserInfoBoxProcess(final Shell shell) {
    	selectUserList = new List(shell, SWT.BORDER|SWT.V_SCROLL);
    	selectUserList.setBounds(5, 10, 190, 230);
    	
    	setUserInfoBoxData(); // 사용자 tray 정보 셋팅.
    	
    	selectUserList.addListener(SWT.MouseDown, new Listener () {
			public void handleEvent (Event  e) {
				selectDataSettring(shell);
			}
		});
    	
    	selectUserList.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_DOWN ||e.keyCode == SWT.ARROW_UP) {
					selectDataSettring(shell);
				}
				return ; 
			}
			
			public void keyPressed(KeyEvent e) {
				return ; 
			}
		});
	}
    
    protected void selectDataSettring(Shell shell) {
    	int sIdx = selectUserList.getSelectionIndex();
		if(sIdx >-1){
			DataEntity tmpEntity = TrayUserDataControl.newIntance().getItemList().get(sIdx);
			
			String type = tmpEntity.getString(TrayKeyConstants.ITEM_TYPE);
			
			nameTxt.setText(tmpEntity.getString(TrayKeyConstants.ITEM_NAME));
			
			String comboTxt = combo.getItem(combo.getSelectionIndex());
			
			if(!type.equals(comboTxt)){
				String [] items = combo.getItems();
				
				for (int i = 0; i < items.length; i++) {
					if(type.equals(items[i])){
						combo.select(i);
						break;
					}
				}
				comboSelectProcess(type, shell);
			}
			
			type=getComboType(type);
			
			if("web".equals(type)){
				processTxt.setText(tmpEntity.getString(TrayKeyConstants.ITEM_COMMAND));
			}else{
				selectProcessList.removeAll();
				selectProcessList.setItems(tmpEntity.split(TrayKeyConstants.ITEM_COMMAND,TrayConfigurationConstants.DELIMETER));
			}
		}
		
	}

	/**
     * 사용자 tray 정보 셋팅.
     */
    private void setUserInfoBoxData(){
    	TrayUserDataControl udc=TrayUserDataControl.newIntance();
    	java.util.List<DataEntity> menuList = udc.getItemList();
    	
    	//팝업 메뉴에 등록 
    	DataEntity tmpMenu = null;
    	int menuSize = menuList.size();
    	String [] userInfoArr = new String[menuSize];
    	for (int i = 0; i < menuSize; i++) {
    		tmpMenu = menuList.get(i);
    		userInfoArr[i]=tmpMenu.getString(TrayKeyConstants.ITEM_NAME);
		}
    	
    	selectUserList.setItems(userInfoArr);
    }
    
    /**
     * 콤보 처리 . 
     * @param shell
     */
    private void typeComboProcess(final Shell shell) {
    	//타입
    	
    	combo = UiUtil.createCombo(shell, typeArr, SWT.LEFT|SWT.READ_ONLY);
    	combo.setBounds(leftPadding,70,inputSize,20);
    	combo.select(0);
    	
    	combo.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    			String comboTxt = combo.getItem(combo.getSelectionIndex());
    			
    			if(selectComboTxtStr.equals(comboTxt))return ; 
    			//프로세스 라벨
    			comboSelectProcess(comboTxt, shell);
    			
    			selectComboTxtStr = comboTxt;
    		};
    	});
	}
    
    /**
     * 타입 체크. 
     * @param comboTxt
     * @return
     */
    private String getComboType(String comboTxt){
    	if(comboTxt.toLowerCase().indexOf("web") >-1){
			comboTxt="web";
		}
    	return comboTxt;
    }
    
    /**
     * 타입 선택했을때 액션
     * @param comboTxt
     * @param shell
     */
	protected void comboSelectProcess(String comboTxt, Shell shell) {
		comboTxt=getComboType(comboTxt);
		
		processLabel.setText("web".equals(comboTxt)?"URL :":"프로세스 : ");
		
		if("web".equals(comboTxt)){
			processTxtProcess(shell);
			if(fileOpenBtn != null){
				fileOpenBtn.dispose();
				selectProcessList.dispose();
				deleteStrLbl.dispose();
			}
		}else{
			if(processTxt != null){
				processTxt.dispose();
			}
			selectBoxProcess(shell);
			
	        // "파일 열기" 버튼 만들기  
			fileOpenBtn = fileOpenButton(shell,"실행파일찾기");  
			fileOpenBtn.setBounds(leftPadding+60, 135, 80, 20);
			
			deleteStrLbl = UiUtil.createLabel(shell, "※더블클릭시 삭제※ ", SWT.CENTER);
			deleteStrLbl.setBounds(leftPadding,195, inputSize, 20);
		}
	}

	/**
     * 버튼 처리. 
     * @param shell
     */
    private void insertCancleBtnProcess(final Shell shell) {
    	//left, top, with, height
    	addBtnProcess(shell); // 추가 버튼
    	
    	cancleBtnProcess(shell); // 취소 버튼
    	
    	delBtnProcess(shell); //삭제 버튼

    	inBtnProcess(shell); // 등록 버튼
    	
    	sortTopBtnProcess(shell); // 위 버튼
    	
    	sortBottomBtnProcess(shell); // 아래 버튼
	}
    
    /**
     * 위 버튼 처리
     * @param shell
     */
    private void sortTopBtnProcess(Shell shell) {
    	topBtn = UiUtil.createButton(shell,"위" ,SWT.PUSH);
    	topBtn.setBounds(leftPadding, 10, btnHalfSize, 20);
    	
    	int sIdx = selectUserList.getSelectionIndex();
    	
    	if(sIdx < 1) return ;
    	
    	topBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		selectUserList.deselectAll();
	    		inputDataClear("add");
	    	}
    	});
	}
    
    private void sortBottomBtnProcess(Shell shell) {
    	
    	bottomBtn = UiUtil.createButton(shell,"아래" ,SWT.PUSH);
    	bottomBtn.setBounds(leftPadding, 10, btnHalfSize, 20);
    	
    	int sIdx = selectUserList.getSelectionIndex();
    	
    	
    	//if(selectUserList < 1) return ;
    	
    	bottomBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		selectUserList.deselectAll();
	    		inputDataClear("add");
	    	}
    	});
    	
    	//Collections.swap(selectUserList,0,4);
		
	}

	

	/**
     * 등록 버튼 처리
     * @param shell
     */
	private void inBtnProcess(Shell shell) {
		inBtn = UiUtil.createButton(shell,"저  장" ,SWT.PUSH);
    	inBtn.setBounds(leftPadding+btnSize+5, 30, btnSize, 20);
    	
    	inBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
		    	String comboTxt = combo.getItem(combo.getSelectionIndex());
		    	String tmpProcess = "";
		    	DataEntity inInfo = new DataEntity();
		    	
		    	int sIdx = selectUserList.getSelectionIndex();
		    	
		    	if(sIdx >-1){
					inInfo = TrayUserDataControl.newIntance().getItemList().get(sIdx);
				}
		    	
		    	comboTxt=getComboType(comboTxt);
		    	
		    	if("web".equals(comboTxt)){
		    		tmpProcess=processTxt.getText();
				}else{
					String [] item = selectProcessList.getItems();
					StringBuilder procSb = new StringBuilder();
			    	for (int i = 0; i < item.length; i++) {
				    	procSb.append(i==0?"":TrayConfigurationConstants.DELIMETER);
				    	procSb.append(item[i]);
			    	}
			    	tmpProcess= procSb.toString();
				}
		    	
		    	if("".equals(nameTxt.getText())){
		    		JOptionPane.showMessageDialog(null, " 이름 입력 !");
		    		return ; 
		    	}
		    	
		    	if("".equals(tmpProcess)){
		    		JOptionPane.showMessageDialog(null, " 실행 프로그램 입력 !");
		    		return ; 
		    	}
		    	
		    	inInfo.put(TrayKeyConstants.ITEM_NAME, nameTxt.getText());
		    	inInfo.put(TrayKeyConstants.ITEM_TYPE, combo.getItem(combo.getSelectionIndex()));
		    	inInfo.put(TrayKeyConstants.ITEM_COMMAND, tmpProcess);
		    	
		    	try {
					if(sIdx >-1){
						TrayUserDataControl.newIntance().modify(inInfo);
					}else{
			    		TrayUserDataControl.newIntance().createItem(inInfo );
					}
				
					TrayUserDataControl.newIntance().store();
					setUserInfoBoxData();
					inputDataClear("insert");
				} catch (Exception e) {
					// TODO 자동 생성된 catch 블록
					e.printStackTrace();
				}
	    	}
    	});
	}
	
	/**
	 * 삭제 버튼 처리 
	 * @param shell
	 */
	private void delBtnProcess(final Shell shell) {
		delBtn = UiUtil.createButton(shell,"삭  제" ,SWT.PUSH);
    	delBtn.setBounds(leftPadding, 30, btnSize, 20);
    	
    	delBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	
		    	int sIdx= selectUserList.getSelectionIndex();
	    		
	    		if(sIdx> -1){
			    	DataEntity delEntity = TrayUserDataControl.newIntance().getItemList().get(sIdx);
		
			    	try {
			    		TrayUserDataControl.newIntance().deleteItem(delEntity.getString(TrayKeyConstants.ENTRY_ATTR_ID));
						TrayUserDataControl.newIntance().store();
						
						selectUserList.remove(sIdx);
						
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
	 * 취소 버튼
	 * @param shell
	 */
	private void cancleBtnProcess(final Shell shell) {
		cancleBtn = UiUtil.createButton(shell,"확  인" ,SWT.PUSH);
    	cancleBtn.setBounds(leftPadding+btnSize+5, 10, btnSize, 20);
    	
    	cancleBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		shell.close();
	    	}
    	});
	}
	
	/**
	 * 추가 버튼
	 * @param shell
	 */
	private void addBtnProcess(final Shell shell) {
		addBtn = UiUtil.createButton(shell,"추  가" ,SWT.PUSH);
		addBtn.setBounds(leftPadding, 10, btnSize, 20);
    	
    	addBtn.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		selectUserList.deselectAll();
	    		inputDataClear("add");
	    	}
    	});
	}
	
	/**
	 * 등록 데이터 필드 지우기. 
	 */
	private void inputDataClear(String type) {
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
     * @param shell
     */
    private void selectBoxProcess(Shell shell) {
    	selectProcessList = new List(shell, SWT.LEFT|SWT.BORDER|SWT.V_SCROLL);
		selectProcessList.setBounds(leftPadding, 155, inputSize, 40);
		
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
	}
    
	/** 
     * 파일 열기 버튼을 
     */  
    Button fileOpenButton(Shell shell, String nm) {  
        Button btn = UiUtil.createButton(shell,nm,SWT.PUSH);  
  
        btn.addSelectionListener(new SelectionAdapter() {  
            public void widgetSelected(SelectionEvent event) {  
            	 if (g_fileDialog == null) {
                     g_fileDialog = new FileDialog(selectProcessList.getShell(), SWT.OPEN);  
                 }  
                 g_fileDialog.setFilterExtensions(new String[] { "*.*" , "*.exe" });  
                 String filePath = g_fileDialog.open();  
                 
                 String fileNm = g_fileDialog.getFileName();
                 
                 if(null != filePath){
                	 selectProcessList.add(filePath);
                	 String tnt = nameTxt.getText();
                	 if("New Name".equals(tnt) || "".equals(tnt)){
                		 nameTxt.setText(fileNm);
                	 }
                 }
            }  
        });  
        return btn;  
    }  
    
	public static void main(String[] args) {
		
		Display display = new Display();  
		
		TrayInfoInsertForm tii = new TrayInfoInsertForm();  
  
        Shell shell = tii.open(display);
        
	    while (!shell.isDisposed()) { // 윈도우가 종료 할때 까지 기다린다.  
	    	if (!display.readAndDispatch()) // 윈도우에서 발생한 이벤트를 읽어 처리한다.  
	            display.sleep(); // 다른 프로그램이 CPU를 사용할 수 있도록 SLEEP 한다.  
	    }  
	    display.dispose();  
	}  
    
}
