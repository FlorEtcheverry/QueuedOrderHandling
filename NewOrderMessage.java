import java.io.Serializable;
import java.util.UUID;


public class NewOrderMessage extends Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; //TODO
	
	private UUID id;
	private int tipo_prod;
	private int cantidad_pedida;
	
	public NewOrderMessage(UUID id,int tipo,int cant) {
		this.id = id;
		tipo_prod = tipo;
		cantidad_pedida = cant;
	}
	
	public UUID getID() {
		return id;
	}
	
	public int getTipo() {
		return tipo_prod;
	}
	
	public int getCantidad() {
		return cantidad_pedida;
	}
	
	@Override
	public String toString() {
		return ("ID: "+id.toString()+" - Tipo Prod: "+tipo_prod+" - Cantidad: "+cantidad_pedida+".");
	}
}
