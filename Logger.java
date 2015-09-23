import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger implements QueueProcesser<NewOrderMessage>, MessageTransformer<NewOrderMessage> {

	public static void main(String[] args) {

		Queue<NewOrderMessage> colaLogging = null;
		try {
			//lee de la cola msj de pedido
			ConfigLoader conf = ConfigLoader.getInstance();
			String loggingQueue = conf.getLoggingQueueName();
			
			Logger logger = new Logger();
			colaLogging = new Queue<NewOrderMessage>(loggingQueue, logger, logger);

			colaLogging.connect();
			colaLogging.recieve();
		} catch (IOException e) {
			System.out.println("Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("Error de la cola de mensajes.");
			if (colaLogging != null) {
				try {
					colaLogging.disconnect();
				} catch (ColaException e1) {
					System.out.println("Error al desconectar cola de mensajes.");
				}
			}
		}

	}

	@Override
	public NewOrderMessage transform(Object o) {
		return (NewOrderMessage) o;
	}

	@Override
	public void process(NewOrderMessage message) throws IOException {
		//appendea en el archivo: timestamp + msj (id pedido+tipo+cant)
		
		Date time = new Date();
		String msg = time+" "+message.getID()+" "+message.getCantidad();
	
		File file = new File(ConfigLoader.getInstance().getLogFilePath());
		if (!file.exists()) {
			file.createNewFile();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		writer.write(msg);
		writer.newLine();
		writer.flush();
		writer.close();
	}

}
