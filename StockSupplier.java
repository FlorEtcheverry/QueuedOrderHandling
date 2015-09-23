import java.io.IOException;


public class StockSupplier implements QueueProcesser<StockMessage>, MessageTransformer<StockMessage> {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Queue<StockMessage> colaStock = null;
		try {
			//lee msj de TIPO y CANT de stock
			
			//conectarse a la cola
			ConfigLoader conf = ConfigLoader.getInstance();
			String stockQueue = conf.getAddStockQueueName();

			StockSupplier stockSupplier = new StockSupplier();
			colaStock = new Queue<StockMessage>(stockQueue,stockSupplier,stockSupplier);
			
			colaStock.connect();
			colaStock.receive();
		} catch (IOException e) {
			System.out.println("Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("Error de la cola de mensajes.");
			if (colaStock != null) {
				try {
					colaStock.disconnect();
				} catch (ColaException e1) {
					System.out.println("Error al desconectar cola de mensajes.");
				}
			}
		}
	}

	@Override
	public StockMessage transform(Object o) {
		return (StockMessage) o;
	}

	@Override
	public void process(StockMessage message) throws IOException {
		// TODO Auto-generated method stub
		int tipo = message.getTipo();
		int cant = message.getCantidad();
		
		StockStorage stock = new StockStorage();
		stock.sumarStock(tipo,cant);
		
	}

}
