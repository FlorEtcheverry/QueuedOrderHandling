package ordersmanagement;
import java.io.IOException;
import java.util.UUID;

import common.ConfigLoader;
import communication.ColaException;
import communication.OrderMessage;
import communication.Queue;
import communication.QueueProcesser;


public class OrderDeliverer implements QueueProcesser<OrderMessage> {

	private OrdersStorage ordenes;
	
	public static void main(String[] args) {
		
		Queue<OrderMessage> colaUpdate = null;
		OrderDeliverer orderDeliverer = new OrderDeliverer();
		orderDeliverer.ordenes = new OrdersStorage();
		try {
			ConfigLoader conf = ConfigLoader.getInstance();
			String changeStateQueue = conf.getUpdateStateQueueName();
			
			colaUpdate = new Queue<OrderMessage>(
							changeStateQueue, orderDeliverer);

			colaUpdate.connect();
			colaUpdate.receive();
			
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
	}

	@Override
	public void process(OrderMessage message) throws IOException {

		//lee de la cola el ID de la orden
		UUID id = message.getOrderId();
		
		//cambia el estado de "aceptada" a "entregada"
		char estado = ordenes.getOrderState(id);
		if (estado == ConfigLoader.ACEPTADA) {
			ordenes.saveNewOrder(id, estado);
		} else {
			//el ID no es válido
			System.out.println("NO se puede aceptar la orden con ID "+
					id.toString()+" porque la orden no fue recibida.");
		}
		
	}

}
