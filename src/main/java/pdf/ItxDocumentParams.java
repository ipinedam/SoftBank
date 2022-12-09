package pdf;

import java.util.Date;
import java.util.List;
import model.entity.Movimiento;
import model.entity.ProductoBancario;
import utilities.Constants.PdfType;

/**
 * Clase para el paso de información a la clase generador de documentos PDF.
 *
 * @author Ignacio Pineda Martín
 */
public class ItxDocumentParams {

    private PdfType pdfType;
    private ProductoBancario productoBancario;
    private List<Movimiento> lstMovimiento;
    private Date fecLiquidacion;
    private double impSaldoCobrar;
    private double impSaldoCobrado;
    private double impIntereses;

    /**
     * Constructor para la generación de extractos de movimientos.
     *
     * @param pdfType En este caso, debe ser {@link PdfType.MOVEMENT}
     * @param productoBancario Los datos del producto a generar el extracto.
     * @param lstMovimiento Lista de movimientos a mostrar.
     */
    public ItxDocumentParams(PdfType pdfType, ProductoBancario productoBancario, List<Movimiento> lstMovimiento) {
        this.pdfType = pdfType;
        this.productoBancario = productoBancario;
        this.lstMovimiento = lstMovimiento;
    }

    /**
     * Constructor para la generación de informes de liquidación.
     *
     * @param pdfType En este caso, debe ser {@link PdfType.LIQUIDATION}
     * @param productoBancario Los datos del producto cuya liquidación se ha
     * generado.
     * @param lstMovimiento Lista de movimientos a mostrar.
     * @param fecLiquidacion Fecha a la que se ha realizado la liquidación.
     * @param impSaldoCobrar Importe que debería haberse cobrado.
     * @param impSaldoCobrado Importe realmente cobrado en la cuenta corriente.
     * @param impIntereses Importe de intereses calculados en la liquidación.
     */
    public ItxDocumentParams(PdfType pdfType, ProductoBancario productoBancario, List<Movimiento> lstMovimiento, Date fecLiquidacion, double impSaldoCobrar, double impSaldoCobrado, double impIntereses) {
        this.pdfType = pdfType;
        this.productoBancario = productoBancario;
        this.lstMovimiento = lstMovimiento;
        this.fecLiquidacion = fecLiquidacion;
        this.impSaldoCobrar = impSaldoCobrar;
        this.impSaldoCobrado = impSaldoCobrado;
        this.impIntereses = impIntereses;
    }

    public PdfType getPdfType() {
        return pdfType;
    }

    public void setPdfType(PdfType pdfType) {
        this.pdfType = pdfType;
    }

    public ProductoBancario getProductoBancario() {
        return productoBancario;
    }

    public void setProductoBancario(ProductoBancario productoBancario) {
        this.productoBancario = productoBancario;
    }

    public List<Movimiento> getLstMovimiento() {
        return lstMovimiento;
    }

    public void setLstMovimiento(List<Movimiento> lstMovimiento) {
        this.lstMovimiento = lstMovimiento;
    }

    public Date getFecLiquidacion() {
        return fecLiquidacion;
    }

    public void setFecLiquidacion(Date fecLiquidacion) {
        this.fecLiquidacion = fecLiquidacion;
    }

    public double getImpSaldoCobrar() {
        return impSaldoCobrar;
    }

    public void setImpSaldoCobrar(double impSaldoCobrar) {
        this.impSaldoCobrar = impSaldoCobrar;
    }

    public double getImpSaldoCobrado() {
        return impSaldoCobrado;
    }

    public void setImpSaldoCobrado(double impSaldoCobrado) {
        this.impSaldoCobrado = impSaldoCobrado;
    }

    public double getImpIntereses() {
        return impIntereses;
    }

    public void setImpIntereses(double impIntereses) {
        this.impIntereses = impIntereses;
    }

}
