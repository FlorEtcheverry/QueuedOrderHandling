package ordersmanagement;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.UUID;

import common.Archivo;
import common.ConfigLoader;
import common.Storage;

public class OrdersStorage extends Storage {
	
	public OrdersStorage() {
		super();
	}
	/*
	private class Archivo {
		public RandomAccessFile randomFile;
		public FileChannel channel;
	}
	
	private HashMap<String, Archivo> openedFiles;

	public OrdersStorage() {
		
		openedFiles = new HashMap<String, OrdersStorage.Archivo>();
	}
	
	private Archivo getArchivo(UUID id) throws IOException {
		
		ConfigLoader conf = ConfigLoader.getInstance();
		String pathStr = conf.getOrdersPath(id.toString().substring(0, 1));
		Archivo archivo = null;
		
		if (!openedFiles.containsKey(pathStr)) {
			File arch = new File(pathStr);
			arch.getParentFile().mkdirs();
			if (!arch.exists()) {
				arch.createNewFile();
			}

			archivo = new Archivo();
			archivo.randomFile = new RandomAccessFile(arch, "rw");
			archivo.channel = archivo.randomFile.getChannel();
			
			openedFiles.put(pathStr, archivo);
		} else {
			archivo = openedFiles.get(pathStr);
		}
		return archivo;		
	}
	
	public void close() throws IOException {
		for (Archivo archivo : openedFiles.values()) {
			archivo.channel.close();
			archivo.randomFile.close();
		}
	}*/
	
	private Archivo init(UUID id) throws IOException {
		ConfigLoader conf = ConfigLoader.getInstance();
		String pathStr = conf.getOrdersPath(id.toString().substring(0, 1));
		return getArchivo(pathStr);
	}

	public void saveNewOrder(UUID id,char estado) throws IOException {
				
		Archivo archivo = init(id);
		RandomAccessFile file = archivo.getRandomFile();
		FileLock lock = archivo.getChannel().lock();
		int lineLength = id.toString().length()+2;
		byte[] orden = new byte[lineLength];
		orden = (id.toString()+"|"+String.valueOf(estado)).getBytes();
		try {
			file.seek(file.length());
			file.write(orden);
		} finally {
			lock.release();
		}
	}
	
	public void changeOrderState(UUID id,char estado) throws IOException {
		
		Archivo archivo = init(id);
		RandomAccessFile file = archivo.getRandomFile();
		FileLock lock = archivo.getChannel().lock();
		int lineLength = id.toString().length()+2;
		byte[] orden = new byte[lineLength];
		boolean encontrado = false;
		file.seek(0);
		try {
			while ((file.getFilePointer() <= file.length()) && !encontrado) {
				int res = file.read(orden);
				if (res == -1) break;
				String linea = new String(orden);
				if (res == orden.length) {
					String[] leido = linea.split("\\|");
					String idStr = leido[0];
					if (id.toString().equals(idStr) && leido[1].equals(
									String.valueOf(ConfigLoader.RECIBIDA))) {
						file.seek(file.getFilePointer()-lineLength);
						String nuevo = idStr+"|"+String.valueOf(estado);
						file.write(nuevo.getBytes());
						encontrado = true;
					}
				}
			}
		} finally {
			lock.release();
		}
	}
	
	public char getOrderState(UUID id) throws IOException{
		
		Archivo archivo = init(id);
		RandomAccessFile file = archivo.getRandomFile();
		FileLock lock = archivo.getChannel().lock();
		int lineLength = id.toString().length()+2;
		byte[] orden = new byte[lineLength];
		file.seek(0);
		try {
			while ((file.getFilePointer() <= file.length())) {
				int res = file.read(orden);
				if (res == -1) break;
				String linea = new String(orden);
				if (res == orden.length) {
					String[] leido = linea.split("\\|");
					String idStr = leido[0];
					if (id.toString().equals(idStr)) {
						return leido[1].charAt(0);
					}
				}
			}
		} finally {
			lock.release();
		}
		return 'o';
	}
}
