package stockmanagement;

import java.io.IOException;

import common.ConfigLoader;
import communication.ColaException;
import communication.Queue;
import communication.QueueProcesser;
import communication.StockMessage;


public class StockSupplier implements QueueProcesser<StockMessage> {

	public static void main(String[] args) {

		Queue<StockMessage> colaStock = null;
		try {
			//lee msj de TIPO y CANT de stock
			
			//conectarse a la cola
			ConfigLoader conf = ConfigLoader.getInstance();
			String stockQueue = conf.getAddStockQueueName();

			StockSupplier stockSup = new StockSupplier();
			colaStock = new Queue<StockMessage>(stockQueue,stockSup);
			
			colaStock.connect();
			colaStock.receive();
			
		} catch (IOException e) {
			System.out.println("					STOCK SUPPLIER - "
					+ "Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("					STOCK SUPPLIER - "
					+ "Error de la cola de mensajes.");
		} finally {
			if (colaStock != null) {
				try {
					colaStock.disconnect();
				} catch (ColaException e1) {
					System.out.println("					STOCK SUPPLIER - "
							+ "Error al desconectar cola de mensajes");
				}
			}
		}
	}

	@Override
	public void process(StockMessage message) throws IOException {
		int tipo = message.getTipo();
		int cant = message.getCantidad();
		
		StockStorage stock = new StockStorage();
		stock.sumarStock(tipo,cant);
		
	}

}