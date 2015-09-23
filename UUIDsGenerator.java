import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;


public class UUIDsGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int cant = Integer.parseInt(args[0]);

		try {
			File file = new File(ConfigLoader.getInstance().getIDsFile());
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
			for (int i=0;i<=cant;i++) {
				writer.write(UUID.randomUUID().toString());
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			System.out.println("No se pudo crear los IDs");
			e.printStackTrace();
		}
	}

}
