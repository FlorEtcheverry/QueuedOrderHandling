import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class ConfigLoader {
	
	private Properties config;
	
	private static ConfigLoader instance = null;
	
	public static ConfigLoader getInstance() throws IOException {
		if (instance == null)
			instance = new ConfigLoader();
		return instance;
	}
	
	private ConfigLoader() throws IOException {
		config = new Properties();
		FileInputStream in = new FileInputStream("config.properties");
		config.load(in);
		in.close();
	}
	
	public String getHost() {
		return config.getProperty("host","localhost"); 
	}
	
	public String getOrdersQueueName() {
		return config.getProperty("ordersQueue","newOrder_queue"); 
	}
	
	public String getLoggingQueueName() {
		return config.getProperty("loggingQueue","logging_queue"); 
	}
	
	public String getProcessingQueueName() {
		return config.getProperty("processingQueue","processing_queue"); 
	}
	
	public String getQueryStateQueueName() {
		return config.getProperty("queryQueue","query_queue");
	}
	
	public String getUpdateStateQueueName() {
		return config.getProperty("deliverQueue","deliver_queue");
	}
	
	public String getAddStockQueueName() {
		return config.getProperty("addStockQueue","addStock_queue"); 
	}
	
	public String getLogFilePath() {
		return config.getProperty("logFilePath","orders.log");
	}
	
	public int getMaxTipo() {
		return Integer.parseInt(config.getProperty("maxTipo","10"));
	}
	
	public int getMaxStock() {
		return  Integer.parseInt(config.getProperty("maxStock","100000"));
	}
	
	public String getIDsFile() {
		return  config.getProperty("idsPath","ids");
	}
	
	public static final char RECIBIDA = 'R';
	public static final char ACEPTADA = 'A';
	public static final char RECHAZADA = 'X';
	public static final char ENTREGADA = 'E';

}
