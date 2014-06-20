package com.texteditor.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.texteditor.api.View;

public class TextEditorFrame extends JFrame implements View {
	private static final long serialVersionUID = 1L;

	private final JTextPane mainTextPane = new JTextPane();
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu fileMenu = new JMenu(FILE_MENU_NAME);
	private final Map<String, JMenuItem> fileMenuItems = new HashMap<>();
	private final JPanel linesIndicator = new JPanel();
	private final JPanel errorsIndicator = new JPanel();
	private final JTextArea functionsDefinition = new JTextArea();


	public TextEditorFrame() {
		super();

		buildUpWindow();
		setUpCompsProperties();		
	}

	private void buildUpWindow() {
		/* budowanie menu */
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

		/* budowanie komponentu edytora */
		JPanel textEditionContainer = new JPanel();
		JPanel innerContainer = new JPanel();
		textEditionContainer.setLayout(new BorderLayout());
		innerContainer.setLayout(new BorderLayout());
		innerContainer.add(linesIndicator, BorderLayout.CENTER);
		innerContainer.add(errorsIndicator, BorderLayout.EAST);
		textEditionContainer.add(innerContainer, BorderLayout.WEST);
		textEditionContainer.add(mainTextPane, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(textEditionContainer);
		
		/* budowanie panelu bocznego */
		JPanel sideContainer = new JPanel();
		JLabel functionsHeader = new JLabel("Functions declared:");
		JScrollPane scrolFuntionsArea = new JScrollPane(functionsDefinition);
		sideContainer.setLayout(new BoxLayout(sideContainer, BoxLayout.Y_AXIS));
		sideContainer.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 10));
		functionsHeader.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
		scrolFuntionsArea.setPreferredSize(functionsHeader.getPreferredSize());
		sideContainer.add(functionsHeader);
		sideContainer.add(scrolFuntionsArea);
		
		/* zlozenie calego okna */
		add(menuBar, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(sideContainer, BorderLayout.EAST);
	}

	private void setUpCompsProperties() {
		linesIndicator.setBackground(LINE_INDICATORS_PANEL_COLOR);
		linesIndicator.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		linesIndicator.setLayout(new BoxLayout(linesIndicator, BoxLayout.Y_AXIS));
		Border border = BorderFactory.createEmptyBorder(5, 0, 0, 0);
		linesIndicator.setBorder(border);
		
		errorsIndicator.setLayout(new BoxLayout(errorsIndicator, BoxLayout.Y_AXIS));
		errorsIndicator.setBorder(border);

		mainTextPane.setFont(EDITOR_FONT);
		
		functionsDefinition.setEditable(false);
		functionsDefinition.setBackground(FUNTION_DEFINITIONS_PANEL_COLOR);
		functionsDefinition.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));;
	}

	@Override
	public void showGUI() {
		setTitle("Text Editor");
		linesIndicator.add(buildLineIndicatorLabel("1"));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setSize(800, 600);
		setVisible(true);
	}
	
	@Override
	public JLabel buildLineIndicatorLabel(String number) {
		JLabel lineLabel = new JLabel(number);
		lineLabel.setFont(EDITOR_FONT);
		lineLabel.setForeground(LINE_INDICATORS_COLOR);
		lineLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		lineLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		return lineLabel;
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
	public JPanel getLinesIndicator() {
		return linesIndicator;
	}
	
	public JPanel getErrorsIndicator() {
		return errorsIndicator;
	}
	
	public JTextArea getFunctionsDefinition() {
		return functionsDefinition;
	}

}
