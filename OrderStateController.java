import java.io.IOException;
import java.util.UUID;


public class OrderStateController implements QueueProcesser<OrderMessage>, 
											MessageTransformer<OrderMessage> {

	public static void main(String[] args) {
		
		Queue<OrderMessage> colaQueries = null;
		try {
			ConfigLoader conf = ConfigLoader.getInstance();
			String queryQueue = conf.getQueryStateQueueName();
			
			OrderStateController orderController = new OrderStateController();
			colaQueries = new Queue<OrderMessage>(
								queryQueue, orderController, orderController);

			colaQueries.connect();
			colaQueries.receive(); //lee de la cola: ID de la orden
			
		} catch (IOException e) {
			System.out.println("Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("Error de la cola de mensajes.");
		} finally {
			if (colaQueries != null) {
				try {
					colaQueries.disconnect();
				} catch (ColaException e1) {
					System.out.println("Error al desconectar cola de mensajes");
				}
			}
		}
	}

	@Override
	public OrderMessage transform(Object o) {
		return (OrderMessage) o;
	}

	@Override
	public void process(OrderMessage message) throws IOException {

		UUID idOrden = message.getOrderId();
		
		//se fija en el archivo de ordenes, para ese ID, el estado
		OrdersStorage ordenes = new OrdersStorage();
		char estado = ordenes.getOrderState(idOrden);
		
		//devuelve el estado al usuario
		System.out.println("Para el ID: "+idOrden+". El estado es: "+estado);
		
	}

}
