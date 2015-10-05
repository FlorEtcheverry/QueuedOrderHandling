package ordersmanagement;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import common.ConfigLoader;
import communication.ColaException;
import communication.NewOrderMessage;
import communication.Queue;
import communication.QueueProcesser;

public class Logger implements QueueProcesser<NewOrderMessage> {
	
	private Queue<NewOrderMessage> colaLogging;
	private BufferedWriter writer;
	
	private static class Quitter implements Runnable {
		
		private Queue<NewOrderMessage> colaLogging;
		private BufferedWriter writer;

		public Quitter(
				Queue<NewOrderMessage> colaLogging,
				BufferedWriter writer) 
		{
			this.colaLogging = colaLogging;
			this.writer = writer;
		}
		
		@Override
		public void run(){
			if (colaLogging != null) {
				try {
					colaLogging.disconnect();
				} catch (ColaException e1) {
					System.out.println("	LOGGER - "
							+ "Error al desconectar cola de mensajes.");
				}
			}
			try {
				writer.close();
			} catch (IOException e) {
				System.out.println("	LOGGER - "
						+ "Error al cerrar el archivo de logging.");
			}
			System.out.println("Logger cerrado correctamente.");
		}
	}

	public static void main(String[] args) {

		Logger logger = new Logger();
		try {
			//lee de la cola msj de pedido
			ConfigLoader conf = ConfigLoader.getInstance();
			String loggingQueue = conf.getLoggingQueueName();
			logger.colaLogging = 
					new Queue<NewOrderMessage>(loggingQueue, logger);
			
			File file = new File(conf.getLogFilePath());
			file.getParentFile().mkdirs();
			if (!file.exists()) {
				file.createNewFile();
			}
			logger.writer = new BufferedWriter(new FileWriter(file, true));

			logger.colaLogging.connect();
			logger.colaLogging.receive();
			
		} catch (IOException e) {
			System.out.println("	LOGGER - Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("	LOGGER - Error de la cola de mensajes.");
		} /*finally { FIXME
			if (colaLogging != null) {
				try {
					colaLogging.disconnect();
				} catch (ColaException e1) {
					System.out.println("	LOGGER - "
							+ "Error al desconectar cola de mensajes");
				}
			}
		}*/
		Runtime.getRuntime().addShutdownHook(new Thread(
				new Quitter(logger.colaLogging,logger.writer))); 
	}

	@Override
	public void process(NewOrderMessage message) throws IOException {
		//appendea en el archivo: timestamp + msj (id pedido+tipo+cant)
		
		Date time = new Date();
		String msg = time+" : "+message.getID()+" Producto tipo: "+
						message.getTipo()+" Cantidad: "+message.getCantidad();
		writer.write(msg);
		writer.newLine();
		writer.flush();
	}

}
