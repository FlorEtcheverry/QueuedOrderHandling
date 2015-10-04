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
	private OrdersStorage orders;
	
	private static class Quitter implements Runnable {
		
		private Queue<NewOrderMessage> colaPedidos;
		private Queue<NewOrderMessage> colaNuevaOrden;
		private Queue<NewOrderMessage> colaProcesar;
		private OrdersStorage ordenes;		

		public Quitter(
				Queue<NewOrderMessage> colaPedidos,
				Queue<NewOrderMessage> colaNuevaOrden,
				Queue<NewOrderMessage> colaProcesar,
				OrdersStorage ordenes) 
		{
			this.colaPedidos = colaPedidos;
			this.colaNuevaOrden = colaNuevaOrden;
			this.colaProcesar = colaProcesar;
			this.ordenes = ordenes;
		}
		
		@Override
		public void run(){
			try {
				if (colaPedidos != null) {
					colaPedidos.disconnect();
				}
				if (colaNuevaOrden != null) {
					colaNuevaOrden.disconnect();
				}
				if (colaProcesar != null) {
					colaProcesar.disconnect();
				}
			} catch (ColaException e1) {
				System.out.println("ORDER MANAGER - "
						+ "Error al desconectar colas de mensajes");
			}
			try {
				ordenes.close();
			} catch (IOException e) {
				System.out.println("ORDER MANAGER - "
						+ "Error al cerrar archivos de ordenes");
			}
			System.out.println("Order Manager cerrado correctamente.");
		}
	}

	public static void main(String[] args) {
		
		OrderManager manager = new OrderManager();
		manager.orders = new OrdersStorage();
		
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
		Runtime.getRuntime().addShutdownHook(new Thread(
				new Quitter(
						manager.colaPedidos,
						manager.colaNuevaOrden,
						manager.colaProcesar,
						manager.orders
						))); 
	}
	
	@Override
	public void process(NewOrderMessage message) throws IOException, 
														ColaException {
		
		/*System.out.println("Orden nueva recibida: "+message.getID()+
							" "+message.getTipo()+" "+message.getCantidad());*/
		
		//pone el pedido en la cola de LOGGING		
		colaNuevaOrden.send(message);
		
		//pone el pedido en el archivo de Orders (ID + "recibida")
		orders.saveNewOrder(message.getID(),ConfigLoader.RECIBIDA);
		
		//manda el pedido a la cola de PROCESSING
		colaProcesar.send(message);
	}
}
