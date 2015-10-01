package communication;
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
	
	public byte[] getBytes() throws IOException {
		byte[] bytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.flush();
        oos.reset();
        bytes = baos.toByteArray();
        oos.close();
        baos.close();
        return bytes;
	}

	public static
	<TMessage extends Message> TMessage fromBytes(byte[] body) 
			throws IOException {
		
        TMessage mes = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(body);
            ObjectInputStream ois = new ObjectInputStream(bis);
            mes = (TMessage) ois.readObject();
            ois.close();
            bis.close();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new Error("MESSAGE CLASS NOT FOUND");
        }
        return mes;
    }
}
