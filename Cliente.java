import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;


public class Cliente implements QueueProcesser<NewOrderMessage>, 
								MessageTransformer<NewOrderMessage>
								{

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.out.println("Error, parametro: cantidad de ciclos.");
		}

		try {
			ConfigLoader conf = ConfigLoader.getInstance();
			String pedidosQueue = conf.getOrdersQueueName();
			String queriesQueue = conf.getQueryStateQueueName();
			
			Cliente cliente = new Cliente();
			ClienteConsulta clienteConsulta = new ClienteConsulta();
			Queue<NewOrderMessage> colaPedidos = 
					new Queue<NewOrderMessage>(pedidosQueue, cliente, cliente);
			Queue<OrderMessage> colaQueries = 
					new Queue<OrderMessage>(
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
			int contador = 0;
			
			for (int i=0; i<vueltas;i++){
				
				//manda pedidos
				for (int j=0; j<vueltas;j++){
					//crear pedido
					UUID id = ids.get(contador++);
					int tipo = r.nextInt(maxT)+1;
					int cant = r.nextInt(maxC)+1;
					
					//crear mensaje
					NewOrderMessage msg = new NewOrderMessage(id, tipo, cant);
					colaPedidos.send(msg);
				}
				
				//consulta
				OrderMessage mes = 
						new OrderMessage(ids.get(r.nextInt(ids.size())));
				colaQueries.send(mes);
				
			}		
	
			colaPedidos.disconnect();
			colaQueries.disconnect();
			
		} catch (NumberFormatException e) {
			System.out.println("Parametro incorrecto.");
		} catch (IOException e) {
			System.out.println("Error al leer de archivo.");
		} catch (ColaException e) {
			System.out.println("Error de la cola de mensajes.");
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
