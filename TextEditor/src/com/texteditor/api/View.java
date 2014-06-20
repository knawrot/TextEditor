package com.texteditor.api;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public interface View {
	String[] C_KEYWORDS = {
			"unsigned", "void", "float",
			"short",	"volatile",
			"char",	"signed", "enum",
			"const",	"_Bool",
			"_Complex", "struct",
			"_Imaginary", "int", "typedef",
			"double", "long", "union",		
			"break", "return", "case",
			"for", "while", "goto", "sizeof",	
			"continue",	"if", "default",
			"inline", "do", "switch",		
			"else", "register",
			"auto",	"restrict",
			"extern", "register"
	};
	Color KEYWORD_COLOR = Color.BLUE;
	Font KEYWORD_FONT = new Font("Verdana", Font.BOLD, 15);
	Font EDITOR_FONT = new Font("Arial", Font.PLAIN, 15);
	Color DEFAULT_TEXT_COLOR = Color.BLACK;
	Color ERROR_INDICATING_COLOR = Color.RED;
	Color LINE_INDICATORS_COLOR = Color.LIGHT_GRAY;
	Color FUNTION_DEFINITIONS_PANEL_COLOR = Color.WHITE;
	Color LINE_INDICATORS_PANEL_COLOR  = Color.GRAY;
	
	String FILE_MENU_NAME = "File";
	String FILE_MENU_SAVE_AS_NAME = "Save as";
	String FILE_MENU_SAVE_NAME = "Save";
	String FILE_MENU_LOAD_NAME = "Load";

	void showGUI();
	
	JLabel buildLineIndicatorLabel(String number);
	
	void setWindowTitle(String title);

	JTextPane getMainTextPane();

	JMenuBar getWindowMenuBar();

	JMenu getFileMenu();

	Map<String, JMenuItem> getFileMenuItems();

	JPanel getLinesIndicator();
	
	JPanel getErrorsIndicator();
	
	JTextArea getFunctionsDefinition();
}
