package simulacion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

import common.ConfigLoader;
import communication.ColaException;
import communication.NewOrderMessage;
import communication.OrderMessage;
import communication.Queue;


public class Cliente {

	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.out.println("Error, parametros: cantidad de ciclos y sleep");
			System.exit(0);
		}

		Queue<NewOrderMessage> colaPedidos = null;
		Queue<OrderMessage> colaQueries = null;
		try {
			ConfigLoader conf = ConfigLoader.getInstance();
			String pedidosQueue = conf.getOrdersQueueName();
			String queriesQueue = conf.getQueryStateQueueName();
			
			colaPedidos = 
					new Queue<NewOrderMessage>(
							pedidosQueue, 
							new NullProcesser<NewOrderMessage>());
			colaQueries = new Queue<OrderMessage>(
							queriesQueue, 
							new NullProcesser<OrderMessage>());
			
			//cargar ids
			UUIDsReader idRead = new UUIDsReader();
			ArrayList<UUID> ids = idRead.getIds();
			
			Random r = new Random();
			int vueltas = Integer.parseInt(args[0]);
			long time = Long.parseLong(args[1]);
			int maxT = conf.getMaxTipo();
			int maxC = conf.getMaxStock();
			
			colaPedidos.connect();
			colaQueries.connect();

			System.out.println(
					"Iniciado CLIENTE. Creará "+vueltas+" de nuevas ordenes. "
							+ "Luego dormirá "+time
							+"milisegundos y consultará por "
							+ "el estado de esas ordenes.");
			ArrayList<UUID> idsUsados = new ArrayList<UUID>();
			
			for (int i=0; i<vueltas;i++) {
				
				//manda pedidos
				UUID id = ids.get(i);
				idsUsados.add(i, id);
				
				int tipo = r.nextInt(maxT)+1;
				int cant = r.nextInt(maxC)+1;
				NewOrderMessage msg = new NewOrderMessage(id, tipo, cant);
				colaPedidos.send(msg);
			}
			Thread.sleep(time);
			
			//consulta 
			Collections.shuffle(idsUsados);
			for (int i=0;i<vueltas;i++) {

				OrderMessage mes = new OrderMessage(idsUsados.get(i));
				colaQueries.send(mes);
				/*System.out.println(
					"CLIENTE envio consulta por ID: "+mes.getOrderId()+".");*/
			}
		} catch (NumberFormatException e) {
			System.out.println("						CLIENTE - "
					+ "Parametro incorrecto.");
		} catch (IOException e) {
			System.out.println("						CLIENTE - "
					+ "Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("						CLIENTE - "
					+ "Error de la cola de mensajes.");
		} catch (InterruptedException e) {
			System.out.println("						CLIENTE - "
					+ "Error en el sleep - Interrupted.");
		} finally {
			if (colaPedidos != null)
				try {
					colaPedidos.disconnect();
				} catch (ColaException e) {
					System.out.println("						CLIENTE - "
							+ "No se pudo desconectar de la cola de pedidos.");
				}
			if (colaQueries != null)
				try {
					colaQueries.disconnect();
				} catch (ColaException e) {
					System.out.println("						CLIENTE - "
							+ "No se pudo desconectar de la cola de queries.");
				}
			System.out.println(
					"Finalizada simulacion de clientes. "
					+ "Desconectado de las colas.");
		}
	}
}
