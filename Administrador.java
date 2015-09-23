import java.io.IOException;


public class Administrador implements QueueProcesser<StockMessage>, 
									MessageTransformer<StockMessage> {

	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.out.println(
					"Error, parametros: tipo prod, stock a agregar.");
		}
		//para un TIPO de producto, aumenta en una CANTIDAD determin el stock
		
		try {
			ConfigLoader conf = ConfigLoader.getInstance();
			String stockQueue = conf.getAddStockQueueName();
			
			Administrador admin = new Administrador();
			Queue<StockMessage> colaAgregarStock = 
					new Queue<StockMessage>(stockQueue, admin, admin);

			int tipo = Integer.parseInt(args[0]);
			int cant = Integer.parseInt(args[1]);
			
			colaAgregarStock.connect();
					
			//crear mensaje
			StockMessage msg = new StockMessage(tipo, cant);
			
			//manda el msj: TIPO + CANTIDAD (a agregar)
			colaAgregarStock.send(msg);
			colaAgregarStock.disconnect();
			
		} catch (NumberFormatException e) {
			System.out.println("Parametro incorrecto.");
		} catch (IOException e) {
			System.out.println("Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("Error de la cola de mensajes.");
		}
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
