package ordersmanagement;
import java.io.IOException;
import java.util.UUID;

import common.ConfigLoader;
import communication.ColaException;
import communication.OrderMessage;
import communication.Queue;
import communication.QueueProcesser;

public class OrderStateController implements QueueProcesser<OrderMessage> {

	private OrdersStorage ordenes;
	
	private static class Quitter implements Runnable {
			
			private Queue<OrderMessage> colaQueries;
			private OrdersStorage ordenes;		
	
			public Quitter(
					Queue<OrderMessage> colaQueries, 
					OrdersStorage ordenes) 
			{
				this.colaQueries=colaQueries;
				this.ordenes=ordenes;
			}
			
			@Override
			public void run(){
				if (colaQueries != null) {
					try {
						colaQueries.disconnect();
					} catch (ColaException e1) {
						System.out.println("			ORDER STATE CONTROLLER"
								+ " - Error al desconectar cola de mensajes");
					}
				}
				try {
					ordenes.close();
				} catch (IOException e) {
					System.out.println("			ORDER STATE CONTROLLER - "
							+ "Error al cerrar archivos de ordenes");
				}
				System.out.println("Order State Controller "
									+ "cerrado correctamente.");
			}
		}
	
	public static void main(String[] args) {
		
		Queue<OrderMessage> colaQueries = null;
		OrderStateController orderController = new OrderStateController();
		orderController.ordenes = new OrdersStorage();
		
		try {
			ConfigLoader conf = ConfigLoader.getInstance();
			String queryQueue = conf.getQueryStateQueueName();
			
			colaQueries = new Queue<OrderMessage>(
								queryQueue, orderController);

			colaQueries.connect();
			System.out.println("ORDER STATE CONTROLLER Iniciado. "
					+ "Esperando nuevas consultas.");
			colaQueries.receive(); //lee de la cola: ID de la orden y procesa
			
		} catch (IOException e) {
			System.out.println("			ORDER STATE CONTROLLER - "
					+ "Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("			ORDER STATE CONTROLLER - "
					+ "Error de la cola de mensajes.");
		} /*finally { FIXME
			if (colaQueries != null) {
				try {
					colaQueries.disconnect();
				} catch (ColaException e1) {
					System.out.println("			ORDER STATE CONTROLLER - "
							+ "Error al desconectar cola de mensajes");
				}
			}
		} */
		Runtime.getRuntime().addShutdownHook(
				new Thread(new Quitter(colaQueries,orderController.ordenes))
				); 
	}

	@Override
	public void process(OrderMessage message) throws IOException {

		UUID idOrden = message.getOrderId();
		
		//se fija en el archivo de ordenes, para ese ID, el estado
		char estado = ordenes.getOrderState(idOrden);
		
		//devuelve el estado al usuario
		System.out.println("Para el ID: "+idOrden+". El estado es: "+estado);		
	}

}
