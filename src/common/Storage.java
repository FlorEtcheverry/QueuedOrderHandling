package common;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class Storage {
	
	private HashMap<String, Archivo> openedFiles;

	public Storage() {
		
		openedFiles = new HashMap<String, Archivo>();
	}
	
	public Archivo getArchivo(String pathStr) throws IOException {
		
		Archivo archivo = null;
		
		if (!openedFiles.containsKey(pathStr)) {
			archivo = new Archivo();
			File arch = new File(pathStr);
			arch.getParentFile().mkdirs();
			if (!arch.exists()) {
				arch.createNewFile();
				archivo.setExisted(false);
			} else {
				archivo.setExisted(true);
			}
			archivo.setRandomFile(new RandomAccessFile(arch, "rw"));
			archivo.setChannel(archivo.getRandomFile().getChannel());
			openedFiles.put(pathStr, archivo);
		} else {
			archivo = openedFiles.get(pathStr);
		}
		return archivo;		
	}
	
	public void close() throws IOException {
		for (Archivo archivo : openedFiles.values()) {
			archivo.getChannel().close();
			archivo.getRandomFile().close();
		}
		System.out.println("Archivos cerrados correctamente.");
	}
	
}
