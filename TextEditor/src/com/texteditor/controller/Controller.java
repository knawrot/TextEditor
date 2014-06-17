package com.texteditor.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

import com.texteditor.api.Model;
import com.texteditor.api.View;

public class Controller {
	private final Model model;
	private final View view;

	private File openedFile;
	private DocumentListener documentListener;

	public Controller(Model model, View view) {
		this.model = model;
		this.view = view;

		setUpGuiListeners();
	}

	private void setUpGuiListeners() {
		Map<String, JMenuItem> fileMenuItems = view.getFileMenuItems();
		fileMenuItems.get(View.FILE_MENU_LOAD_NAME).addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						JFileChooser fileChooser = new JFileChooser(".");
						int status = fileChooser.showOpenDialog(null);

						switch (status) {
						case JFileChooser.APPROVE_OPTION:
							loadFile(fileChooser.getSelectedFile());
							break;
						default:
							break;
						}

					}
				});
		fileMenuItems.get(View.FILE_MENU_SAVE_AS_NAME).addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						JFileChooser fileChooser = new JFileChooser(".");
						int status = fileChooser.showSaveDialog(null);

						switch (status) {
						case JFileChooser.APPROVE_OPTION:
							saveFileAs(fileChooser.getSelectedFile());
							break;
						default:
							break;
						}
					}
				});
		fileMenuItems.get(View.FILE_MENU_SAVE_NAME).addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						view.getMainTextPane().setEditable(false);
						saveFile();
						view.getMainTextPane().setEditable(true);
					}
				});

		documentListener = new DocumentListener() {
			public String getText() {
				int caretPosition = view.getMainTextPane().getDocument()
						.getLength();
				Element root = view.getMainTextPane().getDocument()
						.getDefaultRootElement();
				String text = "1" + System.getProperty("line.separator");
				for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
					text += i + System.getProperty("line.separator");
				}
				//System.out.println(text);
				return text;
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				view.getLinesIndicator().setText(getText());
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				view.getLinesIndicator().setText(getText());
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				view.getLinesIndicator().setText(getText());
			}

		};

		view.getMainTextPane().getDocument()
				.addDocumentListener(documentListener);
	}

	private void loadFile(File file) {
		model.loadAndAnalyzeFile(file);
		try {
			view.getMainTextPane().setPage(file.toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		view.getMainTextPane().getDocument()
				.addDocumentListener(documentListener);
		view.setWindowTitle(file.getName());
		openedFile = file;
	}

	private void saveFile() {
		// model.saveFile();
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new BufferedOutputStream(
					new FileOutputStream(openedFile)));
			view.getMainTextPane().write(writer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void saveFileAs(File file) {
		model.saveFileAtLocation(file);
	}

}
