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
	
	public Queue(String name, QueueProcesser<TMessage> rcv, MessageTransformer<TMessage> trn) {
		this.queueName = name;
		this.reciever = rcv;
		this.transformer = trn;
	}

	public void connect() {
		
		ConfigLoader conf = ConfigLoader.getInstance();
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(conf.getHost());
		
	    try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			
			//true por durable
			channel.queueDeclare(queueName, true, false, false, null);
			
			//not to give more than one message to a worker at a time
			channel.basicQos(1);
			
			System.out.println("cola conectada");

			  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void recieve() {
		
		System.out.println("por recibir");
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
		    	System.out.println("msj recibido: "+msj_recibido.toString());
		    	try { //TODO ???
		    		reciever.process(msj_recibido);
	    		} finally {
	    			//ack de msj procesado
	    			System.out.println("procesado");
	    			channel.basicAck(envelope.getDeliveryTag(), false);
	    		}
		    }
	    };
	    //lee pedido de la cola
	    try {
	    	//queue,autoACK,consumer
	    	System.out.println("por consumir");
			channel.basicConsume(queueName, false, consumer);
			System.out.println("consumido");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(TMessage msg) {
		//mandar msj
		try {
			channel.basicPublish(
					"",
					queueName,
					MessageProperties.PERSISTENT_TEXT_PLAIN,
					msg.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("msj mandado: "+msg.toString());

	}
	
	public void disconnect() {
		try {
			channel.close();
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
