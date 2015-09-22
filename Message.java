import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public abstract class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public abstract String toString();
	
	public byte[] getBytes() {
		byte[] bytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.flush();
            oos.reset();
            bytes = baos.toByteArray();
            oos.close();
            baos.close();
        } catch(IOException e){
        	bytes = new byte[] {}; //TODO
        }
        return bytes;
	}

	public static
	<TMessage extends Message,
	TTransformer extends MessageTransformer<TMessage>> TMessage fromBytes(
			byte[] body, 
			TTransformer transformer) {
        TMessage mes = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(body);
            ObjectInputStream ois = new ObjectInputStream(bis);
            mes = transformer.transform(ois.readObject());
            ois.close();
            bis.close();
        }
        catch (IOException e) { //TODO EXC!!
            e.printStackTrace();
        }
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return mes;
    }
}
