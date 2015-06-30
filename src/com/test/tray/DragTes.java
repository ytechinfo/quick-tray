package com.test.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class DragTes {

  public static void main(String[] args) {

    Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    final Label label1 = new Label(shell, SWT.BORDER);
    label1.setText("TEXT");
    final Label label2 = new Label(shell, SWT.BORDER);
    setDragDrop(label1);
    setDragDrop(label2);
    shell.setSize(200, 200);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }

  public static void setDragDrop(final Label label) {

    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;

    final DragSource source = new DragSource(label, operations);
    source.setTransfer(types);
    source.addDragListener(new DragSourceListener() {
      public void dragStart(DragSourceEvent event) {
        event.doit = (label.getText().length() != 0);
      }

      public void dragSetData(DragSourceEvent event) {
        event.data = label.getText();
      }

      public void dragFinished(DragSourceEvent event) {
        if (event.detail == DND.DROP_MOVE)
          label.setText("");
      }
    });

    DropTarget target = new DropTarget(label, operations);
    target.setTransfer(types);
    target.addDropListener(new DropTargetAdapter() {
      public void drop(DropTargetEvent event) {
        if (event.data == null) {
          event.detail = DND.DROP_NONE;
          return;
        }
        label.setText((String) event.data);
      }
    });
  }
}