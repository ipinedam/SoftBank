package pdf;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import utilities.Banking;
import utilities.Constants;
import utilities.Functions;
//</editor-fold>

/**
 * Clase para generar un cuadro de amortización de préstamo en formato PDF.
 *
 * @author Ignacio Pineda Martín
 */
public class ItxAmortization {

    /**
     * Imagenes usadas en el extracto.
     */
    public static final String SOFTBANK_IMG = "src/main/resources/icons/SoftBank 200x54.png";
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
     * @param ilp Objeto de tipo {@link ItxAmortizationParams} con la información que
     * necesita este generador de cuadros de amortización.
     * @throws IOException
     */
    public ItxAmortization(ItxAmortizationParams ilp) throws IOException {
        // Creamos el fichero en el directorio especificado.
        String fileName = Constants.DEST_PATH + "AMORTIZATION_" + Long.toString(System.currentTimeMillis()) + ".pdf";
        File file = new File(fileName);
        file.getParentFile().mkdirs();

        // Inicializamos los componentes necesario para generar el PDF.
        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        // Añadimos gestor de evento de fin de página.
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new EndOfPageHandler());
        document = new Document(pdf);

        // Creamos las fuentes que vamos a usar en el formulario.
        fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        // Mostramos el logo del banco.
        logo();

        // Mostramos el titulo del documento que estamos generando.
        tituloDocumento();

        // Mostramos información básica del cuadro de amortización.
        infoCuadro(ilp);

