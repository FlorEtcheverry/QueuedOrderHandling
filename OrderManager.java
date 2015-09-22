import java.io.IOException;


public class OrderManager implements QueueProcesser<NewOrderMessage>, MessageTransformer<NewOrderMessage> {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Leer el nuevo pedido
		ConfigLoader conf = ConfigLoader.getInstance();
		String pedidosQueue = conf.getOrdersQueueName();
		OrderManager manager = new OrderManager();
		Queue<NewOrderMessage> colaPedidos = new Queue<NewOrderMessage>(pedidosQueue, manager, manager);
		
		colaPedidos.connect();
		System.out.println("Conectado");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			while (true) {
				colaPedidos.recieve();
				System.out.println("procesado");
			}
		} finally {
			colaPedidos.disconnect();
			System.out.println("desConectado");
		}
/*
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(conf.getHost());
		
	    try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			
			//true por durable
			channel.queueDeclare(pedidosQueue, true, false, false, null);
			
			//not to give more than one message to a worker at a time
			channel.basicQos(1);
			
			//defino qué hacer al consumir
			Consumer consumer = new DefaultConsumer(channel) {
			    @Override
			    public void handleDelivery(
			    		String consumerTag, 
			    		Envelope envelope, 
			    		AMQP.BasicProperties properties, 
			    		byte[] body
			    		) throws IOException {
			    	
			    	NewOrderMessage message = NewOrderMessage.fromBytes(body);
			    	try { //TODO ???
			    		procesarOrden(message);
		    		} finally {
		    			//ack de msj procesado
		    			channel.basicAck(envelope.getDeliveryTag(), false);
		    		}
			    }
		    };
		    //lee pedido de la cola - queue,autoACK,consumer
		    channel.basicConsume(pedidosQueue, false, consumer);
			  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	
	@Override
	public void process(NewOrderMessage message) throws IOException {
		
		System.out.println("Recibido "+message.getID()+" "+message.getTipo()+" "+message.getCantidad());
		ConfigLoader conf = ConfigLoader.getInstance();
		
		//pone el pedido en la cola de LOGGING
		String loggingQueue = conf.getLoggingQueueName();
		Queue<NewOrderMessage> colaNuevaOrden = new Queue<NewOrderMessage>(loggingQueue,this,this);
		colaNuevaOrden.connect();
		colaNuevaOrden.send(message);
		colaNuevaOrden.disconnect();
		
		//pone el pedido en el archivo de Orders (ID + "recibida")
		OrdersStorage orders = new OrdersStorage();
		orders.saveNewOrder(message.getID(),ConfigLoader.RECIBIDA);
		
		//manda el pedido a la cola de PROCESSING
		String processingQueue = conf.getProcessingQueueName();
		Queue<NewOrderMessage> colaProcesar = new Queue<NewOrderMessage>(processingQueue,this,this);
		colaProcesar.connect();
		colaProcesar.send(message);
		colaProcesar.disconnect();
	}

	@Override
	public NewOrderMessage transform(Object o) {
		return (NewOrderMessage) o;
	}
}
