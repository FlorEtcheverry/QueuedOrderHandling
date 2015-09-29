import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;


public class Cliente implements QueueProcesser<NewOrderMessage>, 
								MessageTransformer<NewOrderMessage>
								{

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.out.println("Error, parametro: cantidad de ciclos.");
		}

		Queue<NewOrderMessage> colaPedidos = null;
		Queue<OrderMessage> colaQueries = null;
		
		try {
			ConfigLoader conf = ConfigLoader.getInstance();
			String pedidosQueue = conf.getOrdersQueueName();
			String queriesQueue = conf.getQueryStateQueueName();
			
			Cliente cliente = new Cliente();
			ClienteConsulta clienteConsulta = new ClienteConsulta();
			colaPedidos = 
					new Queue<NewOrderMessage>(pedidosQueue, cliente, cliente);
			colaQueries = new Queue<OrderMessage>(
												queriesQueue, 
												clienteConsulta, 
												clienteConsulta);
			
			//cargar ids
			UUIDsReader idRead = new UUIDsReader();
			ArrayList<UUID> ids = idRead.getIds();
			
			Random r = new Random();
			int vueltas = Integer.parseInt(args[0]);
			int maxT = conf.getMaxTipo();
			int maxC = conf.getMaxStock();
			
			colaPedidos.connect();
			colaQueries.connect();

			System.out.println(
					"Iniciado cliente. Creara "+vueltas+" de nuevas ordenes. "
					+ "Luego consultará por el estado de esas ordenes.");
			ArrayList<UUID> idsUSados = new ArrayList<UUID>();
			
			for (int i=0; i<vueltas;i++) {
				
				//manda pedidos
				UUID id = ids.get(i);
				idsUSados.add(i, id);
				
				int tipo = r.nextInt(maxT)+1;
				int cant = r.nextInt(maxC)+1;
				NewOrderMessage msg = new NewOrderMessage(id, tipo, cant);
				colaPedidos.send(msg);
			}
			
			//consulta
			Collections.shuffle(idsUSados);
			for (int i=0;i<vueltas;i++) {

				OrderMessage mes = new OrderMessage(idsUSados.get(i));
				colaQueries.send(mes);
				System.out.println(
						"Cliente envio consulta por ID: "+mes.getOrderId()+".");
				
			}		
			
			
		} catch (NumberFormatException e) {
			System.out.println("Parametro incorrecto.");
		} catch (IOException e) {
			System.out.println("Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("Error de la cola de mensajes.");
		} finally {
			if (colaPedidos != null)
				try {
					colaPedidos.disconnect();
				} catch (ColaException e) {
					System.out.println("No se pudo desconectar de la cola de"
							+ " pedidos.");
				}
			if (colaQueries != null)
				try {
					colaQueries.disconnect();
				} catch (ColaException e) {
					System.out.println("No se pudo desconectar de la cola de"
							+ " queries.");
				}
			System.out.println(
					"Finalizada simulacion de clientes. "
					+ "Desconectado de las colas.");
		}
	}

	@Override
	public NewOrderMessage transform(Object o) {
		return (NewOrderMessage) o;
	}

	@Override
	public void process(NewOrderMessage message) {
	}
}
