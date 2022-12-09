package pdf;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import model.entity.Cliente;
import model.entity.Direccion;
import model.entity.Empleado;
import model.entity.Movimiento;
import model.entity.ProductoBancario;
import model.entity.Sucursal;

import utilities.Banking;
import utilities.Constants;
import utilities.Constants.PdfType;
import utilities.Functions;
//</editor-fold>

/**
 * Clase para generar un extracto de movimientos en formato PDF.
 *
 * @author Ignacio Pineda Martín
 */
public class ItxDocument {

    /**
     * Imagenes usadas en el extracto.
     */
    public static final String SOFTBANK_IMG = "src/main/resources/icons/SoftBank 200x54.png";
    public static final String CC_ICON = "src/main/resources/icons/cuenta-corriente 24px.png";
    public static final String TC_ICON = "src/main/resources/icons/tarjeta-credito 24px.png";
    public static final String PT_ICON = "src/main/resources/icons/prestamo 24px.png";
    
    /**
     * Elementos PDF usados a lo largo del módulo
     */
    static Document document;    
    static PdfFont fontNormal = null;
    static PdfFont fontBold = null;
    
    /**
     * Generación de extracto de movimientos o de liquidación de producto
     * bancario.
     *
     * @param itp Clase de tipo {@link ItxDocumentParams} con la información que
     * necesita este generador de documentos.
     * @throws IOException
     */
    public ItxDocument(ItxDocumentParams itp) throws IOException {
        // Creamos el fichero en el directorio especificado.
        String fileName = Constants.DEST_PATH + itp.getPdfType().toString() + "_" + itp.getProductoBancario().getCatalogo().getCodProducto() + "_" + Long.toString(System.currentTimeMillis()) + ".pdf";
        File file = new File(fileName);
        file.getParentFile().mkdirs();

        // Inicializamos los componentes necesario para generar el PDF.
        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        document = new Document(pdf);
        // Añadimos un margen inferior más amplio para mostrar las condiciones
        // legales de los productos.
        document.setBottomMargin(80);

        // Creamos las fuentes que vamos a usar en el formulario.
        fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        // Mostramos el logo del banco.
        logo();

        // Mostramos la información de la sucursal asociada al producto.
        sucursalProducto(itp.getProductoBancario());

        // Mostramos el titulo del documento que estamos generando.
        tituloDocumento(itp.getProductoBancario(), itp.getPdfType(), itp.getFecLiquidacion());

        // Mostramos la información asociada a los clientes.
        infoCliente(itp.getProductoBancario());

        // Mostramos información específica del producto.
        infoProducto(itp.getProductoBancario());

        // Mostramos información del gestor del banco.
        infoGestor(itp.getProductoBancario());

        // Mostramos la lista de movimientos en una tabla.
        listarMovimientos(itp.getLstMovimiento());
        
        // Mostramos el resultado de la liquidación.
        infoLiquidacion(itp);
        
        // Mostramos las condiciones legales en el pie de página.
        pieDePagina(pdf, itp.getProductoBancario());

        // Cerramos el documento. El PDF ya está creado.
        document.close();

        // Abrimos el fichero creado.
        Desktop dt = Desktop.getDesktop();
        dt.open(file);
    }

    /**
     * Mostramos el logo del banco.
     *
     * @throws IOException
     */
    private void logo() throws IOException {
        Image softBank = new Image(ImageDataFactory.create(SOFTBANK_IMG));
        Paragraph logo = new Paragraph()
                .add(softBank)
                .setMarginBottom(10);
        document.add(logo);
    }

