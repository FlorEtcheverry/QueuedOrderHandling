package ordersmanagement;
import java.io.IOException;

import common.ConfigLoader;
import communication.ColaException;
import communication.NewOrderMessage;
import communication.Queue;
import communication.QueueProcesser;


public class OrderManager implements QueueProcesser<NewOrderMessage> {
	
	private static Queue<NewOrderMessage> colaNuevaOrden;
	private static Queue<NewOrderMessage> colaProcesar;
	private static Queue<NewOrderMessage> colaPedidos;

	public static void main(String[] args) {
		
		try {
			//Leer el nuevo pedido
			ConfigLoader conf = ConfigLoader.getInstance();
			String pedidosQueue = conf.getOrdersQueueName();
			OrderManager manager = new OrderManager();
			colaPedidos = 
					new Queue<NewOrderMessage>(pedidosQueue, manager);
			
			String loggingQueue = conf.getLoggingQueueName();
			colaNuevaOrden = 
					new Queue<NewOrderMessage>(loggingQueue,manager);
			
			colaNuevaOrden.connect();
			String processingQueue = conf.getProcessingQueueName();
			colaProcesar = 
					new Queue<NewOrderMessage>(processingQueue,manager);
			
			colaPedidos.connect();
			colaProcesar.connect();
			
			colaPedidos.receive();
			
		} catch (IOException e) {
			System.out.println("ORDER MANAGER - Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("ORDER MANAGER - Error de la cola de mensajes.");
		} finally {
			try {
				if (colaPedidos != null) colaPedidos.disconnect();
				if (colaNuevaOrden != null) colaNuevaOrden.disconnect();
				if (colaProcesar != null) colaProcesar.disconnect();
			} catch (ColaException e1) {
				System.out.println("ORDER MANAGER - "
						+ "Error al desconectar colas de mensajes");
			}
		}
	}
	
	
	@Override
	public void process(NewOrderMessage message) throws IOException, 
														ColaException {
		
		/*System.out.println("Orden nueva recibida: "+message.getID()+
							" "+message.getTipo()+" "+message.getCantidad());
		*/
		
		//pone el pedido en la cola de LOGGING		
		colaNuevaOrden.send(message);
		
		//pone el pedido en el archivo de Orders (ID + "recibida")
		OrdersStorage orders = new OrdersStorage();
		orders.saveNewOrder(message.getID(),ConfigLoader.RECIBIDA);
		
		//manda el pedido a la cola de PROCESSING
		colaProcesar.send(message);
	}
}
