package utilities;

/**
 * Clase para almacenar las constantes usadas en la aplicación.
 * 
 * @author Ignacio Pineda Martín
 */
public class Constants {
  
    /**
     * <p>Modos válidos de selección para los {@link JPanel} de la aplicación.</p>
     * 
     * <br>{@code CRUD} Modo por defecto. Permite las altas, bajas, consultas y
     * modificaciones.
     * <br>{@code SELECTION} Permite las consultas.
     * <br>{@code READ_ONLY} Permite sólo la visualización de los datos.
     */
    public static enum PanelMode {
        CRUD,
        SELECTION,
        READ_ONLY
    }
    
    /**
     * <p>
     * Modos válidos de actuación para los {@link JFrame} de la aplicación
     * relacionados con el tratamiento de productos (Cuentas Corrientes,
     * Tarjetas de Crédito y Préstamos.</p>
     *
     * <br>{@code NEW} Indica el modo de alta de un nuevo producto.
     * <br>{@code CANCEL} Indica el modo de cancelación de un producto ya
     * existente.
     * <br>{@code MODIFY} Indica el modo de modificación (limitado) de un
     * producto ya existente.
     * <br>{@code MOVEMENT} Indica el modo de altas, bajas y modificaciones de
     * movimientos asociados al producto.
     * <br>{@code QUERY} Indica el modo de consulta de un producto ya existente,
     * incluyendo sus movimientos.
     */
    public static enum FormAction {
        NEW,
        CANCEL,
        MODIFY,
        MOVEMENT,
        QUERY
    }

    /**
     * <p>
     * Modos válidos de generación de documento en formato Pdf.</p>
     *
     * <br>{@code LIQUIDATION} Indica el modo para generar un extracto de
     * liquidación de producto.
     * <br>{@code MOVEMENT} Indica el modo para generar un extracto de
     * movimientos del producto.
     */
    public static enum PdfType {
        LIQUIDATION,
        MOVEMENT
    }
    
    /**
     * <p>Directorio donde se crearán ficheros de salida (PDF, LOG).
     */
    public static final String DEST_PATH = "results/prod/";
    
    /**
     * <p> Código bancario asignado a SoftBank. Necesario para el cálculo
     * de CCC e IBAN.
     */
    public static final String COD_BANCO_SOFTBANK = "0138";
    
    /**
     * <p>
     * Código bancario asignado a SoftBank. Necesario para el cálculo de CCC e
     * IBAN.
     */
    public static final String COD_PAIS_SOFTBANK = "ES";
    
    /**
     * Constructor por defecto
     */
    public Constants() {
        
    } 
    
}
