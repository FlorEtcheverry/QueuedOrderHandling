import java.util.UUID;


public class OrderStateController implements QueueProcesser<OrderMessage>, MessageTransformer<OrderMessage> {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConfigLoader conf = ConfigLoader.getInstance();
		String queryQueue = conf.getQueryStateQueueName();
		
		OrderStateController orderController = new OrderStateController();
		Queue<OrderMessage> colaQueries = new Queue<OrderMessage>(queryQueue, orderController, orderController);

		colaQueries.connect();
		colaQueries.recieve(); //lee de la cola: ID de la orden
		colaQueries.disconnect();
	}

	@Override
	public OrderMessage transform(Object o) {
		return (OrderMessage) o;
	}

	@Override
	public void process(OrderMessage message) {
		// TODO Auto-generated method stub
		UUID idOrden = message.getOrderId();
		
		//se fija en el archivo de ordenes, para ese ID, el estado
		OrdersStorage ordenes = new OrdersStorage();
		char estado = ordenes.getOrderState(idOrden);
		
		//devuelve el estado al usuario
		System.out.println("Para el ID: "+idOrden+". El estado es: "+estado);
		
	}

}
