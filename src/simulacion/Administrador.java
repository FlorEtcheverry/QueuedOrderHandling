package simulacion;

import java.io.IOException;

import common.ConfigLoader;
import communication.ColaException;
import communication.Queue;
import communication.QueueProcesser;
import communication.StockMessage;


public class Administrador implements QueueProcesser<StockMessage> {

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
					new Queue<StockMessage>(stockQueue, admin);

			int tipo = Integer.parseInt(args[0]);
			int cant = Integer.parseInt(args[1]);
			
			colaAgregarStock.connect();
					
			//crear mensaje
			StockMessage msg = new StockMessage(tipo, cant);
			
			//manda el msj: TIPO + CANTIDAD (a agregar)
			colaAgregarStock.send(msg);
			System.out.println(
					"ADMINISTRADOR envio msj de aumentar stock de tipo "+tipo+
					" en "+cant+" unidades.");
			
			colaAgregarStock.disconnect();
			System.out.println(
					"ADMINISTRADOR finalizado. Desconectado de la cola");
			
		} catch (NumberFormatException e) {
			System.out.println("ADMINISTRADOR - Parametro incorrecto.");
		} catch (IOException e) {
			System.out.println("ADMINISTRADOR - Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("ADMINISTRADOR - Error de la cola de mensajes.");
		}
	}

	@Override
	public void process(StockMessage message) {
	}
}
