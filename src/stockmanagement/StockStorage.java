package stockmanagement;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import common.Archivo;
import common.ConfigLoader;
import common.Storage;


public class StockStorage extends Storage {

	public StockStorage() {
		super();
	}
	
	private Archivo init(int tipo) throws IOException {
		ConfigLoader conf = ConfigLoader.getInstance();
		String pathStr = conf.getStockPath(String.valueOf(tipo));
		return getArchivo(pathStr);
	}

	public boolean restarStock(int tipo,int cant) throws IOException { //TODO sacar repetido!
		//se fija para ese TIPO, el stock en el archivo
		//si alcanza para su CANTIDAD
		//resta el stock de archivo y devuelve true. Sino, false.
		
		Archivo archivo = init(tipo);
		RandomAccessFile file = archivo.getRandomFile();
		FileLock lock = archivo.getChannel().lock();
		try {
			file.seek(0);
			if (!archivo.existed()) {
				file.writeInt(ConfigLoader.getInstance().getMaxStock());
				file.seek(0);
			}
			int leido = file.readInt();
			int nuevo = leido-cant;
			if (nuevo < 0) return false;
			file.seek(0);
			file.writeInt(nuevo);
			System.out.println("Nuevo stock de producto "+tipo+": "+nuevo);
			
		} finally {
			if (lock != null) lock.release();
		}
		return true;
	}

	public void sumarStock(int tipo, int cant) throws IOException {
		//suma el stock del archivo de ese TIPO y escribe el nuevo stock
		
		Archivo archivo = init(tipo);
		RandomAccessFile file = archivo.getRandomFile();
		FileLock lock = archivo.getChannel().lock();
		try {
			file.seek(0);
			if (!archivo.existed()) {
				file.writeInt(ConfigLoader.getInstance().getMaxStock());
				file.seek(0);
			}
			int leido = file.readInt();
			int nuevo = leido+cant;
			file.seek(0);
			file.writeInt(nuevo);
			System.out.println("Stock sumado para producto "+tipo+
											". Nuevo stock: "+nuevo);
		} finally {
			if (lock != null) lock.release();
		}
	}
}
