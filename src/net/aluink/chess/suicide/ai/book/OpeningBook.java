package net.aluink.chess.suicide.ai.book;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class OpeningBook {
	
	Map<String,BookNode> book;
	
	public OpeningBook(File f){
//		load(f);
	}

	String readUntil(FileInputStream fis, char end) throws Exception{
		StringBuilder sb = new StringBuilder();
		char c;
		while((c = (char)fis.read()) != end){
			sb.append(c);
		}
		return sb.toString();
	}
	
//	void load(File f) {
//		try {
//			FileInputStream fis = new FileInputStream(f);
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}		
//	}
	
	void store(File f){
		try {
			FileOutputStream fos = new FileOutputStream(f);
			for(String fen : book.keySet()){
				fos.write(fen.getBytes());
				fos.write((int) ':');
				book.get(fen).store(fos);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
