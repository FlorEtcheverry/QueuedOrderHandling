import java.io.IOException;


public class StockController implements QueueProcesser<NewOrderMessage>, MessageTransformer<NewOrderMessage> {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//lee de la cola pedido: ID + TIPO + CANTIDAD
		
		ConfigLoader conf = ConfigLoader.getInstance();
		String processingQueue = conf.getProcessingQueueName();

		StockController stockController = new StockController();
		Queue<NewOrderMessage> colaProcessing = new Queue<NewOrderMessage>(processingQueue,stockController,stockController);
		
		colaProcessing.connect();
		try {
			while (true)
				colaProcessing.recieve();
		} finally {
			colaProcessing.disconnect();
		}
	}

	@Override
	public NewOrderMessage transform(Object o) {
		return (NewOrderMessage) o;
	}

	@Override
	public void process(NewOrderMessage message) throws IOException {
		int tipo = message.getTipo();
		int cant = message.getCantidad();
		
		StockStorage stock = new StockStorage();
		OrdersStorage orders = new OrdersStorage();
		
		boolean restado = stock.restarStock(tipo,cant);
		if (restado) {
			//escribe "aceptada" en el archivo de ordenes, para ese ID
			orders.changeOrderState(message.getID(),ConfigLoader.ACEPTADA);
		} else {
			//escribe "rechazada" en el archivo de ordenes, para ese ID
			orders.changeOrderState(message.getID(),ConfigLoader.RECHAZADA);
		}
		
	}

}
