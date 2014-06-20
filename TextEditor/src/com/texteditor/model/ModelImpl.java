package com.texteditor.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.texteditor.api.Model;

public class ModelImpl implements Model {
	//private File openedFile;

	// private Map<String, Color> specificElementColor = new HashMap<>();
	private List<String> symbolTable;

	public ModelImpl() {
		symbolTable = new ArrayList<String>();
	}

	@Override
	public void loadAndAnalyzeFile(File file) {
		// TODO: odapalmy sekwencje analizy, czyli jakies "exec"'i z modelu na
		// bisonach,
		// parserach etc
		// System.out.println(fileName);
		// openedFile = file;
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

	@Override
	public List<String> getErrorReport(String text) {
		List<String> errorReport = null;
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
							"B��d sk�adniowy, niespodziewane wyst�pienie symbolu '" + splitTmp[1].split("'")[1] + "'");
				} else if(tmp.equals("At end of input")) {
					errorReport.add("1:Niepoprawna sk�adnia programu");
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
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return errorReport;
	}

}
