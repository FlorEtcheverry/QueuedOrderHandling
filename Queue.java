import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;


public class Queue <TMessage extends Message> {

	private String queueName;
	private QueueProcesser<TMessage> reciever;
	private MessageTransformer<TMessage> transformer;
	private Connection connection;
	private Channel channel;
	
	public Queue(
			String name, 
			QueueProcesser<TMessage> rcv, 
			MessageTransformer<TMessage> trn) 
	{
		this.queueName = name;
		this.reciever = rcv;
		this.transformer = trn;
	}

	public void connect() throws ColaException {
		
		try {
			ConfigLoader conf = ConfigLoader.getInstance();
			ConnectionFactory factory = new ConnectionFactory();
			factory.setConnectionTimeout(5000);
			factory.setHost(conf.getHost());
			
			connection = factory.newConnection();
			channel = connection.createChannel();
			
			//true por durable
			channel.queueDeclare(queueName, true, false, false, null);
			
			//not to give more than one message to a worker at a time
			channel.basicQos(1);  
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
			throw new ColaException();
		}
	}
	
	public void receive() throws ColaException {
		
		//defino qué hacer al consumir
		Consumer consumer = new DefaultConsumer(channel) {
		    @Override
		    public void handleDelivery(
		    		String consumerTag, 
		    		Envelope envelope, 
		    		AMQP.BasicProperties properties, 
		    		byte[] body
		    		) throws IOException {
		    	
		    	TMessage msj_recibido = Message.fromBytes(body, transformer);
		    	try {
		    		reciever.process(msj_recibido);
	    		} catch (ColaException e) {
					System.out.println("Error al procesar el mensaje");
					e.printStackTrace();
				} finally {
	    			//ack de msj procesado
	    			channel.basicAck(envelope.getDeliveryTag(), false);
	    		}
		    }
	    };
	    //lee pedido de la cola
	    try {
	    	//queue,autoACK,consumer
			channel.basicConsume(queueName, false, consumer);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ColaException();
		}
	}
	
	public void send(TMessage msg) throws ColaException {
		//mandar msj
		try {
			channel.basicPublish(
					"",
					queueName,
					MessageProperties.PERSISTENT_TEXT_PLAIN,
					msg.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ColaException();
		}
	}
	
	public void disconnect() throws ColaException {
		try {
			channel.close();
			connection.close();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
			throw new ColaException();
		}
	}
}
