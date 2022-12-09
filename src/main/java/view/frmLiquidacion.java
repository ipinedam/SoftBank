package view;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.swing.ImageIcon;

import model.DAO.CatalogoDAO;
import model.DAO.MovimientoDAO;
import model.DAO.PrestamoDAO;
import model.DAO.ProductoBancarioDAO;
import model.DAO.TarjetaCreditoDAO;

import model.entity.Catalogo;
import model.entity.Movimiento;
import model.entity.Prestamo;
import model.entity.ProductoBancario;
import model.entity.TarjetaCredito;

import pdf.ItxDocument;
import pdf.ItxDocumentParams;

import utilities.Constants;
import utilities.Constants.PdfType;
import utilities.DBConnection;
import utilities.Functions;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
public class frmLiquidacion extends javax.swing.JFrame {
    
    private EntityManagerFactory emf;
    
    private Catalogo catalogo;
    
    private CatalogoDAO cdao;
    private MovimientoDAO mdao;
    private ProductoBancarioDAO pbdao;
    private PrestamoDAO pdao;
    private TarjetaCreditoDAO tdao;
    
    private boolean finProceso;
    
    /**
     * Creates new form frmLiquidacion
     */
    public frmLiquidacion() {
        initComponents();
        // Posicionamos el formulario en el centro de la pantalla.
        this.setLocationRelativeTo(null);        
        // Creamos el EntityManagerFactory.
        emf = new DBConnection().getEntityManagerFactory();
        // Creamos los DAO's con su EntityManagerFactory.
        cdao = new CatalogoDAO(emf);
        mdao = new MovimientoDAO(emf);
        pbdao = new ProductoBancarioDAO(emf);
        pdao = new PrestamoDAO(emf);
        tdao = new TarjetaCreditoDAO(emf);
        // Por defecto, estaremos haciendo liquidación de tarjetas de crédito.
        this.catalogo = cdao.getTarjetaCredito();
        // Título del formulario.
        this.setTitle("Liquidación de Tarjetas de Crédito");        
        // La máxima fecha de selección es el final de mes actual.
        jdcFecLiquidacion.setMaxSelectableDate(Functions.getEndOfMonth());
        // Por defecto, ponemos como fecha de liquidación la del final del mes 
        // anterior a la fecha en la que estamos.
        jdcFecLiquidacion.setDate(Functions.getDateFromLocalDate(LocalDate.now().withDayOfMonth(1).minusDays(1)));
        // Calculamos 1 año antes de la fecha actual y la asignamos como 
        // fecha mínima de fecha de liquidación.
        jdcFecLiquidacion.setMinSelectableDate(Functions.getDateFromLocalDate(LocalDate.now().minusYears(1)));
        // Inicialiamos variable de fin de proceso.
        finProceso = false;
    }
    
    public frmLiquidacion(Catalogo catalogo) {
        this();
        this.catalogo = catalogo;
        if (catalogo.equals(cdao.getTarjetaCredito())) {
            // Título del formulario.
            this.setTitle("Liquidación de Tarjetas de Crédito");
        }      
        if (catalogo.equals(cdao.getPrestamo())) {
            // Título del formulario.
            this.setTitle("Liquidación de Préstamos");
        } 
    }
    
