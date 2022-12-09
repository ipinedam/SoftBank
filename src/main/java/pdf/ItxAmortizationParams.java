package pdf;

/**
 * Clase para el paso de información a la clase generador de cuadros de
 * amortización en formato PDF.
 *
 * @author Ignacio Pineda Martín
 */
public class ItxAmortizationParams {

    private double impPrestamo;
    private long duracionMeses;
    private double tipoInteres;

    /**
     * Constructor por defecto.
     */
    public ItxAmortizationParams() {
    }

    /**
     * Constructor con todos los parámetros
     * @param impPrestamo Importe del préstamo.
     * @param duracionMeses Duración del préstamo, en meses.
     * @param tipoInteres Tipo de interes del préstamo.
     */
    public ItxAmortizationParams(double impPrestamo, long duracionMeses, double tipoInteres) {
        this.impPrestamo = impPrestamo;
        this.duracionMeses = duracionMeses;
        this.tipoInteres = tipoInteres;
    }

    public double getImpPrestamo() {
        return impPrestamo;
    }

    public void setImpPrestamo(double impPrestamo) {
        this.impPrestamo = impPrestamo;
    }

    public long getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(long duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    public double getTipoInteres() {
        return tipoInteres;
    }

    public void setTipoInteres(double tipoInteres) {
        this.tipoInteres = tipoInteres;
    }
  
}
