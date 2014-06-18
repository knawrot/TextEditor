package com.texteditor.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import com.texteditor.api.View;

public class TextEditorFrame extends JFrame implements View {
	private static final long serialVersionUID = 1L;

	private final JTextPane mainTextPane = new JTextPane();
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu fileMenu = new JMenu(FILE_MENU_NAME);
	private final Map<String, JMenuItem> fileMenuItems = new HashMap<>();
	private final JTextArea linesIndicator = new JTextArea("1");
	private final Font font = new Font("Arial", Font.PLAIN, 15);

	/*
	 * TODO:
	 * 1. Co przy zamknieciu? Zapis pliku czy zlewamy? 
	 * Tak czy inaczej plik tworzymy DOPIERO przy zapisie (bo jak wychodzi i 
	 * zapisuje to taki plik ma nie powstac)
	 * 2. Opcja otwarcia nowego czystego pliku (opcja "New")
	 */
	public TextEditorFrame() {
		super();

		buildUpWindow();
		setUpCompsProperties();

		pack();
		setTitle("Text Editor");
		linesIndicator.setText("1");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setVisible(true);
	}

	private void buildUpWindow() {
		fileMenuItems.put(FILE_MENU_SAVE_NAME, new JMenuItem(
				FILE_MENU_SAVE_NAME));
		fileMenuItems.put(FILE_MENU_SAVE_AS_NAME, new JMenuItem(
				FILE_MENU_SAVE_AS_NAME));
		fileMenuItems.put(FILE_MENU_LOAD_NAME, new JMenuItem(
				FILE_MENU_LOAD_NAME));

		for (JMenuItem menuItem : fileMenuItems.values()) {
			fileMenu.add(menuItem);
		}
		menuBar.add(fileMenu);

		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		container.add(linesIndicator, BorderLayout.WEST);
		container.add(mainTextPane, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(container);
		//Dimension prefferedDimension = new Dimension(scrollPane.getPreferredSize().width-1000, scrollPane.getPreferredSize().height);
		//container.setPreferredSize(prefferedDimension);
		//scrollPane.add(mainTextPane);
		//scrollPane.add(linesIndicator);
		add(menuBar, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	private void setUpCompsProperties() {
		linesIndicator.setEditable(false);
		linesIndicator.setBackground(Color.GRAY);
		linesIndicator.setForeground(Color.LIGHT_GRAY);
		linesIndicator.setPreferredSize(new Dimension(30, linesIndicator
				.getPreferredSize().height));
		linesIndicator
				.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		linesIndicator.setMargin(new Insets(3, 0, 0, 5));

		Font font2 = new Font("Arial", Font.PLAIN, 16);
		linesIndicator.setFont(font2);
		mainTextPane.setFont(font);
	}

	@Override
	public void setWindowTitle(String title) {
		setTitle(title);
	}

	@Override
	public JTextPane getMainTextPane() {
		return mainTextPane;
	}

	@Override
	public JMenuBar getWindowMenuBar() {
		return menuBar;
	}

	@Override
	public JMenu getFileMenu() {
		return fileMenu;
	}

	@Override
	public Map<String, JMenuItem> getFileMenuItems() {
		return fileMenuItems;
	}

	@Override
	public JTextArea getLinesIndicator() {
		return linesIndicator;
	}

}
