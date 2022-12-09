package utilities;

import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Clase para crear un {@link DocumentFilter}, aplicable a los objetos {@link JTextField},
 * para conseguir que sólo muestren contenido en mayúsculas.
 * <p>
 * Ejemplo:</p> 
 *      {@code DocumentFilter dfilter = new UpcaseFilter();}
 * <br> {@code ((AbstractDocument) jtextField.getDocument()).setDocumentFilter(dfilter);}
 * 
 * @author Ignacio Pineda Martín
 * @see Document
 */
public class UpcaseFilter extends DocumentFilter {

    @Override
    public void insertString(DocumentFilter.FilterBypass fb, int offset,
            String text, AttributeSet attr) throws BadLocationException {
        fb.insertString(offset, text.toUpperCase(), attr);
    }

    //no need to override remove(): inherited version allows all removals
    @Override
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
            String text, AttributeSet attr) throws BadLocationException {
        fb.replace(offset, length, text.toUpperCase(), attr);
    }

    /**
     * Método para hacer que todos los JTextField de un formulario, 
     * pantalla, etc, muestren su contenido en mayúsculas.
     * 
     * @param container 
     */
    public static void setFieldsToUpperCase(Component container) {
        DocumentFilter df = new UpcaseFilter();     
        for (Component c : Functions.getComponents(container)) {
            if (c instanceof JTextField j)
                ((AbstractDocument) j.getDocument()).setDocumentFilter(df);                
        }
    }
    
}
