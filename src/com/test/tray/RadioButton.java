package com.test.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class RadioButton {

  public RadioButton() {
    Display display = new Display();
    Shell shell = new Shell(display);
    
    shell.setLayout(new RowLayout());
    
    Label label = new Label(shell, SWT.NULL);
    label.setText("Gender: ");
    label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
    
    Button femaleButton = new Button(shell, SWT.RADIO);
    femaleButton.setText("F");
    
    Button maleButton = new Button(shell, SWT.RADIO);
    maleButton.setText("M");
    
    label = new Label(shell, SWT.NULL);
    label.setText("  Title: ");
    label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
    
    Composite composite = new Composite(shell, SWT.NULL);
    composite.setLayout(new RowLayout());
    
    Button mrButton = new Button(composite, SWT.RADIO);
    mrButton.setText("Mr.");
    Button mrsButton = new Button(composite, SWT.RADIO);
    mrsButton.setText("Mrs.");
    Button msButton = new Button(composite, SWT.RADIO);
    msButton.setText("Ms.");
    Button drButton = new Button(composite, SWT.RADIO);
    drButton.setText("Dr.");  

    shell.pack();
    shell.open();
    //textUser.forceFocus();

    // Set up the event loop.
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        // If no more entries in event queue
        display.sleep();
      }
    }

    display.dispose();
  }

  private void init() {

  }

  public static void main(String[] args) {
    new RadioButton();
  }
}