package com.texteditor.api;

import java.io.File;
import java.util.List;

public interface Model {

	void saveFile();

	void saveFileAtLocation(File path);

	void loadAndAnalyzeFile(File file);
	
	List<String> getErrorReport(String text);

}
