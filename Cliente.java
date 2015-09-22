import java.util.UUID;


public class Cliente implements QueueProcesser<NewOrderMessage>, MessageTransformer<NewOrderMessage> {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ConfigLoader conf = ConfigLoader.getInstance();
		String pedidosQueue = conf.getOrdersQueueName();
		
		Cliente cliente = new Cliente();
		Queue<NewOrderMessage> colaPedidos = new Queue<NewOrderMessage>(pedidosQueue, cliente, cliente);
		
		//crear pedido
		UUID id = UUID.randomUUID(); //TODO chequear que sea SI o SI de largo 36 
		int tipo = 1; //TODO
		int cant = 50; //TODO
		
		//crear mensaje
		NewOrderMessage msg = new NewOrderMessage(id, tipo, cant);
		System.out.println("cliente por conectar");
		colaPedidos.connect();
		colaPedidos.send(msg);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		colaPedidos.disconnect();
		System.out.println("cliente envio");
	}

	@Override
	public NewOrderMessage transform(Object o) {
		return (NewOrderMessage) o;
	}

	@Override
	public void process(NewOrderMessage message) {
		// TODO Auto-generated method stub
	}
}
