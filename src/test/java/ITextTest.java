import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
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
import javax.persistence.EntityManagerFactory;
import model.DAO.ProductoBancarioDAO;
import model.entity.Cliente;
import model.entity.Direccion;
import model.entity.Empleado;
import model.entity.Movimiento;
import model.entity.ProductoBancario;
import model.entity.Sucursal;
import utilities.Banking;
import utilities.DBConnection;
import utilities.Functions;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class ITextTest {

    public static final String SOFTBANK_IMG = "src/main/resources/icons/SoftBank 200x54.png";
    public static final String CC_ICON = "src/main/resources/icons/cuenta-corriente 24px.png";    

    public static final String DEST_PATH = "results/test/";

    public static void main(String args[]) throws IOException {
        String fileName = DEST_PATH + Long.toString(System.currentTimeMillis()) + ".pdf";        
        File file = new File(fileName);
        file.getParentFile().mkdirs();

        EntityManagerFactory emf = new DBConnection().getEntityManagerFactory();
        ProductoBancarioDAO pbdao = new ProductoBancarioDAO(emf);

        pbdao.buscarProductoBancario(1);
        ProductoBancario pb = pbdao.getProductoBancario();
        Cliente cliente = pb.getClienteList().get(0);
        Sucursal sucursal;
        Direccion direccion;
        Empleado empleado = cliente.getEmpleado();
        
        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(fileName);

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);

        // Initialize document
        Document document = new Document(pdf);

        // Compose Paragraph
        Image softBank = new Image(ImageDataFactory.create(SOFTBANK_IMG));

        Paragraph logo = new Paragraph()
                .add(softBank)
                .setMarginBottom(10);
        // Add Paragraph to document
        document.add(logo);

        // Create a PdfFont
        PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
 
        // Párrafo de Sucursal
        sucursal = pb.getSucursal();            
        direccion = sucursal.getDireccion();
        document.add(new Paragraph().setMarginTop(5));
        // Create a List
        List datosSucursal = new List()
                .setSymbolIndent(12)
                .setListSymbol("")
                .setFont(fontNormal);

        // Add ListItem objects
        datosSucursal.add(new ListItem(String.format("Sucursal (%s) %s", sucursal.getCodSucursal(), sucursal.getNombreSucursal())))
            .add(new ListItem(String.format("%s %s, %s", direccion.getTipoVia(), direccion.getNombreVia(), direccion.getNumero().stripTrailingZeros().toPlainString())))
            .add(new ListItem(String.format("%05d %s", direccion.getCodPostal(), direccion.getPoblacion())))
            .add(new ListItem(String.format("%s - %s", direccion.getProvinciaEstado(), direccion.getPais())));
        // Add the datosCliente
        document.add(datosSucursal);       
        
        // Párrafo de tipo de producto.
        Image ccIcon = new Image(ImageDataFactory.create(CC_ICON));
        Paragraph p = new Paragraph("")
                .add(ccIcon)
                .add(" Extracto de Cuenta Corriente")
                .setFont(fontBold)
                .add(ccIcon)
                .setTextAlignment(TextAlignment.CENTER);
        p.setMarginTop(10);
        // Add Paragraph to document
        document.add(p);

        // Párrafo del Cliente.
        direccion = cliente.getDireccion();
        document.add(new Paragraph().setMarginTop(10));
        // Create a List
        List datosCliente = new List()
                .setSymbolIndent(12)
                .setListSymbol("")
                .setFont(fontNormal);

        // Add ListItem objects
        datosCliente.add(new ListItem("D./DÑA. " + cliente.getNombreCliente() + " " + cliente.getApellidosCliente()))
            .add(new ListItem(String.format("%s %s, %s", direccion.getTipoVia(), direccion.getNombreVia(), direccion.getNumero().stripTrailingZeros().toPlainString())))
            .add(new ListItem(String.format("%05d %s", direccion.getCodPostal(), direccion.getPoblacion())))
            .add(new ListItem(String.format("%s - %s", direccion.getProvinciaEstado(), direccion.getPais())));
        // Add the datosCliente
        document.add(datosCliente);

        // Información de la cuenta.
        document.add(new Paragraph("IBAN: " + Banking.fourDigitGroup(pb.getCuentaCorriente().getIban()))
                .setFirstLineIndent(12)
                .setMarginTop(5)
                .setFont(fontBold));

        // Párrafo del Empleado.
        sucursal = empleado.getSucursal();
        // Create a List
//        List datosEmpleado = new List()
//                .setSymbolIndent(12)
//                .setListSymbol("")
//                .setFont(fontNormal);        
        // Add ListItem objects
//        datosEmpleado.add(new ListItem(String.format("Su gestor: %s %s", empleado.getNombreEmpleado(), empleado.getApellidosEmpleado())))
//            .add(new ListItem(String.format("(%s) %s", sucursal.getCodSucursal(), sucursal.getNombreSucursal())));
        
//        datosEmpleado.add(new ListItem(String.format("Su gestor: %s %s, en sucursal (%s) %s", empleado.getNombreEmpleado(), empleado.getApellidosEmpleado(), sucursal.getCodSucursal(), sucursal.getNombreSucursal())));

        document.add(new Paragraph(String.format("Su gestor: %s %s, en sucursal (%s) %s", empleado.getNombreEmpleado(), empleado.getApellidosEmpleado(), sucursal.getCodSucursal(), sucursal.getNombreSucursal()))
                .setFirstLineIndent(12)
                .setFont(fontNormal));
        
        // Add the datosEmpleado
//        document.add(datosEmpleado);       
        
        // Añadimos una tabla
        document.add(new Paragraph().setMarginTop(15));
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 5, 2, 2})).useAllAvailableWidth();
        table.setTextAlignment(TextAlignment.LEFT).setFont(fontNormal);
        
        // Cabecera de la tabla.
        table.addHeaderCell(new Cell().add(new Paragraph("Fecha").setTextAlignment(TextAlignment.CENTER).setFont(fontBold)));
        table.addHeaderCell(new Cell().add(new Paragraph("Concepto").setTextAlignment(TextAlignment.CENTER).setFont(fontBold)));
        table.addHeaderCell(new Cell().add(new Paragraph("Imp. Deudor").setTextAlignment(TextAlignment.CENTER).setFont(fontBold)));
        table.addHeaderCell(new Cell().add(new Paragraph("Imp. Acreedor").setTextAlignment(TextAlignment.CENTER).setFont(fontBold)));

        for (Movimiento m : pb.getMovimientoList()) {
            table.addCell(Functions.formatDate(m.getFecMovimiento()));
            table.addCell(m.getConcepto());
            BigDecimal imp = m.getImpMovimiento();
            if (imp.compareTo(BigDecimal.ZERO) < 0) {
                table.addCell(new Cell().add(new Paragraph(Functions.formatAmount(imp)).setTextAlignment(TextAlignment.RIGHT)));
                table.addCell("");
            } else {
                table.addCell("");
                table.addCell(new Cell().add(new Paragraph(Functions.formatAmount(imp)).setTextAlignment(TextAlignment.RIGHT)));
            }
        }
        
        document.add(table);

        //Close document
        document.close();

        // Abrimos el fichero creado.
        Desktop dt = Desktop.getDesktop();
        dt.open(file);
    }

}
