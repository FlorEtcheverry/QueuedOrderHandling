import java.io.IOException;


public interface QueueProcesser <TMessage extends Message> {

	public void process(TMessage message) throws IOException, ColaException;
}
