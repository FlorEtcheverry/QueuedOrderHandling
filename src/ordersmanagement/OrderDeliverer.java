package ordersmanagement;
import java.io.IOException;
import java.util.UUID;

import common.ConfigLoader;
import communication.ColaException;
import communication.OrderMessage;
import communication.Queue;
import communication.QueueProcesser;

public class OrderDeliverer implements QueueProcesser<OrderMessage> {

	private Queue<OrderMessage> colaUpdate;
	private OrdersStorage ordenes;
	
	private static class Quitter implements Runnable {
		
		private Queue<OrderMessage> colaUpdate;
		private OrdersStorage ordenes;		

		public Quitter(
				Queue<OrderMessage> colaUpdate,
				OrdersStorage ordenes) 
		{
			this.colaUpdate = colaUpdate;
			this.ordenes = ordenes;
		}
		
		@Override
		public void run(){
			if (colaUpdate != null) {
				try {
					colaUpdate.disconnect();
				} catch (ColaException e1) {
					System.out.println("				ORDER DELIVERER - "
							+ "Error al desconectar cola de mensajes");
				}
			}
			try {
				ordenes.close();
			} catch (IOException e) {
				System.out.println("				ORDER DELIVERER - "
						+ "Error al cerrar archivos de ordenes");
			}
			System.out.println("Order Deliverer cerrado correctamente.");
		}
	}
	
	public static void main(String[] args) {
		
		OrderDeliverer orderDeliverer = new OrderDeliverer();
		orderDeliverer.ordenes = new OrdersStorage();
		try {
			ConfigLoader conf = ConfigLoader.getInstance();
			String changeStateQueue = conf.getUpdateStateQueueName();
			
			orderDeliverer.colaUpdate = new Queue<OrderMessage>(
							changeStateQueue, orderDeliverer);

			orderDeliverer.colaUpdate.connect();
			orderDeliverer.colaUpdate.receive();
		} catch (IOException e) {
			System.out.println("				ORDER DELIVERER - "
					+ "Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("				ORDER DELIVERER - "
					+ "Error de la cola de mensajes.");
		} /*finally { //FIXME 
			if (colaUpdate != null) {
				try {
					colaUpdate.disconnect();
				} catch (ColaException e1) {
					System.out.println("				ORDER DELIVERER - "
							+ "Error al desconectar cola de mensajes");
				}
			}
		}*/
		Runtime.getRuntime().addShutdownHook(new Thread(
				new Quitter(
						orderDeliverer.colaUpdate,
						orderDeliverer.ordenes
						))); 
	}

	@Override
	public void process(OrderMessage message) throws IOException {

		//lee de la cola el ID de la orden
		UUID id = message.getOrderId();
		
		//cambia el estado de "aceptada" a "entregada"
		char estado = ordenes.getOrderState(id);
		if (estado == ConfigLoader.ACEPTADA) {
			ordenes.changeOrderState(id, ConfigLoader.ENTREGADA);
			System.out.println("La orden "+id.toString()+" fue ENTREGADA.");
		} else if (estado == ConfigLoader.RECHAZADA) {
			System.out.println("NO se puede entregar la orden con ID "+
					id.toString()+" porque fue rechazada.");
		} else {
			System.out.println("NO se puede entregar la orden con ID "+
					id.toString()+" porque no fue recibida.");
		}
		
	}

}
