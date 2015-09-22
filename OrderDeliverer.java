import java.io.IOException;
import java.util.UUID;


public class OrderDeliverer implements QueueProcesser<OrderMessage>, MessageTransformer<OrderMessage> {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConfigLoader conf = ConfigLoader.getInstance();
		String changeStateQueue = conf.getUpdateStateQueueName();
		
		OrderDeliverer orderDeliverer = new OrderDeliverer();
		Queue<OrderMessage> colaUpdate = new Queue<OrderMessage>(changeStateQueue, orderDeliverer, orderDeliverer);

		colaUpdate.connect();
		colaUpdate.recieve();
		colaUpdate.disconnect();
	}

	@Override
	public OrderMessage transform(Object o) {
		return (OrderMessage) o;
	}

	@Override
	public void process(OrderMessage message) throws IOException {
		// TODO Auto-generated method stub
		//lee de la cola el ID de la orden
		UUID id = message.getOrderId();
		
		//cambia el estado de "aceptada" a "entregada" ---sino estaba en aceptada, ERROR
		OrdersStorage ordenes = new OrdersStorage();
		char estado = ordenes.getOrderState(id);
		if (estado == ConfigLoader.ACEPTADA) {
			ordenes.saveNewOrder(id, estado);
		} else {
			//el ID no es válido TODO
		}
		
	}

}