    /**
     * Método para la liquidación de tarjetas de crédito.
     */
    private void liquidacionTarjetas() {
        // Variables auxiliares para el proceso de liquidación.
        Date fecLiquidacion = jdcFecLiquidacion.getDate();
        Date fecLiquidacionAnterior = null;
        double intereses = 0;
        double saldoPendiente = 0;
        double totalDeuda = 0;
        double saldoCobrar = 0;
        double saldoCCCobro = 0;
        double saldoCobrado = 0;
        int i = pbProgreso.getMinimum();

        // Marcamos comienzo del proceso.
        showMessage("*** Inicio liquidación de Tarjetas de Crédito ***");
        showMessage("Fecha de liquidación: " + Functions.formatDate(fecLiquidacion));
        pbProgreso.setValue(i);

        // Capturamos todas las tarjetas a liquidar.
        List<TarjetaCredito> lstTarjetaCredito = tdao.buscarTarjetaCredito(fecLiquidacion);
        // Mensaje informado de que no hay tarjetas para liquidar.
        // ¿Proceso de liquidación ya realizado?.
        if (lstTarjetaCredito.isEmpty()) {
            showMessage("No hay tarjetas de crédito pendientes de liquidar a fecha " + Functions.formatDate(fecLiquidacion));
        }
        // Bucle de liquidación.
        for (TarjetaCredito tc : lstTarjetaCredito) {
            // Guardamos el producto bancario asociado a la tarjeta.
            ProductoBancario pb = tc.getProductoBancario();
            
            // Mensaje de liquidación.
            showMessage(String.format("Liquidando tarjeta %s - %s %s",
                    tc.getNumeroTarjeta(),
                    pb.getClienteList().get(0).getNombreCliente(),
                    pb.getClienteList().get(0).getApellidosCliente()));
            
            // Calculamos la fecha  de liquidación anterior.
            fecLiquidacionAnterior = (pb.getFecLiquidacion() != null) ? pb.getFecLiquidacion() : pb.getFecApertura();

            // Actualizamos la fecha de liquidación en el producto bancario 
            // asociado a la tarjeta.
            pb.setFecLiquidacion(fecLiquidacion);
            if (!pbdao.actualizarProductoBancario(pb)) {
                showMessage("Error al actualizar la fecha de liquidación en tarjeta.");
            }

            // Obtenemos el saldo pendiente.
            saldoPendiente = mdao.saldoFinDia(pb, fecLiquidacion).getImpMovimiento().doubleValue();

            // Obtenemos los intereses.
            intereses = calcularInteresesTC(tc, fecLiquidacion, fecLiquidacionAnterior);
            showMessage(String.format("Intereses calculados: %s", Functions.formatAmount(intereses)));

            // Generamos el movimiento de "INTERESES TARJETA" en la tarjeta.
            insertarMovimiento(fecLiquidacion, "INTERESES TARJETA", new BigDecimal(intereses), pb);

            // Calculamos la deuda total.
            totalDeuda = saldoPendiente + intereses;
            showMessage(String.format("Saldo pendiente actual: %s. Deuda total: %s", 
                    Functions.formatAmount(saldoPendiente),
                    Functions.formatAmount(totalDeuda)));

            // Obtenemos el importe a cobrar.
            showMessage(String.format("Forma de pago: %s", tc.getFormaPago()));
            // En la forma de pago a "CONTADO", se cobra todo el saldo.
            // En la forma de pago "APLAZADO", se cobra el 20% de la deuda con
            // un cargo mínimo de 20€ (según se especifíca en las condiciones
            // legales que se muestra en el extracto de tarjeta de crédito).
            if (tc.getFormaPago().equals("CONTADO")) {
                saldoCobrar = totalDeuda;
            } else {
                // Pago "APLAZADO". 20% de la cantidad pendiente, con un mínimo
                // de 20€.
                saldoCobrar = Functions.round((totalDeuda * 20 / 100.0), 2);
                if (Math.abs(saldoCobrar) < 20.0) {
                    if (Math.abs(totalDeuda) > 20) {
                        saldoCobrar = -20;
                    } else {
                        saldoCobrar = totalDeuda;
                    }
                }
            }
            showMessage(String.format("Importe de cobro: %s", Functions.formatAmount(saldoCobrar)));
            
            // Capturamos el saldo disponible en la cuenta corriente de cobro.
            saldoCCCobro = mdao.saldoFinDia(tc.getCuentaCorrientePago().getProductoBancario(), fecLiquidacion).getImpMovimiento().doubleValue();
            showMessage(String.format("Cuenta de cobro: %s. Saldo: %s", 
                    tc.getCuentaCorrientePago().getNumeroCuenta(),
                    Functions.formatAmount(saldoCCCobro)));
            // Comprobamos que el cliente tiene saldo en la cuenta corriente
            // de cobro.
            if ((saldoCCCobro + saldoCobrar) < 0) {
                // Saldo insuficiente
                showMessage(String.format("Saldo insuficiente. A cobrar: %s > %s",
                        Functions.formatAmount(saldoCobrar),
                        Functions.formatAmount(saldoCCCobro)));
                saldoCobrado = -saldoCCCobro;
            } else {
                saldoCobrado = saldoCobrar;
            }
            // Generamos movimiento "LIQUIDACIÓN TARJETA" en tarjeta. (-saldoCobrado)
            insertarMovimiento(fecLiquidacion, "LIQUIDACIÓN TARJETA", new BigDecimal(-saldoCobrado), pb);
            
            // Generamos movimiento "LIQUIDACIÓN TARJETA" en cuenta corriente de pago. (saldoCobrado)
            insertarMovimiento(fecLiquidacion, "LIQUIDACIÓN TARJETA", new BigDecimal(saldoCobrado), tc.getCuentaCorrientePago().getProductoBancario());
            
            // Si la fecha de liquidación anterior coincide con la fecha de 
            // apertura, la lista de movimientos comprende desde el día inicial
            // hasta la fecha de liquidación.
            // Si la fecha de liquidación anterior no coincide con la fecha 
            // de apertura, la lista de movimientos empieza al día siguiente
            // de la última liquidación.
            List<Movimiento> lstMovimiento;
            if (fecLiquidacionAnterior.equals(pb.getFecApertura()))
                lstMovimiento = mdao.listarMovimiento(pb, fecLiquidacionAnterior, fecLiquidacion);
            else
                lstMovimiento = mdao.listarMovimiento(pb, Functions.getDateFromLocalDate(Functions.getLocalDateFromDate(fecLiquidacionAnterior).plusDays(1)), fecLiquidacion);
            
            try {
                // Generamos el extracto de liquidación de tarjeta.
                ItxDocumentParams itp = new ItxDocumentParams(PdfType.LIQUIDATION, pb,
                        lstMovimiento, fecLiquidacion, saldoCobrar, saldoCobrado, intereses);
                ItxDocument itd = new ItxDocument(itp);
            } catch (IOException E) {
                System.out.println(E.getMessage());
            }

            // Avanzamos la barra de progreso.
            i += (pbProgreso.getMaximum() / lstTarjetaCredito.size());
            pbProgreso.setValue(i);
            // Forzamos a "repintar" el formulario (en caso contrario, no 
            // se mostrará nada hasta finalizar completamente el proceso).
            // ver https://stackoverflow.com/questions/5691979/progressbar-doesnt-change-its-value-in-java
            this.update(this.getGraphics());
        }   // Fin de bucle de liquidación

        // Marcamos finalización del proceso.
        showMessage("*** Fin liquidación de Tarjetas de Crédito ***");
        pbProgreso.setValue(pbProgreso.getMaximum());
        
        // Guardamos el resultado de la liquidación en un fichero de log.
        String fileName = Constants.DEST_PATH + "Liquidacion_Tarjetas_" + Long.toString(System.currentTimeMillis()) + ".log";
        File file = new File(fileName);
        file.getParentFile().mkdirs();        
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file, true)); // true for append
            txaResultado.write(writer);
            writer.close();
        } catch (IOException E) {
            System.out.println(E.getMessage());
        }
        
        // Fin de proceso.
        finProceso = true;
    }
    
    /**
     * Método para el cálculo de los intereses de una tarjeta de crédito.
     *
     * @param tc La tarjeta de crédito cuyos intereses se quieren calcular.
     * @param fecLiquidacion Fecha de liquidación actual.
     * @param fecLiquidacionAnterior Fecha de liquidación anterior.
     * @return El importe de los intereses calculados.
     */
    private double calcularInteresesTC(TarjetaCredito tc, Date fecLiquidacion, Date fecLiquidacionAnterior) {
        ProductoBancario pb = tc.getProductoBancario();
        // Calculamos los días transcurridos entre la liquidación anterior y la 
        // actual.
        long tiempo = fecLiquidacion.getTime() - fecLiquidacionAnterior.getTime();
        long dias = TimeUnit.DAYS.convert(tiempo, TimeUnit.MILLISECONDS);
        
        // Capturamos el saldo al final de día de la anterior liquidación.
        Movimiento m = mdao.saldoFinDia(pb, fecLiquidacionAnterior);
        
        showMessage(String.format("Saldo pendiente anterior: %s", Functions.formatAmount(m.getImpMovimiento())));
        
        // Capturamos los interes (C * r * t / (B * 100))
        double intereses = m.getImpMovimiento().doubleValue() * tc.getTipoInteres().doubleValue() * dias / (360.0 * 100.0);
        intereses = Functions.round(intereses, 2); // Redondeamos a 2 decimales.
        // No se liquidan intereses positivos.
        if (intereses > 0)
            intereses = 0;
        return intereses;
    }
    
    /**
     * Método para la liquidación de préstamos.
     */
    private void liquidacionPrestamos() {
        // Variables auxiliares para el proceso de liquidación.
        Date fecLiquidacion = jdcFecLiquidacion.getDate();
        Date fecLiquidacionAnterior = null;
        double saldoPendiente = 0;
        double saldoCobrar = 0;
        double saldoCCCobro = 0;
        double saldoCobrado = 0;
        int i = pbProgreso.getMinimum();

        // Marcamos comienzo del proceso.
        showMessage("*** Inicio liquidación de Préstamos ***");
        showMessage("Fecha de liquidación: " + Functions.formatDate(fecLiquidacion));
        pbProgreso.setValue(i);

        // Capturamos todas las tarjetas a liquidar.
        List<Prestamo> lstPrestamo = pdao.buscarPrestamo(fecLiquidacion);
        // Mensaje informado de que no hay tarjetas para liquidar.
        // ¿Proceso de liquidación ya realizado?.
        if (lstPrestamo.isEmpty()) {
            showMessage("No hay préstamos pendientes de liquidar a fecha " + Functions.formatDate(fecLiquidacion));
        }
        // Bucle de liquidación.
        for (Prestamo p : lstPrestamo) {
            // Guardamos el producto bancario asociado al prestamo.
            ProductoBancario pb = p.getProductoBancario();
            
            // Mensaje de liquidación.
            showMessage(String.format("Liquidando préstamo %s - %s %s",
                    p.getNumeroPrestamo(),
                    pb.getClienteList().get(0).getNombreCliente(),
                    pb.getClienteList().get(0).getApellidosCliente()));

            // Determinamos la fecha de liquidación actual.
            fecLiquidacion = (p.getFecVencimiento().compareTo(fecLiquidacion) > 0) ? fecLiquidacion : p.getFecVencimiento();
            if (fecLiquidacion.equals(p.getFecVencimiento())) {
                showMessage(String.format("Liquidación sólo hasta fecha de vencimiento %s", Functions.formatDate(fecLiquidacion)));
            }
            
            // Calculamos la fecha  de liquidación anterior.
            fecLiquidacionAnterior = (pb.getFecLiquidacion() != null) ? pb.getFecLiquidacion() : pb.getFecApertura();

            // Actualizamos la fecha de liquidación en el producto bancario 
            // asociado a la tarjeta.
            pb.setFecLiquidacion(fecLiquidacion);
            if (!pbdao.actualizarProductoBancario(pb)) {
                showMessage("Error al actualizar la fecha de liquidación en préstamo.");
            }

            // Obtenemos el saldo pendiente.
            saldoPendiente = mdao.saldoFinDia(pb, fecLiquidacion).getImpMovimiento().doubleValue();

            // Obtenemos los intereses.
            AmortizacionPrestamo amortizacionPrestamo = calcularInteresesPT(p, fecLiquidacion, fecLiquidacionAnterior);
            showMessage(String.format("Intereses calculados: %s", Functions.formatAmount(amortizacionPrestamo.intereses)));

            // Generamos el movimiento de "INTERESES PRÉSTAMO" en el préstamo.
            insertarMovimiento(fecLiquidacion, "INTERESES PRÉSTAMO", new BigDecimal(amortizacionPrestamo.intereses), pb);

            // Calculamos la deuda total.
            saldoCobrar = amortizacionPrestamo.cuotaDiaria;
            showMessage(String.format("Saldo pendiente actual: %s. Cuota: %s. Amortizado: %s", 
                    Functions.formatAmount(saldoPendiente),
                    Functions.formatAmount(saldoCobrar),
                    Functions.formatAmount(-amortizacionPrestamo.capitalAmortizado)));
            
            // Capturamos el saldo disponible en la cuenta corriente de cobro.
            saldoCCCobro = mdao.saldoFinDia(p.getCuentaCorrientePago().getProductoBancario(), fecLiquidacion).getImpMovimiento().doubleValue();
            showMessage(String.format("Cuenta de cobro: %s. Saldo: %s", 
                    p.getCuentaCorrientePago().getNumeroCuenta(),
                    Functions.formatAmount(saldoCCCobro)));
            // Comprobamos que el cliente tiene saldo en la cuenta corriente
            // de cobro.
            if ((saldoCCCobro + saldoCobrar) < 0) {
                // Saldo insuficiente
                showMessage(String.format("Saldo insuficiente. A cobrar: %s > %s",
                        Functions.formatAmount(saldoCobrar),
                        Functions.formatAmount(saldoCCCobro)));
                saldoCobrado = -saldoCCCobro;
            } else {
                saldoCobrado = saldoCobrar;
            }
            // Generamos movimiento "AMORTIZACIÓN PRÉSTAMO" en préstamo. (-saldoCobrado)
            insertarMovimiento(fecLiquidacion, "AMORTIZACIÓN PRÉSTAMO", new BigDecimal(-saldoCobrado), pb);
            
            // Generamos movimiento "AMORTIZACIÓN PRÉSTAMO" en cuenta corriente de pago. (saldoCobrado)
            insertarMovimiento(fecLiquidacion, "AMORTIZACIÓN PRÉSTAMO", new BigDecimal(saldoCobrado), p.getCuentaCorrientePago().getProductoBancario());
            
            // Si la fecha de liquidación anterior coincide con la fecha de 
            // apertura, la lista de movimientos comprende desde el día inicial
            // hasta la fecha de liquidación.
            // Si la fecha de liquidación anterior no coincide con la fecha 
            // de apertura, la lista de movimientos empieza al día siguiente
            // de la última liquidación.
            List<Movimiento> lstMovimiento;
            if (fecLiquidacionAnterior.equals(pb.getFecApertura()))
                lstMovimiento = mdao.listarMovimiento(pb, fecLiquidacionAnterior, fecLiquidacion);
            else
                lstMovimiento = mdao.listarMovimiento(pb, Functions.getDateFromLocalDate(Functions.getLocalDateFromDate(fecLiquidacionAnterior).plusDays(1)), fecLiquidacion);

            // Si el préstamo ha llegado a su finalización, debe cancelarse.
            if (fecLiquidacion.equals(p.getFecVencimiento())) {
                cancelarPrestamo(p);
            }

            try {
                // Generamos el extracto de liquidación de préstamo.
                ItxDocumentParams itp = new ItxDocumentParams(PdfType.LIQUIDATION, pb,
                        lstMovimiento, fecLiquidacion, saldoCobrar, saldoCobrado, amortizacionPrestamo.intereses);
                ItxDocument itd = new ItxDocument(itp);
            } catch (IOException E) {
                System.out.println(E.getMessage());
            }
            
            // Avanzamos la barra de progreso.
            i += (pbProgreso.getMaximum() / lstPrestamo.size());
            pbProgreso.setValue(i);
            // Forzamos a "repintar" el formulario (en caso contrario, no 
            // se mostrará nada hasta finalizar completamente el proceso).
            // ver https://stackoverflow.com/questions/5691979/progressbar-doesnt-change-its-value-in-java
            this.update(this.getGraphics());
        }   // Fin de bucle de liquidación

        // Marcamos finalización del proceso.
        showMessage("*** Fin liquidación de Préstamos ***");
        pbProgreso.setValue(pbProgreso.getMaximum());
        
        // Guardamos el resultado de la liquidación en un fichero de log.
        String fileName = Constants.DEST_PATH + "Liquidacion_Préstamos_" + Long.toString(System.currentTimeMillis()) + ".log";
        File file = new File(fileName);
        file.getParentFile().mkdirs();        
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file, true)); // true for append
            txaResultado.write(writer);
            writer.close();
        } catch (IOException E) {
            System.out.println(E.getMessage());
        }
        
        // Fin de proceso.
        finProceso = true;
    }
    
    /**
     * Método para el cálculo de los intereses de un préstamo.
     *
     * @param p Préstamo cuyos intereses se quieren calcular.
     * @param fecLiquidacion La fecha actual de liquidación.
     * @param fecLiquidacionAnterior La fecha anterior de liquidación.
     * @return Objeto {@link AmortizacionPrestamo} con el resultado de la
     * liquidación.
     */
    private AmortizacionPrestamo calcularInteresesPT(Prestamo p, Date fecLiquidacion, Date fecLiquidacionAnterior) {
        ProductoBancario pb = p.getProductoBancario();
        // Calculamos los días transcurridos entre la liquidación anterior y la 
        // actual.
        long tiempo = fecLiquidacion.getTime() - fecLiquidacionAnterior.getTime();
        long dias = TimeUnit.DAYS.convert(tiempo, TimeUnit.MILLISECONDS);

        // Capturamos el saldo al final de día de la anterior liquidación.
        Movimiento m = mdao.saldoFinDia(pb, fecLiquidacionAnterior);
        showMessage(String.format("Saldo pendiente anterior: %s", Functions.formatAmount(m.getImpMovimiento())));

        /**
         * Calculamos los intereses con amortización francesa.
         * a: cuota periódica constante
         * C: capital prestado 
         * i: tipo de interés del préstamo
         * n: número de períodos 
         * 
         *        C * i
         * a = -----------
         *     1-(1+i)^-n
         * 
         */
        long duracionMeses = ChronoUnit.MONTHS.between(Functions.getLocalDateFromDate(pb.getFecApertura()),
                Functions.getLocalDateFromDate(p.getFecVencimiento()));
        // Si el capital inicial es menor que el capital pendiente, tomamos 
        // el capital inicial como base para calcular la cuota. En caso 
        // contrario (por impago y acumulación de intereses), tomamos el 
        // capital pendiente para el cálculo de la cuota.
        double capitalInicial = (p.getImpConcedido().doubleValue() < m.getImpMovimiento().doubleValue()) ? p.getImpConcedido().doubleValue() : m.getImpMovimiento().doubleValue();
        double capitalPendiente = m.getImpMovimiento().doubleValue();
        double tipoMensual = p.getTipoInteres().doubleValue() / (12 * 100);
        double cuotaMensual = capitalInicial * (tipoMensual / (1 - Math.pow((1 + tipoMensual), (duracionMeses * -1))));

        // Obtenemos los días del mes, para hacer la proporción de la cuota e
        // e intereses con respecto al número de días transcurridos en la 
        // liquidación.
        long diasEnMes = Functions.getDaysOfMonth(fecLiquidacion);
        // Calculamos la cuota mensual, los intereses y el capital amortizado.
        cuotaMensual = cuotaMensual * dias / diasEnMes;
        double intereses = Functions.round(capitalPendiente * tipoMensual * dias / diasEnMes, 2);
        double capitalAmortizado = cuotaMensual - intereses;
        capitalPendiente = capitalPendiente - capitalAmortizado;

        // Caso especial para la última liquidación.
        if (fecLiquidacion.equals(p.getFecVencimiento())) {
            intereses = intereses - capitalPendiente;
            capitalAmortizado = cuotaMensual - intereses;
            capitalPendiente = 0;
        }

        return new AmortizacionPrestamo(cuotaMensual, capitalAmortizado, intereses);
    }
    
    /**
     * Clase privada para almacenar información relacionada con las
     * liquidaciones de préstamos.
     */
    private class AmortizacionPrestamo {
        double cuotaDiaria;
        double capitalAmortizado;
        double intereses;

        public AmortizacionPrestamo() {
        }
        
        public AmortizacionPrestamo(double cuotaDiaria, double capitalAmortizado, double intereses) {
            this.cuotaDiaria = cuotaDiaria;
            this.capitalAmortizado = capitalAmortizado;
            this.intereses = intereses;
        }

    }

    /**
     * Método para cancelar un préstamo que ya ha llegado a su vencimiento.
     *
     * @param p El préstamo a cancelar.
     */
    private void cancelarPrestamo(Prestamo p) {
        // Comprobamos el saldo pendiente a la fecha de vencimiento 
        // del préstamo.
        ProductoBancario pb = p.getProductoBancario();
        double saldoPendiente = mdao.saldoFinDia(pb, p.getFecVencimiento()).getImpMovimiento().doubleValue();
        if (saldoPendiente == 0) {
            // Refrescamos el producto bancario.
            if (pbdao.buscarProductoBancario(pb.getIdProductoBancario())) {
                pb = pbdao.getProductoBancario();
            }
            // Cancelamos el préstamo.
            pb.setFecCancelacion(p.getFecVencimiento());
            if (pbdao.actualizarProductoBancario(pb)) {
                showMessage(String.format("Préstamo vencido cancelado. Fecha de vencimiento: %s", Functions.formatDate(p.getFecVencimiento())));
            } else {
                showMessage("Error al actualizar la fecha de cancelación en préstamo.");
            }
        }
    }
    
    private void insertarMovimiento(Date fecMovimiento, String concepto, BigDecimal impMovimiento, ProductoBancario pb) {
        // Comprobamos que el movimiento no sea de importe cero.
        if (!impMovimiento.equals(BigDecimal.ZERO)) {
            // Creamos el movimiento con la información recibida y lo grabamos.
            Movimiento movimiento = new Movimiento(null, fecMovimiento, concepto, impMovimiento);
            movimiento.setProductoBancario(pb);
            if (!mdao.insertarMovimiento(movimiento)) {
                showMessage(String.format("Error al grabar el movimiento %s %s %s",
                        Functions.formatDate(fecMovimiento), concepto, Functions.formatAmount(impMovimiento)));
            }
        }
    }
    
    private void showMessage(String message) {
        txaResultado.append(setStatusBarText(message) + "\n");        
    }
    
    private String setStatusBarText(String texto) {
        Format f = new SimpleDateFormat("HH:mm:ss");
        texto = f.format(new Date()) + " " + texto;
        txtStatus.setText(texto);
        return texto;
    }    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        pnlDatos = new javax.swing.JPanel();
        lblFecLiquidacion = new javax.swing.JLabel();
        jdcFecLiquidacion = new com.toedter.calendar.JDateChooser();
        btnIniciarLiquidacion = new javax.swing.JButton();
        pbProgreso = new javax.swing.JProgressBar();
        pnlResultado = new javax.swing.JScrollPane();
        txaResultado = new javax.swing.JTextArea();
        pnlStatusBar = new view.pnlAppSoftBank();
        txtStatus = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(Functions.getImage("icons/SoftBank 16px.png")).getImage());
        setMinimumSize(new java.awt.Dimension(131, 360));
        setPreferredSize(new java.awt.Dimension(600, 400));
        setSize(new java.awt.Dimension(600, 420));

        pnlDatos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos de Liquidación", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lblFecLiquidacion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblFecLiquidacion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFecLiquidacion.setText("Fecha de liquidación");

        jdcFecLiquidacion.setToolTipText("Fecha de liquidación. Debe ser un final de mes.");
        jdcFecLiquidacion.setDateFormatString("dd/MM/yyyy");
        jdcFecLiquidacion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jdcFecLiquidacion.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcFecLiquidacionPropertyChange(evt);
            }
        });
        // Asignamos el listener "propertyChange" también al editor del campo.
        jdcFecLiquidacion.getDateEditor().addPropertyChangeListener((java.beans.PropertyChangeEvent evt) -> {
            jdcFecLiquidacionPropertyChange(evt);
        });

        btnIniciarLiquidacion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnIniciarLiquidacion.setText("Iniciar liquidación");
        btnIniciarLiquidacion.setEnabled(false);
        btnIniciarLiquidacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarLiquidacionActionPerformed(evt);
            }
        });

        pbProgreso.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pbProgreso.setStringPainted(true);

        javax.swing.GroupLayout pnlDatosLayout = new javax.swing.GroupLayout(pnlDatos);
        pnlDatos.setLayout(pnlDatosLayout);
        pnlDatosLayout.setHorizontalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFecLiquidacion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jdcFecLiquidacion, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnIniciarLiquidacion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pbProgreso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFecLiquidacion)
                    .addComponent(jdcFecLiquidacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnIniciarLiquidacion)
                    .addComponent(pbProgreso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnIniciarLiquidacion, jdcFecLiquidacion, lblFecLiquidacion, pbProgreso});

        getContentPane().add(pnlDatos, java.awt.BorderLayout.PAGE_START);

        pnlResultado.setMinimumSize(new java.awt.Dimension(16, 200));

        txaResultado.setEditable(false);
        txaResultado.setColumns(20);
        txaResultado.setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        txaResultado.setRows(5);
        pnlResultado.setViewportView(txaResultado);

        getContentPane().add(pnlResultado, java.awt.BorderLayout.CENTER);

        pnlStatusBar.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlStatusBar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pnlStatusBar.setMinimumSize(new java.awt.Dimension(131, 100));
        pnlStatusBar.setName("pnlStatusBar"); // NOI18N
        pnlStatusBar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        txtStatus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtStatus.setText(" ");
        pnlStatusBar.add(txtStatus);

        getContentPane().add(pnlStatusBar, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnIniciarLiquidacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarLiquidacionActionPerformed
        // Lanzamos el proceso de liquidación.
        if (catalogo.equals(cdao.getTarjetaCredito())) {
            liquidacionTarjetas();
        }
        if (catalogo.equals(cdao.getPrestamo())) {
            liquidacionPrestamos();
        }
        jdcFecLiquidacion.setEnabled(false);
        btnIniciarLiquidacion.setEnabled(false);
    }//GEN-LAST:event_btnIniciarLiquidacionActionPerformed

    private void jdcFecLiquidacionPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcFecLiquidacionPropertyChange
        if (!finProceso) {
            // Comprobamos que:
            // - La fecha de liquidación no sea nula.
            // - Que esté comprendida entre la mínima y la máxima permitidas.
            // - Que coincida con un final de mes.
            if ((jdcFecLiquidacion.getDate() != null)
                    && (jdcFecLiquidacion.getDate().compareTo(jdcFecLiquidacion.getMaxSelectableDate()) <= 0)
                    && (jdcFecLiquidacion.getDate().compareTo(jdcFecLiquidacion.getMinSelectableDate()) >= 0)
                    && (Functions.getEndOfDay(jdcFecLiquidacion.getDate())).equals(Functions.getEndOfMonth(jdcFecLiquidacion.getDate()))) {
                btnIniciarLiquidacion.setEnabled(true);
            } else {
                btnIniciarLiquidacion.setEnabled(false);
            }
        }
    }//GEN-LAST:event_jdcFecLiquidacionPropertyChange

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmLiquidacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmLiquidacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmLiquidacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmLiquidacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmLiquidacion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIniciarLiquidacion;
    private javax.swing.JLabel jLabel1;
    private com.toedter.calendar.JDateChooser jdcFecLiquidacion;
    private javax.swing.JLabel lblFecLiquidacion;
    private javax.swing.JProgressBar pbProgreso;
    private javax.swing.JPanel pnlDatos;
    private javax.swing.JScrollPane pnlResultado;
    private javax.swing.JPanel pnlStatusBar;
    private javax.swing.JTextArea txaResultado;
    private javax.swing.JLabel txtStatus;
    // End of variables declaration//GEN-END:variables
}
