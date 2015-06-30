package com.test.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ComboTest {

	public static void main(String[] a) {
	    Display d = new Display();
	    Shell s = new Shell(d);

	    s.setSize(250, 200);
	    s.setText("A KeyListener Example");
	    s.setLayout(new RowLayout());

	    final Combo c = new Combo(s,SWT.DROP_DOWN);
	    c.add("A");
	    c.add("B");
	    c.add("C");
	    c.add("D");
	    
	    

	    c.addKeyListener(new KeyListener() {
	      String selectedItem = "";

	      public void keyPressed(KeyEvent e) {
	        if (c.getText().length() > 0) {
	          return;
	        }
	        String key = Character.toString(e.character);
	        String[] items = c.getItems();
	        for (int i = 0; i < items.length; i++) {
	          if (items[i].toLowerCase().startsWith(key.toLowerCase())) {
	            c.select(i);
	            selectedItem = items[i];
	            
	            return;
	          }
	        }
	      }

	      public void keyReleased(KeyEvent e) {
	        if (selectedItem.length() > 0)
	          c.setText(selectedItem);
	        selectedItem = "";
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
