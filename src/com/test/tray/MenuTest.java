package com.test.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MenuTest {

	Display display;

	Shell shell;

	Menu menuBar, fileMenu, helpMenu;

	MenuItem fileMenuHeader, helpMenuHeader;

	MenuItem fileExitItem, fileSaveItem, helpGetHelpItem;

	Label label;

	public MenuTest() {

		display = new Display();
		shell = new Shell(display);
		shell.setText("Menu Example");
		shell.setSize(300, 200);

		label = new Label(shell, SWT.CENTER);
		label.setBounds(shell.getClientArea());

		menuBar = new Menu(shell, SWT.BAR);
		fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		fileMenuHeader.setText("&File");

		fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileMenuHeader.setMenu(fileMenu);

		fileSaveItem = new MenuItem(fileMenu, SWT.PUSH);
		fileSaveItem.setText("&Save");

		fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExitItem.setText("E&xit");

		helpMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		helpMenuHeader.setText("&Help");

		helpMenu = new Menu(shell, SWT.DROP_DOWN);
		helpMenuHeader.setMenu(helpMenu);

		helpGetHelpItem = new MenuItem(helpMenu, SWT.PUSH);
		helpGetHelpItem.setText("&Get Help");

		fileExitItem.addSelectionListener(new fileExitItemListener());
		fileSaveItem.addSelectionListener(new fileSaveItemListener());
		helpGetHelpItem.addSelectionListener(new helpGetHelpItemListener());

		shell.setMenuBar(menuBar);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	class fileExitItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			shell.close();
			display.dispose();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			shell.close();
			display.dispose();
		}
	}

	class fileSaveItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			label.setText("Saved");
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			label.setText("Saved");
		}
	}

	class helpGetHelpItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			label.setText("No worries!");
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			label.setText("No worries!");
		}
	}

	public static void main(String[] args) {
		MenuTest menuExample = new MenuTest();
	}
}
