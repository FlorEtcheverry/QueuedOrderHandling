package ordersmanagement;
import java.io.IOException;

import common.ConfigLoader;
import communication.ColaException;
import communication.NewOrderMessage;
import communication.Queue;
import communication.QueueProcesser;


public class OrderManager implements QueueProcesser<NewOrderMessage> {
	
	private Queue<NewOrderMessage> colaNuevaOrden;
	private Queue<NewOrderMessage> colaProcesar;
	private Queue<NewOrderMessage> colaPedidos;

	public static void main(String[] args) {
		
		OrderManager manager = new OrderManager();
		
		try {
			//Leer el nuevo pedido
			ConfigLoader conf = ConfigLoader.getInstance();
			String pedidosQueue = conf.getOrdersQueueName();
			String loggingQueue = conf.getLoggingQueueName();
			String processingQueue = conf.getProcessingQueueName();
			
			manager.colaPedidos = 
					new Queue<NewOrderMessage>(pedidosQueue, manager);
			
			manager.colaNuevaOrden = 
					new Queue<NewOrderMessage>(loggingQueue,manager);
			
			manager.colaProcesar = 
					new Queue<NewOrderMessage>(processingQueue,manager);
			
			manager.colaNuevaOrden.connect();
			manager.colaPedidos.connect();
			manager.colaProcesar.connect();
			
			System.out.println("ORDER MANAGER Iniciado. "
					+ "Esperando nuevos pedidos.");
			manager.colaPedidos.receive();
			
		} catch (IOException e) {
			System.out.println("ORDER MANAGER - Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("ORDER MANAGER - Error de la cola de mensajes.");
		} /*finally {
			try {
				if (manager.colaPedidos != null) {
					manager.colaPedidos.disconnect();
				}
				if (manager.colaNuevaOrden != null) {
					manager.colaNuevaOrden.disconnect();
				}
				if (manager.colaProcesar != null) {
					manager.colaProcesar.disconnect();
				}
			} catch (ColaException e1) {
				System.out.println("ORDER MANAGER - "
						+ "Error al desconectar colas de mensajes");
			}
		} */ //FIXME
	}
	
	@Override
	public void process(NewOrderMessage message) throws IOException, 
														ColaException {
		
		/*System.out.println("Orden nueva recibida: "+message.getID()+
							" "+message.getTipo()+" "+message.getCantidad());*/
		
		//pone el pedido en la cola de LOGGING		
		colaNuevaOrden.send(message);
		
		//pone el pedido en el archivo de Orders (ID + "recibida")
		OrdersStorage orders = new OrdersStorage();
		orders.saveNewOrder(message.getID(),ConfigLoader.RECIBIDA);
		
		//manda el pedido a la cola de PROCESSING
		colaProcesar.send(message);
	}
}
