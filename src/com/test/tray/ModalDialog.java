package com.test.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ModalDialog {
  static boolean deleteFlag = false;

  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setText("Dialog Example");
    shell.setSize(300, 200);
    shell.open();

    final Button button = new Button(shell, SWT.PUSH);
    button.setText("Delete File");
    button.setBounds(20, 40, 80, 25);

    final Text text = new Text(shell, SWT.SHADOW_IN);
    text.setBounds(140, 40, 100, 25);

    final Shell dialog = new Shell(shell, SWT.APPLICATION_MODAL
        | SWT.DIALOG_TRIM);
    dialog.setText("Delete File");
    dialog.setSize(250, 150);

    final Button buttonOK = new Button(dialog, SWT.PUSH);
    buttonOK.setText("OK");
    buttonOK.setBounds(20, 55, 80, 25);

    Button buttonCancel = new Button(dialog, SWT.PUSH);
    buttonCancel.setText("Cancel");
    buttonCancel.setBounds(120, 55, 80, 25);

    final Label label = new Label(dialog, SWT.NONE);
    label.setText("Delete the file?");
    label.setBounds(20, 15, 100, 20);

    Listener listener = new Listener() {
      public void handleEvent(Event event) {
        if (event.widget == buttonOK) {
          deleteFlag = true;
        } else {
          deleteFlag = false;
        }
        dialog.close();
      }
    };

    buttonOK.addListener(SWT.Selection, listener);
    buttonCancel.addListener(SWT.Selection, listener);

    Listener buttonListener = new Listener() {
      public void handleEvent(Event event) {
        dialog.open();
      }
    };

    button.addListener(SWT.Selection, buttonListener);

    while (!dialog.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }

    if (deleteFlag) {
      text.setText("File deleted.");
    } else {
      text.setText("File not deleted.");
    }

    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }
}
