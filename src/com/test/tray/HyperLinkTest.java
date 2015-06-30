package com.test.tray;

import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;

public class HyperLinkTest {

	private Display display;
	private Shell shell;
	private Vector ranges = new Vector();
	private StyledText text;
	private Cursor handCursor;
	private Cursor arrowCursor;

	public static void main(String[] args) {
		new HyperLinkTest();
	}

	public HyperLinkTest() {
		display = new Display();
		shell = new Shell(display);
		shell.setText("HyperLinkTest");
		shell.setLayout(new FillLayout());

		text = new StyledText(shell, SWT.MULTI | SWT.BORDER);
		text.setText("이것은 하이퍼링크 테스트입니다 ");
		text.append("\nURL1: http://www.yahoo.co.jp/qmffjem09 ");
		text.append("URL2: http://cjasmin.fc2web.com");

		// init cursor
		handCursor = new Cursor(display, SWT.CURSOR_HAND);
		arrowCursor = new Cursor(display, SWT.CURSOR_ARROW);

		createHyperLink(text);
		text.addMouseListener(new HyperLinkMouseListener());
		text.addMouseMoveListener(new HyperLinkMouseMove());

		shell.setSize(300, 200);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		handCursor.dispose();
		arrowCursor.dispose();
		display.dispose();
	}

	private void createHyperLink(StyledText text) {
		String content = text.getText();
		Pattern pattern = Pattern.compile("http://[a-zA-Z./0-9%]*");
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			
			
			String line = matcher.group();
			
			
			
			int index = content.indexOf(line);
			HyperLinkRange range = new HyperLinkRange();
			
			System.out.println(line+" | "+index +" | "+line.length());
			
			range.start = index;
			range.length = line.length();
			range.fontStyle = SWT.BOLD;
			range.foreground = display.getSystemColor(SWT.COLOR_BLUE);
			range.keyword = line;
			ranges.add(range);

			text.setStyleRange(range);
		}

	}

	// カーソル上にハイパーリンクがあればHyperLinkRangeを返し、커서상에 하이퍼링크가 있으면 하이퍼링크레인지로 응답하고
	// それ以外はnullを返す。이외에는 널로 응답합니다.
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

	class HyperLinkMouseListener implements MouseListener{

		public void mouseDoubleClick(MouseEvent arg0) {
			return;
		}

		public void mouseDown(MouseEvent event) {
			int x = event.x;
			int y = event.y;
			HyperLinkRange range = getHyperLinkRange(x, y);
			if (range != null) {
				Runtime runtime = Runtime.getRuntime();
				String path = "C:/Program Files/Internet Explorer/IEXPLORE.EXE";
				String[] cmds = { path, range.keyword };
				try {
					runtime.exec(cmds);
				} catch (IOException ex) {
					ex.printStackTrace();
				}

			}
			return;
		}

		public void mouseUp(MouseEvent arg0) {
			return;
		}
		
	}
	
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


	public void mouseMove(MouseEvent event) {
		
	}

	class HyperLinkRange extends StyleRange {
		String keyword;
	}
}
