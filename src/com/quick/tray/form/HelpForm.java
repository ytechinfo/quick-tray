package com.quick.tray.form;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.quick.tray.constants.UIConstants;
import com.quick.tray.entity.DataEntity;
import com.quick.tray.lang.ResourceControl;
import com.quick.tray.main.TrayMain;
import com.quick.tray.ui.UiUtil;
import com.quick.util.StringUtil;
import com.quick.util.TrayUIUtil;

public class HelpForm {
	Display g_display; 
	
	final int helpW = 400 , helpH = 220;
	
	StyledText text;
	
	private Vector<HyperLinkRange> ranges = new Vector<HyperLinkRange>();
	
	final int btnW = UIConstants.BTN_W, btnH = UIConstants.BTN_H;
	
	private Cursor handCursor, arrowCursor;
	
	final private DataEntity  resource = ResourceControl.getInstance().getResource();
	
	public HelpForm(Display g_display) {
		this.g_display = g_display;
	}

	public void open(Shell g_shell){
		
		final Shell helpDialog = new Shell(g_shell ,SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
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
			helpImg=new Image(g_display, TrayUIUtil.getTrayImage("helpQuickTray.png"));
			
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
		.append("git : ").append("https://github.com/ytechinfo").append("\n")	;
		
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
		
		helpDialog.setLocation (TrayUIUtil.getCenterPoint(g_display, helpDialog));
		
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
				HyperLinkRange r = ranges.get(i);
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
