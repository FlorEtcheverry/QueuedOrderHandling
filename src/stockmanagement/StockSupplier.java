package stockmanagement;
import java.io.IOException;

import common.ConfigLoader;
import communication.ColaException;
import communication.Queue;
import communication.QueueProcesser;
import communication.StockMessage;

public class StockSupplier implements QueueProcesser<StockMessage> {

	private StockStorage stock;
	private Queue<StockMessage> colaStock;
	
	private static class Quitter implements Runnable {
		
		private Queue<StockMessage> colaStock;
		private StockStorage stock;

		public Quitter(Queue<StockMessage> colaStock,StockStorage stock) {
			this.colaStock = colaStock;
			this.stock = stock;
		}
		
		@Override
		public void run(){
			if (colaStock != null) {
				try {
					colaStock.disconnect();
				} catch (ColaException e1) {
					System.out.println("					STOCK SUPPLIER - "
							+ "Error al desconectar cola de mensajes");
				}
			}
			try {
				stock.close();
			} catch (IOException e) {
				System.out.println("					STOCK SUPPLIER - "
						+ "Error al cerrar el archivo de stock");
			}
			System.out.println("Stock Supplier cerrado correctamente.");
		}
	}
	
	public static void main(String[] args) {

		StockSupplier stockSup = new StockSupplier();
		stockSup.stock = new StockStorage();
		try {
			//lee msj de TIPO y CANT de stock
			
			//conectarse a la cola
			ConfigLoader conf = ConfigLoader.getInstance();
			String stockQueue = conf.getAddStockQueueName();

			stockSup.colaStock = new Queue<StockMessage>(stockQueue,stockSup);
			
			stockSup.colaStock.connect();
			stockSup.colaStock.receive();
			
		} catch (IOException e) {
			System.out.println("					STOCK SUPPLIER - "
					+ "Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("					STOCK SUPPLIER - "
					+ "Error de la cola de mensajes.");
		} /*finally { FIXME
			if (colaStock != null) {
				try {
					colaStock.disconnect();
				} catch (ColaException e1) {
					System.out.println("					STOCK SUPPLIER - "
							+ "Error al desconectar cola de mensajes");
				}
			}
		} */
		Runtime.getRuntime().addShutdownHook(new Thread(
				new Quitter(
						stockSup.colaStock,
						stockSup.stock
						))); 
	}

	@Override
	public void process(StockMessage message) throws IOException {
		int tipo = message.getTipo();
		int cant = message.getCantidad();
		stock.sumarStock(tipo,cant);
	}
}
