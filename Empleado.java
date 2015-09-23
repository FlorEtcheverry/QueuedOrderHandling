import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;


public class Empleado implements QueueProcesser<OrderMessage>, 
								MessageTransformer<OrderMessage> {

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println(
					"Error, parametros: cantidad de ciclos y tiempo sleep.");
		}
		
		int vueltas = Integer.parseInt(args[0]);
		int time = Integer.parseInt(args[1]);
		
		try {
			//cargar ids
			UUIDsReader idRead = new UUIDsReader();
			ArrayList<UUID> ids = idRead.getIds();
			
			Empleado empleado = new Empleado();
			ConfigLoader conf = ConfigLoader.getInstance();
			String deliverQueue = conf.getUpdateStateQueueName();
			Queue<OrderMessage> colaEntregar = 
					new Queue<OrderMessage>(deliverQueue, empleado, empleado);
			
			colaEntregar.connect();
			System.out.println("Lista: " + ids.size() );
			//manda consulta
			for (int i=0; i<vueltas;i++){
				//crear pedido
				UUID id = ids.get(i);
				
				//crear mensaje
				OrderMessage msg = new OrderMessage(id);
				
				//envia mensaje con el ID
				colaEntregar.send(msg);	
				
				Thread.sleep(time);
			}
			
			colaEntregar.disconnect();
			
		} catch (NumberFormatException e) {
			System.out.println("Parametro incorrecto.");
		} catch (IOException e) {
			System.out.println("Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("Error de la cola de mensajes.");
		} catch (InterruptedException e) {
			System.out.println("Error en el sleep del empleado.");
		}
	}

	@Override
	public OrderMessage transform(Object o) {
		return null;
	}

	@Override
	public void process(OrderMessage message) {
		
	}

}
