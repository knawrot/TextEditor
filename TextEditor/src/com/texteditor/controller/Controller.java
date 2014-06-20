package com.texteditor.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.texteditor.api.Model;
import com.texteditor.api.View;

public class Controller {
	private final Model model;
	private final View view;

	private File openedFile;
	private boolean loadedFileParsed = true;
	private DocumentListener documentListener;

	public Controller(Model model, View view) {
		this.model = model;
		this.view = view;

		setUpGuiListeners();
	}

	public void startApp() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				view.showGUI();
			}
		});
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
			
			
			public void handleEditorUpdate (final String text, final int linesCount) {
				SwingWorker<Void, Void> backgroundWorker = new SwingWorker<Void, Void>() {
					
					@Override
					protected Void doInBackground() throws Exception {
						model.parseText(text);
						return null;
					}
					
					@Override
					protected void done() {
						SwingUtilities.invokeLater(new Runnable() {
							
							@Override
							public void run() {
								updateErrorPanel(linesCount);
								updateErrors(model.getErrorReport());
								updateFuntionsDefinitions();
							}
						});
					}
				};
				backgroundWorker.execute();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				if (!loadedFileParsed) {
					view.getMainTextPane().getDocument().putProperty(
							DefaultEditorKit.EndOfLineStringProperty, "\n");
					int linesCount = view.getMainTextPane().getDocument()
							.getDefaultRootElement().getElementCount();
					updateLinePanel(linesCount);
					handleEditorUpdate(view.getMainTextPane().getText(), linesCount);
					highlightKeywords();
					loadedFileParsed = true;
				}
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				view.getMainTextPane().getDocument().putProperty(
						DefaultEditorKit.EndOfLineStringProperty, "\n");
				int linesCount = view.getMainTextPane().getDocument()
						.getDefaultRootElement().getElementCount();
				updateLinePanel(linesCount);
				handleEditorUpdate(view.getMainTextPane().getText(), linesCount);
				highlightKeywords();
				
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				view.getMainTextPane().getDocument().putProperty(
						DefaultEditorKit.EndOfLineStringProperty, "\n");
				int linesCount = view.getMainTextPane().getDocument()
						.getDefaultRootElement().getElementCount();
				updateLinePanel(linesCount);
				handleEditorUpdate(view.getMainTextPane().getText(), linesCount);
				highlightKeywords();
			}

		};

		view.getMainTextPane().getDocument()
				.addDocumentListener(documentListener);
	}

	private void loadFile(File file) {
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
		loadedFileParsed = false;
		openedFile = file;
	}

	private void saveFile() {
		if (openedFile != null)
			saveFileAs(openedFile);
		else
			saveFileAs(model.createEmptyFile());
	}

	private void saveFileAs(File file) {
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new BufferedOutputStream(
					new FileOutputStream(file)));
			view.getMainTextPane().write(writer);
			view.setWindowTitle(file.getName());
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

	private void updateLinePanel(int linesCount) {
		int compCount = view.getLinesIndicator().getComponentCount();

		if (linesCount > compCount) {
			if (compCount == 0) 
				compCount++;	/* wskaznik 1 lini jest zawsze obecny */
			for (int i = compCount + 1; i <= linesCount; i++) {
				view.getLinesIndicator().add(view.buildLineIndicatorLabel(String.valueOf(i)));
			}
		}
		else {
			for (int i = compCount; i > linesCount; i--)
				view.getLinesIndicator().remove(i-1);
		}
		
		view.getLinesIndicator().revalidate();
		view.getLinesIndicator().repaint();
	}
	
	private void updateErrorPanel(int linesCount) {
		int compCount = view.getErrorsIndicator().getComponentCount();

		if (linesCount > compCount) {
			for (int i = compCount; i < linesCount; i++) {
				JLabel label = new JLabel("   ");
				label.setFont(View.EDITOR_FONT);
				view.getErrorsIndicator().add(label);
			}
		}
		else {
			for (int i = compCount; i > linesCount; i--)
				view.getErrorsIndicator().remove(i-1);
		}
	}
	
	private void updateFuntionsDefinitions() {
		List<String> list = model.getSymbolTable();
		StringBuilder builder = new StringBuilder();
		for (String string : list) {
			String functionName = string.substring(0, string.indexOf(':'));
			String functionRetValue = string.substring(string.lastIndexOf(':')+1, string.length());
			builder.append(functionName + "(): " + functionRetValue + "\n");
		}
		
		view.getFunctionsDefinition().setText(builder.toString());
		view.getFunctionsDefinition().revalidate();
		view.getFunctionsDefinition().repaint();
	}
	
	private void updateErrors(List<String> result){		
		Component[] labels = view.getErrorsIndicator().getComponents();
		for (int i = 0; i < view.getErrorsIndicator().getComponentCount(); i++) {
			((JLabel) labels[i]).setOpaque(false);
		}
			
		for (String string : result) {
			int colonIndex = string.indexOf(':');
			int line = Integer.parseInt(string.substring(0, colonIndex));
			String errorMsg = string.substring(colonIndex+1);
			
			JLabel errorIndicator = (JLabel) view.getErrorsIndicator().getComponent(line-1);
			errorIndicator.setOpaque(true);
			errorIndicator.setBackground(View.ERROR_INDICATING_COLOR);
			errorIndicator.setToolTipText(errorMsg);
		}
		
		view.getErrorsIndicator().revalidate();
		view.getErrorsIndicator().repaint();
	}
	
	private void applyTextModification(final int offset, final int length, Color c, boolean bold) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet tmp_aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
				StyleConstants.Foreground, c);
		final AttributeSet aset = sc.addAttribute(tmp_aset, StyleConstants.Bold, bold);
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				view.getMainTextPane().getStyledDocument().setCharacterAttributes(offset, length, 
						aset, true);
				
			}
		});
	}
	
	private void updateTextColors(int offset, int length, Color c) {
		applyTextModification(offset, length, c, true);
	}
	
	private void clearTextColors() {
		applyTextModification(0, view.getMainTextPane().getText().length(), 
				View.DEFAULT_TEXT_COLOR, false);
	}
	
	private void highlightKeywords() {		
		clearTextColors();
		Pattern pattern = Pattern.compile(model.getKeywordsRegExp());
		Matcher match = pattern.matcher(view.getMainTextPane().getText());
		while (match.find()) {
			updateTextColors(match.start(), match.end() - match.start(), View.KEYWORD_COLOR);
		}
	}
}
