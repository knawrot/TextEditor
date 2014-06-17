package com.texteditor.api;

import java.awt.Color;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public interface View {
	// jakie tymczasowe robocze kolorki
	Color VARIABLE_TYPE = new Color(0, 0, 0);
	Color LOOPS = Color.BLUE;
	
	String FILE_MENU_NAME = "File";
	String FILE_MENU_SAVE_AS_NAME = "Save as";
	String FILE_MENU_SAVE_NAME = "Save";
	String FILE_MENU_LOAD_NAME = "Load";

	void setWindowTitle(String title);

	JTextPane getMainTextPane();

	JMenuBar getWindowMenuBar();

	JMenu getFileMenu();

	Map<String, JMenuItem> getFileMenuItems();

	JTextArea getLinesIndicator();
}
