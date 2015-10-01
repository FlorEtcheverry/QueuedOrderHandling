package simulacion;

import java.io.IOException;

import communication.OrderMessage;
import communication.QueueProcesser;


public class ClienteConsulta implements QueueProcesser<OrderMessage> {

	@Override
	public void process(OrderMessage message) throws IOException {
	}

	

}
