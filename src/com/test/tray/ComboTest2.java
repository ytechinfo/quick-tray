package com.test.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ComboTest2 {

  public static void main(String[] a) {
    Display d = new Display();
    Shell s = new Shell(d);

    s.setSize(250, 250);
    s.setText("A Combo Example");
    final Combo c1 = new Combo(s, SWT.READ_ONLY);
    c1.setBounds(50, 50, 150, 65);
    final Combo c2 = new Combo(s, SWT.READ_ONLY);
    c2.setBounds(50, 85, 150, 65);
    c2.setEnabled(false);
    String items[] = { "Item One", "Item Two", "Item Three", "Item Four", "Item Five" };
    c1.setItems(items);
    c1.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        if (c1.getText().equals("Item One")) {
          String newItems[] = { "A", "B", "C" };
          c2.setItems(newItems);
          c2.setEnabled(true);
        } else if (c1.getText().equals("Item Two")) {
          String newItems[] = { "A", "B", "C" };
          c2.setItems(newItems);
          c2.setEnabled(true);
        } else {
          c2.add("Not Applicable");
          c2.setText("Not Applicable");
        }
      }
    });
    s.open();
    while (!s.isDisposed()) {
      if (!d.readAndDispatch())
        d.sleep();
    }
    d.dispose();
  }
}