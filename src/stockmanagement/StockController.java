package stockmanagement;

import java.io.IOException;

import ordersmanagement.OrdersStorage;
import common.ConfigLoader;
import communication.ColaException;
import communication.NewOrderMessage;
import communication.Queue;
import communication.QueueProcesser;


public class StockController implements QueueProcesser<NewOrderMessage> {

	private StockStorage stock;
	private OrdersStorage orders;
	
	public static void main(String[] args) {
		//lee de la cola pedido: ID + TIPO + CANTIDAD
		
		Queue<NewOrderMessage> colaProcessing = null;
		StockController stockController = new StockController();
		stockController.stock = new StockStorage();
		stockController.orders = new OrdersStorage();
		
		try {
			ConfigLoader conf = ConfigLoader.getInstance();
			String processingQueue = conf.getProcessingQueueName();
			
			colaProcessing = new Queue<NewOrderMessage>(
							processingQueue,stockController);
			
			colaProcessing.connect();
			colaProcessing.receive();
			
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
