import java.io.IOException;


public class StockController implements QueueProcesser<NewOrderMessage>, MessageTransformer<NewOrderMessage> {

	public static void main(String[] args) {
		//lee de la cola pedido: ID + TIPO + CANTIDAD
		
		Queue<NewOrderMessage> colaProcessing = null;
		try {
			ConfigLoader conf = ConfigLoader.getInstance();
			String processingQueue = conf.getProcessingQueueName();

			StockController stockController = new StockController();
			colaProcessing = new Queue<NewOrderMessage>(processingQueue,stockController,stockController);
			
			colaProcessing.connect();
			colaProcessing.recieve();
		} catch (IOException e) {
			System.out.println("Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("Error de la cola de mensajes.");
			if (colaProcessing != null) {
				try {
					colaProcessing.disconnect();
				} catch (ColaException e1) {
					System.out.println("Error al desconectar cola de mensajes.");
				}
			}
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
