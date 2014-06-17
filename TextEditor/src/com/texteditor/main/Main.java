package com.texteditor.main;

import com.texteditor.controller.Controller;
import com.texteditor.model.ModelImpl;
import com.texteditor.view.TextEditorFrame;

public class Main {

	public static void main(String[] args) {
		Controller controller = new Controller(new ModelImpl(), new TextEditorFrame());

	}

}
