package common;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class Archivo {

	private RandomAccessFile randomFile;
	private FileChannel channel;
	private boolean existed;
	
	public Archivo() {
		this.randomFile = null;
		this.channel = null;
		this.existed = true;
	}
	public RandomAccessFile getRandomFile() {
		return randomFile;
	}
	public void setRandomFile(RandomAccessFile randomFile) {
		this.randomFile = randomFile;
	}
	public FileChannel getChannel() {
		return channel;
	}
	public void setChannel(FileChannel channel) {
		this.channel = channel;
	}
	public void setExisted(boolean existed) {
		this.existed = existed;
	}
	public boolean existed() {
		return existed;
	}
	

}