    /**
     * Mostramos la información de sucursal.
     *
     * @param pb
     * @throws IOException
     */
    private void sucursalProducto(ProductoBancario pb) throws IOException {
        Sucursal sucursal;
        Direccion direccion;
        sucursal = pb.getSucursal();
        direccion = sucursal.getDireccion();
        document.add(new Paragraph().setMarginTop(5));
        // Creamos una lista de datos para el PDF.
        List datosSucursal = new List()
                .setSymbolIndent(12)
                .setListSymbol("")
                .setFont(fontNormal);
        // Añadimos información de la sucursal a la lista.
        datosSucursal.add(new ListItem(String.format("Sucursal (%s) %s", sucursal.getCodSucursal(), sucursal.getNombreSucursal())))
                .add(new ListItem(String.format("%s %s, %s", direccion.getTipoVia(), direccion.getNombreVia(), direccion.getNumero().stripTrailingZeros().toPlainString())))
                .add(new ListItem(String.format("%05d %s", direccion.getCodPostal(), direccion.getPoblacion())))
                .add(new ListItem(String.format("%s - %s", direccion.getProvinciaEstado(), direccion.getPais())));
        // Añadimos la lista al documento.
        document.add(datosSucursal);
    }

    /**
     * Mostramos el título del documento.
     *
     * @param pb
     * @param pdfType
     * @throws IOException
     */
    private void tituloDocumento(ProductoBancario pb, PdfType pdfType, Date fecLiquidacion) throws IOException {
        String titulo = "";
        Image icon = null;
        if (pb.getCuentaCorriente() != null) {
            // Cuenta Corriente.
            icon = new Image(ImageDataFactory.create(CC_ICON));
            switch (pdfType) {
                case LIQUIDATION ->
                    titulo = " Liquidación de Cuenta Corriente a fecha " + Functions.formatDate(fecLiquidacion) + " ";
                case MOVEMENT ->
                    titulo = " Extracto de Cuenta Corriente ";
            }
        }
        if (pb.getTarjetaCredito() != null) {
            // Tarjeta de Crédito.
            icon = new Image(ImageDataFactory.create(TC_ICON));
            switch (pdfType) {
                case LIQUIDATION ->
                    titulo = " Liquidación de Tarjeta de Crédito a fecha " + Functions.formatDate(fecLiquidacion) + " ";
                case MOVEMENT ->
                    titulo = " Extracto de Tarjeta de Crédito ";
            }
        }
        if (pb.getPrestamo() != null) {
            // Préstamo.
            icon = new Image(ImageDataFactory.create(PT_ICON));
            switch (pdfType) {
                case LIQUIDATION ->
                    titulo = " Liquidación de Préstamo a fecha " + Functions.formatDate(fecLiquidacion) + " ";
                case MOVEMENT ->
                    titulo = " Extracto de Préstamo ";
            }
        }
        Paragraph tc = new Paragraph("")
                .add(icon)
                .add(titulo)
                .add(icon)
                .setFont(fontBold)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10);
        // Añadimos el párrafo al documento.
        document.add(tc);
    }

    /**
     * Mostramos información del cliente.
     *
     * @param pb
     * @throws IOException
     */
    private void infoCliente(ProductoBancario pb) throws IOException {
        java.util.List<Cliente> lstCliente = pb.getClienteList();
        Direccion direccion = lstCliente.get(0).getDireccion();
        document.add(new Paragraph().setMarginTop(10));
        // Creamos una lista de datos para los clientes.
        List datosCliente = new List()
                .setSymbolIndent(12)
                .setListSymbol("")
                .setFont(fontNormal);
        // Añadimos todos los titulares del producto.
        for (Cliente c : lstCliente) {
            datosCliente.add(new ListItem(String.format("D./DÑA. %s %s", c.getNombreCliente(), c.getApellidosCliente())));
        }
        // Añadimos los datos de la dirección.
        datosCliente.add(new ListItem(String.format("%s %s, %s", direccion.getTipoVia(), direccion.getNombreVia(), direccion.getNumero().stripTrailingZeros().toPlainString())))
                .add(new ListItem(String.format("%05d %s", direccion.getCodPostal(), direccion.getPoblacion())))
                .add(new ListItem(String.format("%s - %s", direccion.getProvinciaEstado(), direccion.getPais())));
        // Añadimos la lista al documento.
        document.add(datosCliente);
    }

    /**
     * Mostramos información específica del producto bancario.
     *
     * @param pb
     * @throws IOException
     */
    private void infoProducto(ProductoBancario pb) throws IOException {
        String info = "";
        if (pb.getCuentaCorriente() != null) {
            info = String.format("IBAN: %s / Cuenta: %s",
                    Banking.fourDigitGroup(pb.getCuentaCorriente().getIban()),
                    pb.getCuentaCorriente().getNumeroCuenta());
        }
        if (pb.getTarjetaCredito() != null) {
            info = String.format("PAN: %s / Tarjeta: %s / Pago: %s / Límite: %s",
                    Banking.fourDigitGroup(pb.getTarjetaCredito().getPan()),
                    pb.getTarjetaCredito().getNumeroTarjeta(),
                    pb.getTarjetaCredito().getFormaPago(),
                    Functions.formatAmount(pb.getTarjetaCredito().getImpLimiteTarjeta()));
        }
        if (pb.getPrestamo() != null) {
            info = String.format("Prestamo: %s / Garantía: %s / Imp.: %s / Vto.: %s",
                    pb.getPrestamo().getNumeroPrestamo(),
                    pb.getPrestamo().getTipoGarantia(),
                    Functions.formatAmount(pb.getPrestamo().getImpConcedido()),
                    Functions.formatDate(pb.getPrestamo().getFecVencimiento()));
        }        
        document.add(new Paragraph(info)
                .setFirstLineIndent(12)
                .setMarginTop(5)
                .setFont(fontBold));
    }

    /**
     * Mostramos información del gestor asignado.
     *
     * @param pb
     * @throws IOException
     */
    private void infoGestor(ProductoBancario pb) throws IOException {
        Empleado empleado = pb.getClienteList().get(0).getEmpleado();
        Sucursal sucursal = empleado.getSucursal();
        document.add(new Paragraph(String.format("Su gestor: %s %s, en sucursal (%s) %s", empleado.getNombreEmpleado(), empleado.getApellidosEmpleado(), sucursal.getCodSucursal(), sucursal.getNombreSucursal()))
                .setFirstLineIndent(12)
                .setFont(fontNormal));
    }

    /**
     * Mostramos la lista de movimientos en una tabla.
     *
     * @param lstMovimiento
     * @throws IOException
     */
    private void listarMovimientos(java.util.List<Movimiento> lstMovimiento) throws IOException {
        document.add(new Paragraph().setMarginTop(15));
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 5, 2, 2})).useAllAvailableWidth();
        table.setTextAlignment(TextAlignment.LEFT).setFont(fontNormal);
        // Cabecera de la tabla.
        table.addHeaderCell(new Cell().add(new Paragraph("Fecha").setTextAlignment(TextAlignment.CENTER).setFont(fontBold)));
        table.addHeaderCell(new Cell().add(new Paragraph("Concepto").setTextAlignment(TextAlignment.CENTER).setFont(fontBold)));
        table.addHeaderCell(new Cell().add(new Paragraph("Imp. Deudor").setTextAlignment(TextAlignment.CENTER).setFont(fontBold)));
        table.addHeaderCell(new Cell().add(new Paragraph("Imp. Acreedor").setTextAlignment(TextAlignment.CENTER).setFont(fontBold)));
        // Añadimos los movimientos a la tabla.
        for (Movimiento m : lstMovimiento) {
            table.addCell(Functions.formatDate(m.getFecMovimiento()));
            table.addCell(m.getConcepto());
            BigDecimal imp = m.getImpMovimiento();
            if (imp.compareTo(BigDecimal.ZERO) < 0) {
                table.addCell(new Cell().add(new Paragraph(Functions.formatAmount(imp)).setTextAlignment(TextAlignment.RIGHT).setFontColor(ColorConstants.RED)));
                table.addCell("");
            } else {
                table.addCell("");
                table.addCell(new Cell().add(new Paragraph(Functions.formatAmount(imp)).setTextAlignment(TextAlignment.RIGHT).setFontColor(new DeviceRgb(0, 129, 0))));
            }
        }
        // Añadimos la tabla al documento.
        document.add(table);
    }

    /**
     * Mostramos información relacionada específicamente con la liquidación.
     *
     * @param itp
     */
    private void infoLiquidacion(ItxDocumentParams itp) {
        ProductoBancario pb = itp.getProductoBancario();
        if (itp.getPdfType() == PdfType.LIQUIDATION) {
            // Mostramos información diferenciada según el producto que 
            // se haya liquidado.
            if (pb.getCuentaCorriente() != null) {
                // TODO Liquidación de Cuentas Corrientes
            }
            if (pb.getTarjetaCredito() != null) {
                // Mostramos la cuenta de cargo.
                document.add(new Paragraph(String.format("Cuenta de cargo: %s", Banking.fourDigitGroup(pb.getTarjetaCredito().getCuentaCorrientePago().getIban())))
                        .setFirstLineIndent(12)
                        .setFont(fontBold));

                // Si ha faltado saldo en la cuenta de pago, mostramos un aviso.
                if (itp.getImpSaldoCobrado() > itp.getImpSaldoCobrar()) {
                    document.add(new Paragraph(String.format("Se recuerda al cliente que, dado que la deuda calculada de %s no ha podido ser cargada en la cuenta (saldo disponible %s), el diferencial de %s seguirá devengando intereses en la tarjeta.",
                            Functions.formatAmount(itp.getImpSaldoCobrar()),
                            Functions.formatAmount(-itp.getImpSaldoCobrado()),
                            Functions.formatAmount(itp.getImpSaldoCobrar() - itp.getImpSaldoCobrado())))
                            .setFont(fontNormal)
                            .setTextAlignment(TextAlignment.JUSTIFIED));
                }
            }
            if (pb.getPrestamo() != null) {
                // Mostramos la cuenta de cargo.
                document.add(new Paragraph(String.format("Cuenta de cargo: %s", Banking.fourDigitGroup(pb.getPrestamo().getCuentaCorrientePago().getIban())))
                        .setFirstLineIndent(12)
                        .setFont(fontBold));
                
                // Mostramos el importe amortizado.
                double impAmortizado = ((-itp.getImpSaldoCobrado() + itp.getImpIntereses()) > 0) ? Functions.round(-itp.getImpSaldoCobrado() + itp.getImpIntereses(), 2) : 0;
                document.add(new Paragraph(String.format("A titulo informativo, la amortización de capital de este mes ha sido de %s.",
                        Functions.formatAmount(impAmortizado)))
                        .setFont(fontNormal)
                        .setTextAlignment(TextAlignment.JUSTIFIED));

                // Si ha faltado saldo en la cuenta de pago, mostramos un aviso.
                if (itp.getImpSaldoCobrado() > itp.getImpSaldoCobrar()) {
                    document.add(new Paragraph(String.format("Se recuerda al cliente que, dado que la deuda calculada de %s no ha podido ser cargada en la cuenta (saldo disponible %s), el diferencial de %s seguirá devengando intereses en el préstamo.",
                            Functions.formatAmount(itp.getImpSaldoCobrar()),
                            Functions.formatAmount(-itp.getImpSaldoCobrado()),
                            Functions.formatAmount(itp.getImpSaldoCobrar() - itp.getImpSaldoCobrado())))
                            .setFont(fontNormal)
                            .setTextAlignment(TextAlignment.JUSTIFIED));
                }
            }
        }
    }
    
    /**
     * Mostramos las concidiones legales en el pie de página del documento.
     *
     * @param pdf
     * @throws IOException
     */
    private void pieDePagina(PdfDocument pdf, ProductoBancario pb) throws IOException {
        String titulo = "";
        String infoLegal = "";
        float plusY = 0;
        // Creamos los textos a mostrar, en función del producto.
        if (pb.getCuentaCorriente() != null) {
            plusY = 35;
            double TAE = Banking.getTAE(pb.getCuentaCorriente().getTipoInteres().doubleValue());
            titulo = "*** Condiciones legales de Cuenta Corriente ***";
            infoLegal = String.format("Tipo de interes aplicado %s%%. TAE %s%%. No se aplican comisiones por transferencias cuyo importe sea menor a 50.000€. Queda a potestad del banco la autorización de descubiertos en la cuenta corriente. En esos casos, se aplicará un tipo de interes equivalente al tipo de interes legal más un 8%% adicional. Pueden aplicarse comisiones de cambio y de reclamación de saldos.",
                    Functions.formatNumber(pb.getCuentaCorriente().getTipoInteres()),
                    Functions.formatNumber(TAE));
        }
        if (pb.getTarjetaCredito() != null) {
            plusY = 35;
            double TAE = Banking.getTAE(pb.getTarjetaCredito().getTipoInteres().doubleValue());
            titulo = "*** Condiciones legales de Tarjeta de Crédito ***";
            infoLegal = String.format("Tipo de interes aplicado %s%%. TAE %s%%. La forma de pago APLAZADO implica el cobro del 20%% del saldo pendiente, con un cobro mínimo de 20€. La forma de pago CONTADO implica el cobro completo a la fecha de liquidación. En caso de no haber saldo suficiente en la cuenta de cobro designada, el saldo pendiente quedará acumulado en la tarjeta. Pueden aplicarse comisiones de cambio y de reclamación de saldos.",
                    Functions.formatNumber(pb.getTarjetaCredito().getTipoInteres()),
                    Functions.formatNumber(TAE));
        }
        if (pb.getPrestamo()!= null) {
            plusY = 35;
            double TAE = Banking.getCompoundTAE(pb.getPrestamo().getTipoInteres().doubleValue());
            titulo = "*** Condiciones legales de Préstamo ***";
            infoLegal = String.format("Tipo de interes aplicado %s%%. TAE %s%%. El prestatario respalda el pago del préstamo (incluyendo intereses, gastos y comisiones) con todos sus bienes personales, tanto presentes como futuros. En la garantía HIPOTECARIA, además, se incluye expresamente el inmueble objeto del préstamo. Prestamista y prestario quedan sometidos a la jurisdicción de los juzgados de Madrid.",
                    Functions.formatNumber(pb.getPrestamo().getTipoInteres()),
                    Functions.formatNumber(TAE));
        }        
        
        Rectangle pageSize = pdf.getFirstPage().getPageSize();
        float footerY = document.getBottomMargin() - 40;
        float widthX = pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin();

        // Creamos los párrafos, con su posición absoluta. El eje vertical
        // empieza en la parte inferior del documento. Por tanto, cuanto más 
        // alto sea el valor, más arriba se mostrarán los textos.
        Paragraph pTitulo = new Paragraph(titulo).setTextAlignment(TextAlignment.CENTER).setFont(fontBold).setFixedPosition(0, footerY + plusY, pageSize.getWidth());
        Paragraph pInfoLegal = new Paragraph(infoLegal).setTextAlignment(TextAlignment.LEFT).setFixedPosition(document.getLeftMargin(), footerY, widthX);
        
        // Mostramos el pie de página en todas las páginas del documento.
        for (int i = 1; i <= pdf.getNumberOfPages(); i++) {
            // Incluimos el número de página.
            Paragraph pNumPagina = new Paragraph(String.valueOf(i)).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, footerY - 10, pageSize.getWidth());
            Canvas canvas = new Canvas(pdf.getPage(i), pageSize);
            canvas
                    .setFont(fontNormal)
                    .setFontSize(8)
                    .add(pTitulo)
                    .add(pInfoLegal)
                    .add(pNumPagina)                    
                    .close();
        }
    }
    
}
