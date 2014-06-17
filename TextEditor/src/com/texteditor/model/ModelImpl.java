package com.texteditor.model;

import java.io.File;

import com.texteditor.api.Model;

public class ModelImpl implements Model {
	private File openedFile;

	// private Map<String, Color> specificElementColor = new HashMap<>();

	public ModelImpl() {
		// TODO inicjalizacja
	}

	@Override
	public void loadAndAnalyzeFile(File file) {
		// TODO: odapalmy sekwencje analizy, czyli jakies "exec"'i z modelu na
		// bisonach,
		// parserach etc
		// System.out.println(fileName);
		openedFile = file;
	}

	@Override
	public void saveFile() {
		// FileWriter writer = new FileWriter(openedFile);
		// writer.write(arg0);

	}

	@Override
	public void saveFileAtLocation(File path) {
		// TODO Auto-generated method stub

	}

}
