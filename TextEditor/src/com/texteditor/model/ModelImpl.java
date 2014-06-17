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

	@Override
	public List<String> getErrorReport(String text) throws IOException {
		Process proc = Runtime.getRuntime().exec("python -O ErrorCheck/main.py");
		OutputStream os = proc.getOutputStream();
		os.write(text.getBytes());
		os.close();
        BufferedReader br = new BufferedReader(
        		new InputStreamReader(proc.getInputStream()));
        List<String> errorReport = new ArrayList<String>();
        String tmp = null;
        while((tmp = br.readLine()) != null) {
        	if(tmp.startsWith("(linia ")) {
        		String[] splitTmp = tmp.split("\\): ");
        		errorReport.add(Integer.parseInt(splitTmp[0].substring(7)) + ":" + splitTmp[1]);
        	}
        }
        return errorReport;
	}
	
	

}
