import java.util.UUID;


public class Empleado implements QueueProcesser<OrderMessage>, MessageTransformer<OrderMessage> {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//para un ID de una orden
		UUID id = UUID.randomUUID(); //TODO
		
		//crear mensaje
		OrderMessage msg = new OrderMessage(id);
		
		//envia mensaje con el ID
		ConfigLoader conf = ConfigLoader.getInstance();
		String deliverQueue = conf.getUpdateStateQueueName();
		
		Empleado empleado = new Empleado();
		Queue<OrderMessage> colaEntregar = new Queue<OrderMessage>(deliverQueue, empleado, empleado);
		
		colaEntregar.connect();
		colaEntregar.send(msg);
		colaEntregar.disconnect();
	}

	@Override
	public OrderMessage transform(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void process(OrderMessage message) {
		// TODO Auto-generated method stub
		
	}

}
