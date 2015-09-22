import java.io.IOException;


public class StockSupplier implements QueueProcesser<StockMessage>, MessageTransformer<StockMessage> {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//lee msj de TIPO y CANT de stock
		
		//conectarse a la cola
		ConfigLoader conf = ConfigLoader.getInstance();
		String stockQueue = conf.getAddStockQueueName();

		StockSupplier stockSupplier = new StockSupplier();
		Queue<StockMessage> colaStock = new Queue<StockMessage>(stockQueue,stockSupplier,stockSupplier);
		
		colaStock.connect();
		colaStock.recieve();
		colaStock.disconnect();
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
