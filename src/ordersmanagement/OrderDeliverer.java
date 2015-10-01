package ordersmanagement;
import java.io.IOException;
import java.util.UUID;

import common.ConfigLoader;
import communication.ColaException;
import communication.OrderMessage;
import communication.Queue;
import communication.QueueProcesser;


public class OrderDeliverer implements QueueProcesser<OrderMessage> {

	public static void main(String[] args) {
		
		Queue<OrderMessage> colaUpdate = null;
		try {
			ConfigLoader conf = ConfigLoader.getInstance();
			String changeStateQueue = conf.getUpdateStateQueueName();
			
			OrderDeliverer orderDeliverer = new OrderDeliverer();
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
		} finally {
			if (colaUpdate != null) {
				try {
					colaUpdate.disconnect();
				} catch (ColaException e1) {
					System.out.println("				ORDER DELIVERER - "
							+ "Error al desconectar cola de mensajes");
				}
			}
		}
	}

	@Override
	public void process(OrderMessage message) throws IOException {

		//lee de la cola el ID de la orden
		UUID id = message.getOrderId();
		
		//cambia el estado de "aceptada" a "entregada"
		OrdersStorage ordenes = new OrdersStorage();
		char estado = ordenes.getOrderState(id);
		if (estado == ConfigLoader.ACEPTADA) {
			ordenes.saveNewOrder(id, estado);
		} else {
			//el ID no es válido
			System.out.println("El ID "+id.toString()+
					" no se puede aceptar porque la orden no fue aceptada.");
		}
		
	}

}