package com.test.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.quick.tray.ui.UiUtil;

public class CompViewer {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		shell.setText("Show Group");
		shell.setSize(400,250);

		shell.setLayout(new FillLayout());

		ScrolledComposite sc = new ScrolledComposite(shell, SWT.H_SCROLL
				| SWT.V_SCROLL);
		Composite parent = new Composite(sc, SWT.NONE);
		sc.setContent(parent);
		parent.setLayout(new FormLayout());

		Composite group1 = new Composite(parent, SWT.LEFT|SWT.SHADOW_IN);

		group1.setLayout(new RowLayout(SWT.VERTICAL));
		
		group1.setSize(parent.getSize());

		new Button(group1, SWT.CHECK).setText("Tennis");
		new Button(group1, SWT.CHECK).setText("FootBall");
		new Button(group1, SWT.CHECK | SWT.LEFT).setText("Cricket");
		new Button(group1, SWT.CHECK).setText("Hockey");
		new Label(group1,SWT.NONE).setText("test");
		

		Composite group2 = new Composite(parent, SWT.SHADOW_IN|SWT.LEFT);
		group2.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		new Button(group2, SWT.RADIO).setText("Lucknow");
		new Button(group2, SWT.RADIO).setText("Agra");
		new Button(group2, SWT.RADIO).setText("Delhi");
		new Button(group2, SWT.RADIO).setText("Mumbai");
		

		FormData data = new FormData();
	    data.top = new FormAttachment(0, 10);
	    data.left = new FormAttachment(0, 10);
	    group1.setLayoutData(data);
	    
	    
	    data = new FormData();
	    data.top = new FormAttachment(0, 10);
	    data.left = new FormAttachment(0,200);
	    group2.setLayoutData(data);
		
	    sc.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}