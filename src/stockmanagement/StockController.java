package stockmanagement;
import java.io.IOException;

import common.ConfigLoader;
import communication.ColaException;
import communication.NewOrderMessage;
import communication.Queue;
import communication.QueueProcesser;
import ordersmanagement.OrdersStorage;

public class StockController implements QueueProcesser<NewOrderMessage> {

	private Queue<NewOrderMessage> colaProcessing;
	private StockStorage stock;
	private OrdersStorage orders;
	
	private static class Quitter implements Runnable {
		
		private Queue<NewOrderMessage> colaProcessing;
		private StockStorage stock;
		private OrdersStorage orders;		

		public Quitter(
				Queue<NewOrderMessage> colaProcessing,
				StockStorage stock,
				OrdersStorage orders) 
		{
			this.colaProcessing = colaProcessing;
			this.stock = stock;
			this.orders = orders;
		}
		
		@Override
		public void run(){
			if (colaProcessing != null) {
				try {
					colaProcessing.disconnect();
				} catch (ColaException e1) {
					System.out.println("		STOCK CONTROLLER - "
							+ "Error al desconectar cola de mensajes");
				}
			}
			try {
				orders.close();
			} catch (IOException e) {
				System.out.println("			STOCK CONTROLLER - "
						+ "Error al cerrar archivos de ordenes");
			}
			try {
				stock.close();
			} catch (IOException e) {
				System.out.println("			STOCK CONTROLLER - "
						+ "Error al cerrar archivos de stock");
			}
			System.out.println("Stock controller cerrado correctamente.");
		}
	}
	
	public static void main(String[] args) {
		//lee de la cola pedido: ID + TIPO + CANTIDAD

		StockController stockController = new StockController();
		stockController.stock = new StockStorage();
		stockController.orders = new OrdersStorage();
		
		try {
			ConfigLoader conf = ConfigLoader.getInstance();
			String processingQueue = conf.getProcessingQueueName();
			
			stockController.colaProcessing = new Queue<NewOrderMessage>(
							processingQueue,stockController);
			
			stockController.colaProcessing.connect();
			stockController.colaProcessing.receive();
			
		} catch (IOException e) {
			System.out.println("		STOCK CONTROLLER - "
					+ "Error al leer de archivo de configuracion.");
		} catch (ColaException e) {
			System.out.println("		STOCK CONTROLLER - "
					+ "Error de la cola de mensajes.");
		} /*finally { FIXME
			if (colaProcessing != null) {
				try {
					colaProcessing.disconnect();
				} catch (ColaException e1) {
					System.out.println("		STOCK CONTROLLER - "
							+ "Error al desconectar cola de mensajes");
				}
			}
		} */
		Runtime.getRuntime().addShutdownHook(new Thread(
				new Quitter(
						stockController.colaProcessing,
						stockController.stock,
						stockController.orders
						))); 
	}

	@Override
	public void process(NewOrderMessage message) throws IOException {
		int tipo = message.getTipo();
		int cant = message.getCantidad();
		
		boolean restado = stock.restarStock(tipo,cant);
		if (restado) {
			//escribe "aceptada" en el archivo de ordenes, para ese ID
			orders.changeOrderState(message.getID(),ConfigLoader.ACEPTADA);
			System.out.println("Cambiado el estado de orden "+
											message.getID()+" a aceptada.");
		} else {
			//escribe "rechazada" en el archivo de ordenes, para ese ID
			orders.changeOrderState(message.getID(),ConfigLoader.RECHAZADA);
			System.out.println("Cambiado el estado de orden "+
											message.getID()+" a rechazada.");
		}
	}
}
