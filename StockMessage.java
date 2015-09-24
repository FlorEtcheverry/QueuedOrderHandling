import java.io.Serializable;


public class StockMessage extends Message implements Serializable {

	private static final long serialVersionUID = 1L;
	
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
		return ("Tipo Prod: "+tipo_prod+" - Cantidad a Aumentar: "+
													cantidad_a_aumentar+".");
	}
}
