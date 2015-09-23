import java.io.IOException;


public class OrderManager implements QueueProcesser<NewOrderMessage>, 
									MessageTransformer<NewOrderMessage> {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Queue<NewOrderMessage> colaPedidos = null;
		try {
			//Leer el nuevo pedido
			ConfigLoader conf = ConfigLoader.getInstance();
			String pedidosQueue = conf.getOrdersQueueName();
			OrderManager manager = new OrderManager();
			colaPedidos = 
					new Queue<NewOrderMessage>(pedidosQueue, manager, manager);
			
			colaPedidos.connect();
			colaPedidos.receive();
		} catch (IOException e) {
			System.out.println("Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("Error de la cola de mensajes.");
			if (colaPedidos != null) {
				try {
					colaPedidos.disconnect();
				} catch (ColaException e1) {
					System.out.println("Error al desconectar cola de mensajes");
				}
			}
		}

	}
	
	
	@Override
	public void process(NewOrderMessage message) throws IOException, 
														ColaException {
		
		System.out.println("Recibido "+message.getID()+
							" "+message.getTipo()+" "+message.getCantidad());
		
		ConfigLoader conf = ConfigLoader.getInstance();
		
		//pone el pedido en la cola de LOGGING
		String loggingQueue = conf.getLoggingQueueName();
		Queue<NewOrderMessage> colaNuevaOrden = 
				new Queue<NewOrderMessage>(loggingQueue,this,this);
		
		colaNuevaOrden.connect();
		System.out.println("conectado");
		colaNuevaOrden.send(message);
		System.out.println("mandado a logger");
		colaNuevaOrden.disconnect();
		System.out.println("desconectado");
		
		//pone el pedido en el archivo de Orders (ID + "recibida")
		OrdersStorage orders = new OrdersStorage();
		orders.saveNewOrder(message.getID(),ConfigLoader.RECIBIDA);
		System.out.println("orden guardada");
		
		//manda el pedido a la cola de PROCESSING
		String processingQueue = conf.getProcessingQueueName();
		Queue<NewOrderMessage> colaProcesar = new Queue<NewOrderMessage>(processingQueue,this,this);
		colaProcesar.connect();
		System.out.println("conectado a pr");
		colaProcesar.send(message);
		System.out.println("msj mandado a pr");
		colaProcesar.disconnect();
		System.out.println("desconectado de pro");
	}

	@Override
	public NewOrderMessage transform(Object o) {
		return (NewOrderMessage) o;
	}
}
