package com.texteditor.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.texteditor.api.Model;
import com.texteditor.api.View;

public class ModelImpl implements Model {
	private List<String> symbolTable;
	private List<String> errorReport;
	private int fileNameAppender = 1;
	private String keywordsReg;
	
		public ModelImpl() {
		symbolTable = new ArrayList<String>();
		
		StringBuilder buff = new StringBuilder("");
		buff.append("(");
		for (String keyword : View.C_KEYWORDS) {
			buff.append("\\b").append(keyword).append("\\b").append("|");
		}
		buff.deleteCharAt(buff.length() - 1);
		buff.append(")");
		keywordsReg = buff.toString();
	}

	@Override
	public File createEmptyFile() {
		File file;
		do {
			file = new File(EMPTY_FILE_PREFIX + String.valueOf(fileNameAppender++));
		} while (file.exists());
		return file;
	}

	@Override
	public void parseText(String text) {
		symbolTable.clear();
		symbolTable.clear();
		
		try {
			Process proc = Runtime.getRuntime().exec("python -O ErrorCheck/main.py");
			OutputStream os = proc.getOutputStream();
			os.write(text.getBytes());
			os.close();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(proc.getInputStream()));
			errorReport = new ArrayList<String>();
			String tmp = null;
			String[] splitTmp = null;
			while((tmp = br.readLine()) != null) {
				if(tmp.startsWith("(linia ")) {
					splitTmp = tmp.split("\\): ");
					errorReport.add(Integer.parseInt(splitTmp[0].substring(7)) + ":" + splitTmp[1]);
				} else if(tmp.startsWith("Syntax error at line ")) {
					splitTmp = tmp.split(" at line ");
					splitTmp = splitTmp[1].split(", column [0-9]+: ");
					errorReport.add(Integer.parseInt(splitTmp[0]) + ":" + 
							"B³¹d sk³adniowy, niespodziewane wyst¹pienie symbolu '" + splitTmp[1].split("'")[1] + "'");
				} else if(tmp.equals("At end of input")) {
					errorReport.add("1:Niepoprawna sk³adnia programu");
				} else if(tmp.startsWith("symbolTable")) {
					splitTmp = tmp.split("': ");
					int i;
					for(i = 1; i < splitTmp.length - 1; i++) {
						symbolTable.add(splitTmp[i].split(",")[0]);
					}
					symbolTable.add(splitTmp[i].split("}")[0].split(",")[0]);
				} else if(tmp.startsWith("Illegal character")) {
					splitTmp = tmp.split(" in line ");
					errorReport.add(splitTmp[1] + ":" + splitTmp[0]);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> getErrorReport() {		
		return errorReport;
	}
	
	@Override
	public List<String> getSymbolTable() {
		return symbolTable;
	}
	
	@Override
	public String getKeywordsRegExp() {
		return keywordsReg;
	}

}
