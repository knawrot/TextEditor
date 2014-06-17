package com.texteditor.api;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Model {

	void saveFile();

	void saveFileAtLocation(File path);

	void loadAndAnalyzeFile(File file);
	
	List<String> getErrorReport(String text) throws IOException;

}
