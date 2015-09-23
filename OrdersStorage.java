import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.UUID;

public class OrdersStorage {

	public void saveNewOrder(UUID id,char estado) throws IOException {
		
		String pathStr = id.toString().substring(0, 1);
		File arch = new File(pathStr);
		if (!arch.exists()) {
			arch.createNewFile();
		}
		
		RandomAccessFile file = null;
		FileChannel channel = null;
		FileLock lock = null;
				
		try {	
			file = new RandomAccessFile(arch, "rw");
			channel = file.getChannel();
			lock = channel.lock();
			
			int lineLength = id.toString().length()+1;
			byte[] orden = new byte[lineLength];
			orden = (id.toString()+"|"+String.valueOf(estado)).getBytes();
			file.seek(file.length());
			file.write(orden);
			
		} finally {
			lock.release();
			channel.close();
			file.close();
		}
	}
	
	public void changeOrderState(UUID id,char estado) throws IOException {
		
		String pathStr = id.toString().substring(0, 1);
		File arch = new File(pathStr);
		if (!arch.exists()) {
			arch.createNewFile();
		}
		
		RandomAccessFile file = new RandomAccessFile(arch, "rw");
		FileChannel channel = file.getChannel();
		FileLock lock = channel.lock();
		
		int lineLength = id.toString().length()+1;
		byte[] orden = new byte[lineLength];
		boolean encontrado = false;
		
		try {
			while (file.getFilePointer() <= file.length() && !encontrado) {
				int res = file.read(orden);
				if (res == orden.length) {
					String[] leido = orden.toString().split("|");
					String idStr = leido[0];
					if (id.toString().equals(idStr) && leido[1].equals(String.valueOf(ConfigLoader.RECIBIDA))) {
						file.seek(file.getFilePointer()-lineLength);
						String nuevo = idStr+"|"+String.valueOf(estado);
						file.write(nuevo.getBytes());
						encontrado = true;
					}
				}
			}
		} catch (EOFException e) {
			//TODO
		}
		finally {
			lock.release();
			file.close();
			channel.close();
		}
	}
	
	public char getOrderState(UUID id) throws IOException{
		
		String pathStr = id.toString().substring(0, 1);
		File arch = new File(pathStr);
		if (!arch.exists()) {
			arch.createNewFile();
		}
		RandomAccessFile file = new RandomAccessFile(arch, "rw");
		FileChannel channel = file.getChannel();
		FileLock lock = channel.lock();
		
		int lineLength = id.toString().length()+1;
		byte[] orden = new byte[lineLength];
		
		try {
			while (file.getFilePointer() <= file.length()) {
				int res = file.read(orden);
				if (res == orden.length) {
					String[] leido = orden.toString().split("|");
					String idStr = leido[0];
					if (id.toString().equals(idStr)) {
						return leido[1].charAt(0);
					}
				}
			}
		} catch (EOFException e) {
			//TODO
		} finally {
			lock.release();
			file.close();
			channel.close();
		}
		return 'o';
	}
}
