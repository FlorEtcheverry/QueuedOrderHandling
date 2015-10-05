package simulacion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import common.ConfigLoader;
import communication.ColaException;
import communication.OrderMessage;
import communication.Queue;


public class Empleado {

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println(
					"Error, parametros: cantidad de ciclos y tiempo sleep.");
		}
		
		int vueltas = Integer.parseInt(args[0]);
		int time = Integer.parseInt(args[1]);
		System.out.println(
				"Iniciado un EMPLEADO. Va a entregar "+vueltas+
				"pedido(s) y dormirá "+time+" milisegundos.");
		
		try {
			//cargar ids
			UUIDsReader idRead = new UUIDsReader();
			ArrayList<UUID> ids = idRead.getIds();
			
			ConfigLoader conf = ConfigLoader.getInstance();
			String deliverQueue = conf.getUpdateStateQueueName();
			Queue<OrderMessage> colaEntregar = 
					new Queue<OrderMessage>(deliverQueue, 
											new NullProcesser<OrderMessage>());
			
			colaEntregar.connect();

			//manda consulta
			for (int i=0; i<vueltas;i++){
				Thread.sleep(time);
				
				//crear pedido
				UUID id = ids.get(i);
				
				//crear mensaje
				OrderMessage msg = new OrderMessage(id);
				
				//envia mensaje con el ID
				colaEntregar.send(msg);	
				System.out.println("EMPLEADO consultó por ID: "+id+".");
			}
			colaEntregar.disconnect();
			System.out.println(
					"Finalizado un empleado. Desconectado de la cola.");
			
		} catch (NumberFormatException e) {
			System.out.println("							EMPLEADO - "
					+ "Parametro incorrecto.");
		} catch (IOException e) {
			System.out.println("							EMPLEADO - "
					+ "Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("							EMPLEADO - "
					+ "Error de la cola de mensajes.");
		} catch (InterruptedException e) {
			System.out.println("							EMPLEADO - "
					+ "Error en el sleep del empleado.");
		}
	}
}
