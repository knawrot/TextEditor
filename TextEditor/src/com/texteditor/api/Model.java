package com.texteditor.api;

import java.io.File;

public interface Model {

	void saveFile();

	void saveFileAtLocation(File path);

	void loadAndAnalyzeFile(File file);

}
