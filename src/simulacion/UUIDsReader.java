package simulacion;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import common.ConfigLoader;


public class UUIDsReader {

	public ArrayList<UUID> getIds() throws IOException {
		 ArrayList<UUID> ids = new ArrayList<UUID>();
		 
		File file = new File(ConfigLoader.getInstance().getIDsFile());
		file.getParentFile().mkdirs();
		if (!file.exists()) {
			file.createNewFile();
		}
		FileReader fr = new FileReader(file);
		BufferedReader reader = new BufferedReader(fr);
		String line;
		while ((line=reader.readLine())!=null && line.length()!=0) {
			ids.add(UUID.fromString(line));
		}
		reader.close();
		return ids;
	}
}
