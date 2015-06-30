package com.test.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MenuSubmenu {
  Display d;

  Shell s;

  MenuSubmenu() {
    d = new Display();
    s = new Shell(d);
    s.setSize(500, 500);
    s.setText("A Shell Menu Example");
    final Menu m = new Menu(s, SWT.BAR);

    // create a file menu and add an exit item
    final MenuItem file = new MenuItem(m, SWT.CASCADE);
    file.setText("&File");
    final Menu filemenu = new Menu(s, SWT.DROP_DOWN);
    file.setMenu(filemenu);
    final MenuItem openItem = new MenuItem(filemenu, SWT.CASCADE);
    openItem.setText("&Open");
    final Menu submenu = new Menu(s, SWT.DROP_DOWN);
    openItem.setMenu(submenu);
    final MenuItem childItem = new MenuItem(submenu, SWT.PUSH);
    childItem.setText("&Child\tCTRL+C");
    childItem.setAccelerator(SWT.CTRL + 'C');
    final MenuItem dialogItem = new MenuItem(submenu, SWT.PUSH);
    dialogItem.setText("&Dialog\tCTRL+D");
    dialogItem.setAccelerator(SWT.CTRL + 'D');
    final MenuItem separator = new MenuItem(filemenu, SWT.SEPARATOR);
    final MenuItem exitItem = new MenuItem(filemenu, SWT.PUSH);
    exitItem.setText("E&xit");

    // create an edit menu and add cut copy and paste items
    final MenuItem edit = new MenuItem(m, SWT.CASCADE);
    edit.setText("&Edit");
    final Menu editmenu = new Menu(s, SWT.DROP_DOWN);
    edit.setMenu(editmenu);
    final MenuItem cutItem = new MenuItem(editmenu, SWT.PUSH);
    cutItem.setText("&Cut");
    final MenuItem copyItem = new MenuItem(editmenu, SWT.PUSH);
    copyItem.setText("Co&py");
    final MenuItem pasteItem = new MenuItem(editmenu, SWT.PUSH);
    pasteItem.setText("&Paste");

    //create an options menu and add menu items
    final MenuItem options = new MenuItem(m, SWT.CASCADE);
    options.setText("&Options");
    final Menu optionsmenu = new Menu(s, SWT.DROP_DOWN);
    options.setMenu(optionsmenu);
    final MenuItem checkItem = new MenuItem(optionsmenu, SWT.CHECK);
    checkItem.setText("&Checked Option");
    final MenuItem optionsseparator = new MenuItem(optionsmenu,
        SWT.SEPARATOR);
    final MenuItem radioItem1 = new MenuItem(optionsmenu, SWT.RADIO);
    radioItem1.setText("Radio &One");
    final MenuItem radioItem2 = new MenuItem(optionsmenu, SWT.RADIO);
    radioItem2.setText("Radio &Two");

    //create a Window menu and add Child item
    final MenuItem window = new MenuItem(m, SWT.CASCADE);
    window.setText("&Window");
    final Menu windowmenu = new Menu(s, SWT.DROP_DOWN);
    window.setMenu(windowmenu);
    final MenuItem maxItem = new MenuItem(windowmenu, SWT.PUSH);
    maxItem.setText("Ma&ximize");
    final MenuItem minItem = new MenuItem(windowmenu, SWT.PUSH);
    minItem.setText("Mi&nimize");

    // create a Help menu and add an about item
    final MenuItem help = new MenuItem(m, SWT.CASCADE);
    help.setText("&Help");
    final Menu helpmenu = new Menu(s, SWT.DROP_DOWN);
    help.setMenu(helpmenu);
    final MenuItem aboutItem = new MenuItem(helpmenu, SWT.PUSH);
    aboutItem.setText("&About");

    // add action listeners for the menu items
    // this code is the same as seen previously so it is
    // omitted here

    s.setMenuBar(m);
    s.open();
    while (!s.isDisposed()) {
      if (!d.readAndDispatch())
        d.sleep();
    }
    d.dispose();
  }

  public static void main(String[] argv) {
    new MenuSubmenu();
  }

}