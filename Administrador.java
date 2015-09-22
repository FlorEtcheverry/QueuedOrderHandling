
public class Administrador implements QueueProcesser<StockMessage>, MessageTransformer<StockMessage> {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//para un TIPO de producto, aumenta en una CANTIDAD determinada el stock ---ARGS?? TODO
		int tipo = 1; //TODO
		int cant = 100; //TODO
		
		//crear mensaje
		StockMessage msg = new StockMessage(tipo, cant);
		
		//manda el msj: TIPO + CANTIDAD (a agregar)
		ConfigLoader conf = ConfigLoader.getInstance();
		String stockQueue = conf.getAddStockQueueName();
		
		Administrador admin = new Administrador();
		Queue<StockMessage> colaAgregarStock = new Queue<StockMessage>(stockQueue, admin, admin);
		
		colaAgregarStock.connect();
		colaAgregarStock.send(msg);
		colaAgregarStock.disconnect();
	}

	@Override
	public StockMessage transform(Object o) {
		return (StockMessage) o;
	}

	@Override
	public void process(StockMessage message) {
		// TODO Auto-generated method stub
		
	}
}