        // Mostramos el cudro de amortización en una tabla.
        listarCuadro(ilp);
       
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
     * Mostramos el título del documento.
     *
     * @param font
     * @throws IOException
     */
    private void tituloDocumento() throws IOException {
        String titulo = " Cuadro de Amortización de Préstamo ";
        Image icon = new Image(ImageDataFactory.create(PT_ICON));
                    
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
     * Mostramos información básica del cuadro de amortización.
     *
     * @param ilp
     * @throws IOException
     */
    private void infoCuadro(ItxAmortizationParams ilp) throws IOException {
        String info = String.format("Importe: %s / Duración: %d meses / Tipo: %s%% / TAE: %s%%",
                Functions.formatAmount(ilp.getImpPrestamo()),
                ilp.getDuracionMeses(),
                Functions.formatNumber(ilp.getTipoInteres()),
                Functions.formatNumber(Banking.getCompoundTAE(ilp.getTipoInteres())));

        document.add(new Paragraph(info)
                .setFirstLineIndent(12)
                .setMarginTop(5)
                .setFont(fontBold));
    }

    /**
     * Mostramos el cuadro de amortización en una tabla.
     *
     * @param ilp
     * @throws IOException
     */
    private void listarCuadro(ItxAmortizationParams ilp) throws IOException {
        document.add(new Paragraph().setMarginTop(15));
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4, 4, 4, 4})).useAllAvailableWidth();
        table.setTextAlignment(TextAlignment.LEFT).setFont(fontNormal);
        // Cabecera de la tabla.
        table.addHeaderCell(new Cell().add(new Paragraph("Pago").setTextAlignment(TextAlignment.CENTER).setFont(fontBold)));
        table.addHeaderCell(new Cell().add(new Paragraph("Cuota").setTextAlignment(TextAlignment.CENTER).setFont(fontBold)));
        table.addHeaderCell(new Cell().add(new Paragraph("Capital").setTextAlignment(TextAlignment.CENTER).setFont(fontBold)));
        table.addHeaderCell(new Cell().add(new Paragraph("Intereses").setTextAlignment(TextAlignment.CENTER).setFont(fontBold)));
        table.addHeaderCell(new Cell().add(new Paragraph("Pendiente").setTextAlignment(TextAlignment.CENTER).setFont(fontBold)));

        double capitalPendiente = ilp.getImpPrestamo();
        double tipoMensual = ilp.getTipoInteres() / (12 * 100);
        double cuotaMensual = capitalPendiente * (tipoMensual / (1 - Math.pow((1 + tipoMensual), (ilp.getDuracionMeses() * -1))));

        double sumCuotaMensual = 0;
        double sumCapitalAmortizado = 0;
        double sumIntereses = 0;

        // Añadimos los movimientos a la tabla.
        for (int x = 1; x <= ilp.getDuracionMeses(); x++) {
            double intereses = Functions.round(capitalPendiente * tipoMensual, 2);
            double capitalAmortizado = cuotaMensual - intereses;
            capitalPendiente = capitalPendiente - capitalAmortizado;
            // En el último mes, ajustamos los intereses para cuadrar los
            // importe mostrados.
            if (x == ilp.getDuracionMeses()) {
                intereses = intereses - capitalPendiente;
                capitalAmortizado = cuotaMensual - intereses;
                capitalPendiente = 0;
            }

            table.addCell(new Cell().add(new Paragraph(Functions.formatNumber(x)).setTextAlignment(TextAlignment.RIGHT)));
            table.addCell(new Cell().add(new Paragraph(Functions.formatAmount(cuotaMensual)).setTextAlignment(TextAlignment.RIGHT)));
            table.addCell(new Cell().add(new Paragraph(Functions.formatAmount(capitalAmortizado)).setTextAlignment(TextAlignment.RIGHT)));
            table.addCell(new Cell().add(new Paragraph(Functions.formatAmount(intereses)).setTextAlignment(TextAlignment.RIGHT)));
            table.addCell(new Cell().add(new Paragraph(Functions.formatAmount(capitalPendiente)).setTextAlignment(TextAlignment.RIGHT)));

            sumCuotaMensual += cuotaMensual;
            sumCapitalAmortizado += capitalAmortizado;
            sumIntereses += intereses;
        }
        // Fila de totales.
        table.addCell("TOTAL");
        table.addCell(new Cell().add(new Paragraph(Functions.formatAmount(sumCuotaMensual)).setTextAlignment(TextAlignment.RIGHT)));
        table.addCell(new Cell().add(new Paragraph(Functions.formatAmount(sumCapitalAmortizado)).setTextAlignment(TextAlignment.RIGHT)));
        table.addCell(new Cell().add(new Paragraph(Functions.formatAmount(sumIntereses)).setTextAlignment(TextAlignment.RIGHT)));
        table.addCell("");

        // Añadimos la tabla al documento.
        document.add(table);
    }

    protected class EndOfPageHandler implements IEventHandler {

        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int pageNumber = pdfDoc.getPageNumber(page);
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

            // Pie de página
            float plusY = 30;       // Base.
            float lineHeight = 8;   // Altura de línea.
            float footerY = pageSize.getBottom();
            float widthX = pageSize.getWidth();

            Paragraph pTitulo = new Paragraph("*** Condiciones legales de Cuadro de Amortización ***").setTextAlignment(TextAlignment.CENTER).setFont(fontBold).setFixedPosition(0, footerY + plusY, pageSize.getWidth());
            Paragraph pInfoLegal = new Paragraph("Este cuadro de amortización es puramente informativo y no supone obligación contractual para ninguna de las partes.").setTextAlignment(TextAlignment.LEFT).setFixedPosition(pageSize.getLeft() + 35, footerY + plusY - lineHeight, widthX);
            Paragraph pNumPagina = new Paragraph(String.valueOf(pageNumber)).setTextAlignment(TextAlignment.CENTER).setFixedPosition(0, footerY + plusY - lineHeight * 2, widthX);

            Canvas footCanvas = new Canvas(pdfCanvas, pdfDoc.getDefaultPageSize());
            footCanvas
                    .setFont(fontNormal)
                    .setFontSize(8)
                    .add(pTitulo)
                    .add(pInfoLegal)
                    .add(pNumPagina)
                    .close();

            // Marca de agua.
            Canvas canvas = new Canvas(pdfCanvas, page.getPageSize());
            canvas.setFontColor(ColorConstants.LIGHT_GRAY);
            canvas.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(60));
            canvas.setProperty(Property.FONT, fontBold);
            canvas.showTextAligned(new Paragraph("INFORMATIVO"), 298, 421, pdfDoc.getPageNumber(page),
                    TextAlignment.CENTER, VerticalAlignment.MIDDLE, 45);

            pdfCanvas.release();
        }
    }

}
