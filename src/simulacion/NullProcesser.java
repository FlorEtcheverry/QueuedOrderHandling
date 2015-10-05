package simulacion;

import java.io.IOException;

import communication.ColaException;
import communication.Message;
import communication.QueueProcesser;

public class NullProcesser <TMessage extends Message> 
							implements QueueProcesser<TMessage> {

	@Override
	public void process(TMessage message) throws IOException, ColaException {
		//nada
	}

}
