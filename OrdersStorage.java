import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.UUID;

public class OrdersStorage {

	public void saveNewOrder(UUID id,char estado) throws IOException {
		//TODO
		
		String pathStr = id.toString().substring(0, 1);
		File arch = new File(pathStr);
		System.out.println("nombre del archivo: "+pathStr);
		if (!arch.exists()) {
			arch.createNewFile();
		}
		/*
		Path path = Paths.get(pathStr);
		FileChannel fileCh = FileChannel.open(path, StandardOpenOption.WRITE);
		FileLock lock = fileCh.lock();
		
		BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.WRITE);	
		bw.write(id.toString()+" "+estado);
		bw.newLine();
		bw.flush();
		bw.close();
		
		lock.release();
		fileCh.close();
		*/
		
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
		/*
		 * File arch = new File(fileNameURLs);
			if (!arch.exists()) {
				arch.createNewFile();
			}
			BufferedReader br = new BufferedReader(new FileReader(arch));
			String line;
			boolean found = false;
			while (((line = br.readLine()) != null) && !found) {
				if (url.equals(line)) {
					found = true;
				}
			}
			
			//si no lo fue, lo agrega y devuelve false
			if (!found) {
				BufferedWriter bw = 
					new BufferedWriter(new FileWriter(arch,true));
				bw.write(url);
				bw.newLine();
				bw.close();
				res = false;
			}
			br.close();
		 */
		
		/*if (!file.exists()) {
			file.createNewFile();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		writer.write(msg);
		writer.newLine();
		writer.flush();
		writer.close();
		*/
	}
	
	public synchronized void changeOrderState(UUID id,char estado) throws IOException {
		//TODO
		
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
		/*
		Path path = Paths.get(pathStr);
		FileChannel fileCh = FileChannel.open(path, StandardOpenOption.READ);
		FileLock lock = fileCh.lock();
		
		BufferedWriter bw = Files.newBufferedWriter(path);
		
		List<String> lines = Files.readAllLines(path,Charset.defaultCharset());
		
		for (String line : lines) {
			String idStr = line.split(" ")[0];
			if (id.toString().equals(idStr)) {
				bw.
			}
		}
		-------------------------------
		String line;
		boolean found = false;
		while (((line = bw.readLine()) != null) && !found) {
			String idStr = line.split(" ")[0];
			if (id.toString().equals(idStr)) {
				found = true;
			}
		}------------------------------
		bw.write(id.toString()+" "+estado);
		bw.newLine();
		bw.flush();
		bw.close();
		*/
		finally {
			lock.release();
			file.close();
			channel.close();
		}
	}
	
	public synchronized char getOrderState(UUID id){
		//TODO
		return 'a';
	}
}
