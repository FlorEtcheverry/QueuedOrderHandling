import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;


public class StockStorage {

	public boolean restarStock(int tipo,int cant) throws IOException {
		//se fija para ese TIPO, el stock en el archivo
		//si alcanza para su CANTIDAD
		//resta el stock de archivo y devuelve true. Sino, false.
		
		String pathStr = String.valueOf(tipo)+".stock";
		File arch = new File(pathStr);
		boolean existed = true;
		if (!arch.exists()) {
			existed = false;
		}
	
		RandomAccessFile file = null;
		FileChannel channel = null;
		FileLock lock = null;
				
		try {
			file = new RandomAccessFile(arch, "rw");
			channel = file.getChannel();
			lock = channel.lock();
			
			if (!existed) {
				file.seek(0);
				file.writeInt(ConfigLoader.getInstance().getMaxStock());
				file.seek(0);
			}
			
			int leido = file.readInt();
			int nuevo = leido-cant;
			if (nuevo < 0) return false;
			file.seek(0);
			file.writeInt(nuevo);
			
		} finally {
			if (lock != null) lock.release();
			if (channel != null) channel.close();
			if (file != null) file.close();
		}
		return true;
	}

	public void sumarStock(int tipo, int cant) throws IOException {
		//suma el stock del archivo de ese TIPO y escribe el nuevo stock
		
		String pathStr = String.valueOf(tipo)+".stock";
		File arch = new File(pathStr);
		boolean existed = true;
		if (!arch.exists()) {
			existed = false;
		}
	
		RandomAccessFile file = null;
		FileChannel channel = null;
		FileLock lock = null;
				
		try {
			file = new RandomAccessFile(arch, "rw");
			channel = file.getChannel();
			lock = channel.lock();
			
			if (!existed) {
				file.seek(0);
				file.writeInt(ConfigLoader.getInstance().getMaxStock());
				file.seek(0);
			}
			
			int leido = file.readInt();
			int nuevo = leido+cant;
			file.seek(0);
			file.writeInt(nuevo);
			System.out.println("Stock sumado. Nuevo stock: "+nuevo);
			
		} finally {
			if (lock != null) lock.release();
			if (channel != null) channel.close();
			if (file != null) file.close();
		}
	}
}
