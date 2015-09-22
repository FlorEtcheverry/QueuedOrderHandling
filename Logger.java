import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger implements QueueProcesser<NewOrderMessage>, MessageTransformer<NewOrderMessage> {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//lee de la cola msj de pedido
		ConfigLoader conf = ConfigLoader.getInstance();
		String loggingQueue = conf.getLoggingQueueName();
		
		Logger logger = new Logger();
		Queue<NewOrderMessage> colaLogging = new Queue<NewOrderMessage>(loggingQueue, logger, logger);

		colaLogging.connect();
		try {
			while (true)
			colaLogging.recieve();
		} finally {
		colaLogging.disconnect();
		}
	}

	@Override
	public NewOrderMessage transform(Object o) {
		return (NewOrderMessage) o;
	}

	@Override
	public void process(NewOrderMessage message) throws IOException {
		// TODO Auto-generated method stub
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
