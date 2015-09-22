import java.io.Serializable;


public class StockMessage extends Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; //TODO
	
	private int tipo_prod;
	private int cantidad_a_aumentar;
	
	public StockMessage(int tipo,int cant) {
		tipo_prod = tipo;
		cantidad_a_aumentar = cant;
	}
	
	public int getTipo() {
		return tipo_prod;
	}
	
	public int getCantidad() {
		return cantidad_a_aumentar;
	}
	
	@Override
	public String toString() {
		return ("Tipo Prod: "+tipo_prod+" - Cantidad a Aumentar: "+cantidad_a_aumentar+".");
	}
	
	/*public byte[] getBytes() {
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

    public static StockMessage fromBytes(byte[] body) {
        StockMessage obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(body);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = (StockMessage) ois.readObject();
            ois.close();
            bis.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }*/

}
