package communication;
import java.io.Serializable;
import java.util.UUID;


public class OrderMessage extends Message implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private UUID orderId;

	public OrderMessage(UUID id) {
		orderId = id;
	}
	
	@Override
	public String toString() {
		return ("Order ID:  "+orderId+".");
	}

	public UUID getOrderId() {
		return orderId;
	}
}
