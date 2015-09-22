import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class ConfigLoader {
	
	private Properties config;
	
	private static ConfigLoader instance = null;
	
	public static ConfigLoader getInstance() {
		if (instance == null)
			instance = new ConfigLoader();
		return instance;
	}
	
	private ConfigLoader() {
		config = new Properties();
		try {
			FileInputStream in = new FileInputStream("config.properties");
			config.load(in);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getHost() {
		return config.getProperty("host","localhost"); //TODO
	}
	
	public String getOrdersQueueName() {
		return config.getProperty("ordersQueue","newOrder_queue"); //TODO
	}
	
	public String getLoggingQueueName() {
		return config.getProperty("loggingQueue","logging_queue"); //TODO
	}
	
	public String getProcessingQueueName() {
		return config.getProperty("processingQueue","processing_queue"); //TODO
	}
	
	public String getQueryStateQueueName() {
		return config.getProperty("queryQueue","query_queue"); //TODO
	}
	
	public String getUpdateStateQueueName() {
		return config.getProperty("deliverQueue","deliver_queue"); //TODO
	}
	
	public String getAddStockQueueName() {
		return config.getProperty("addStockQueue","addStock_queue"); //TODO
	}
	
	public String getLogFilePath() {
		return config.getProperty("logFilePath","orders.log"); //TODO
	}
	
	public static final char RECIBIDA = 'R';
	public static final char ACEPTADA = 'A';
	public static final char RECHAZADA = 'X';
	public static final char ENTREGADA = 'E';

}
