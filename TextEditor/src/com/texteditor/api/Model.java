package com.texteditor.api;

import java.io.File;
import java.util.List;

public interface Model {
	String EMPTY_FILE_PREFIX = "new";
	
	File createEmptyFile();
	
	void parseText(String text);
	
	List<String> getErrorReport();
	
	List<String> getSymbolTable();
	
	String getKeywordsRegExp();

}
