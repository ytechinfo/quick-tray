package com.test.tray;

import org.eclipse.swt.SWT; 
import org.eclipse.swt.events.SelectionEvent; 
import org.eclipse.swt.events.SelectionListener; 
import org.eclipse.swt.widgets.Button; 
import org.eclipse.swt.widgets.Display; 
import org.eclipse.swt.widgets.Event; 
import org.eclipse.swt.widgets.Label; 
import org.eclipse.swt.widgets.Listener; 
import org.eclipse.swt.widgets.Shell; 

public class SingleDisplayMultipleShell
{ 
  // ======================================== 
  // Inner class to represent a child Shell 
  // ======================================== 
  private class ChildShell 
  { 
     public ChildShell(Display display) 
     { 
        System.out.println("Creating new child Shell"); 
        
        // ========================================= 
        // Create a Shell (window) from the Display 
        // ========================================= 
        final Shell shell = new Shell(display, SWT.CLOSE); 

        // ===================== 
        // Set the Window Title 
        // ===================== 
        shell.setText("Child Shell"); 

        // ============================ 
        // Create a Label in the Shell 
        // ============================ 
        Label label = new Label(shell, SWT.NONE); 
        label.setText("Child"); 
        label.setBounds(0, 0, 100, 20); 
        shell.setSize(200, 200); 
        shell.open(); 

        // ============================================================= 
        // Register a listener for the Close event on the child Shell. 
        // This disposes the child Shell 
        // ============================================================= 
        shell.addListener(SWT.Close, new Listener() 
        { 
           public void handleEvent(Event event) 
           { 
              System.out.println("Child Shell handling Close event, about to dispose this Shell"); 
              shell.dispose(); 
           } 
        }); 
     } 
  } 

  public SingleDisplayMultipleShell() 
  { 
     // ====================================================== 
     // Create the main Display object that represents the UI 
     // subsystem and contains the single UI handling thread 
     // ====================================================== 
     final Display display = Display.getDefault(); 

     // ==================================================== 
     // create a shell for the main window from the Display 
     // ==================================================== 
     final Shell mainWindowShell = new Shell(display, SWT.CLOSE); 

     // ===================== 
     // Set the Window Title 
     // ===================== 
     mainWindowShell.setText("Main Shell"); 

     // ========================================= 
     // Create a button that spawns child Shells 
     // ========================================= 
     Button spawn = new Button(mainWindowShell, SWT.PUSH); 
     spawn.setText("Create Child"); 
     spawn.setBounds(10, 10, 150, 30); 
     spawn.addSelectionListener(new SelectionListener() 
     { 
        public void widgetSelected(SelectionEvent e) 
        { 
           // ===================================================== 
           // on button press, create a child Shell object passing 
           // the main Display. The child could also access the 
           // display itself by calling Display.getDefault() 
           // ===================================================== 
           System.out.println("Main Shell handling Button press, about to create child Shell"); 
           new ChildShell(display); 
        } 

        public void widgetDefaultSelected(SelectionEvent e) 
        { 
           widgetSelected(e); 
        } 
     }); 

     // ============================================================= 
     // Register a listener for the Close event on the main Shell. 
     // This disposes the Display which will cause the entire child 
     // tree to dispose 
     // ============================================================= 
     mainWindowShell.addListener(SWT.Close, new Listener() 
     { 
        public void handleEvent(Event event) 
        { 
           System.out.println("Main Shell handling Close event, about to dipose the main Display"); 
           display.dispose(); 
        } 
     }); 

     // ================================ 
     // Set size on main Shell and open 
     // ================================ 
     mainWindowShell.setSize(200, 200); 
     mainWindowShell.open(); 

     // ===================================== 
     // Main UI event dispatch loop 
     // that handles all UI events from all 
     // SWT components created as children of 
     // the main Display object 
     // ===================================== 
     while (!display.isDisposed()) 
     { 
        // =================================================== 
        // Wrap each event dispatch in an exception handler 
        // so that if any event causes an exception it does 
        // not break the main UI loop 
        // =================================================== 
        try 
        { 
           if (!display.readAndDispatch()) 
           { 
              display.sleep(); 
           } 
        } 
        catch (Exception e) 
        { 
           e.printStackTrace(); 
        } 
     } 

     System.out.println("Main Display event handler loop has exited"); 
  } 

  public static void main(String[] args) 
  { 
     new SingleDisplayMultipleShell(); 
  } 
} 
