package com.test.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class ScreenCapture {

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		Button button = new Button(shell, SWT.PUSH);
		button.setText("Capture");
		button.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				// 현재의 GC를 Image 객체에 복사한다.
				GC gc = new GC(display);
				final Image image = new Image(display, display.getBounds());
				gc.copyArea(image, 0, 0);
				gc.dispose();

				final Shell popup = new Shell(shell, SWT.SHELL_TRIM);
				popup.setLayout(new FillLayout());
				popup.setText("Image");
				popup.setBounds(50, 50, 200, 200);
				popup.addListener(SWT.Close, new Listener() {

					public void handleEvent(Event e) {
						FileDialog fileDialog = new FileDialog(popup, SWT.SAVE);
						String filename = fileDialog.open();
						if (filename == null) {
							return;
						}

						// Image 객체를 JPEG 파일로 저장한다.
						ImageData imageData = image.getImageData();

						ImageLoader imageLoader = new ImageLoader();
						imageLoader.data = new ImageData[] { imageData };
						imageLoader.save(filename, SWT.IMAGE_BMP);

						image.dispose();
					}

				});

				ScrolledComposite sc = new ScrolledComposite(popup,
						SWT.V_SCROLL | SWT.H_SCROLL);
				Canvas canvas = new Canvas(sc, SWT.NONE);
				sc.setContent(canvas);
				canvas.setBounds(display.getBounds());
				canvas.addPaintListener(new PaintListener() {

					public void paintControl(PaintEvent e) {
						// Image 객체를 Canvas에 그린다.
						e.gc.drawImage(image, 0, 0);
					}

				});
				popup.open();
			}

		});

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

}