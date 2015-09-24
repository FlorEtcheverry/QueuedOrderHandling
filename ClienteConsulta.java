import java.io.IOException;


public class ClienteConsulta implements QueueProcesser<OrderMessage>,
										MessageTransformer<OrderMessage> {
	
	@Override
	public OrderMessage transform(Object o) {
		return (OrderMessage) o;
	}

	@Override
	public void process(OrderMessage message) throws IOException {
	}

	

}
