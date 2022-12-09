import utilities.CheckIdDocuments;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class CheckIdDocumentTest {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // *********** Validación de DNI ***********
        System.out.println("Validación de DNI");
        // DNI que no son válidos por el Ministerio de interior.
        System.out.println("No válidos por Ministerior de Interior");
        System.out.println(CheckIdDocuments.validarDNI("00000000T"));
        System.out.println(CheckIdDocuments.validarDNI("00000001R"));
        System.out.println(CheckIdDocuments.validarDNI("99999999R"));

        // Strings que no cumplen la expresion regular.
        System.out.println("No válidos por no cumplir la expresión regular");
        System.out.println(CheckIdDocuments.validarDNI("0123"));
        System.out.println(CheckIdDocuments.validarDNI("01234a67Z"));
        System.out.println(CheckIdDocuments.validarDNI("012345678-"));
        System.out.println(CheckIdDocuments.validarDNI("0123456789"));

        // DNI que si cumplen todas las validaciones.
        System.out.println("Válidos");
        System.out.println(CheckIdDocuments.validarDNI("12345678Z"));
        System.out.println(CheckIdDocuments.validarDNI("45673254S"));
        System.out.println(CheckIdDocuments.validarDNI("72849506L"));
        System.out.println(CheckIdDocuments.validarDNI("51659507N"));

        // *********** Validación de NIE ***********  
        System.out.println("Validación de NIE");
        // Strings que no cumplen la expresion regular.
        System.out.println("No válidos por no cumplir la expresión regular");
        System.out.println(CheckIdDocuments.validarNIE("Z0123"));
        System.out.println(CheckIdDocuments.validarNIE("B1234a67Z"));
        System.out.println(CheckIdDocuments.validarNIE("Y12345678-"));
        System.out.println(CheckIdDocuments.validarNIE("Z12345678"));

        // NIE que cumplen la expresión regular, pero no la letra de control.
        System.out.println("No válidos por no tener el dígito de control correcto.");
        System.out.println(CheckIdDocuments.validarNIE("X2218960M"));
        System.out.println(CheckIdDocuments.validarNIE("Y2548513J"));
        System.out.println(CheckIdDocuments.validarNIE("X9514689D"));
        System.out.println(CheckIdDocuments.validarNIE("Z7848418G"));

        // NIE que si cumplen todas las validaciones.
        System.out.println("Válidos");
        System.out.println(CheckIdDocuments.validarNIE("X2218960N"));
        System.out.println(CheckIdDocuments.validarNIE("Y2548514J"));
        System.out.println(CheckIdDocuments.validarNIE("X9516489D"));
        System.out.println(CheckIdDocuments.validarNIE("Y7848418G"));

        // *********** Validación de CIF ***********  
        System.out.println("Validación de CIF");
        // CIF que no cumplen la expresion regular.
        System.out.println("No válidos por no cumplir la expresión regular");
        System.out.println(CheckIdDocuments.CheckIdDocument("CIF", "Z0123"));
        System.out.println(CheckIdDocuments.CheckIdDocument("CIF", "B1234a67Z"));
        System.out.println(CheckIdDocuments.CheckIdDocument("CIF", "Y12345678-"));
        System.out.println(CheckIdDocuments.CheckIdDocument("CIF", "Z12345678"));

        // CIF que cumplen la expresión regular, pero no la letra de control.
        System.out.println("No válidos por no tener el dígito de control correcto.");
        System.out.println(CheckIdDocuments.CheckIdDocument("CIF", "B92353467"));
        System.out.println(CheckIdDocuments.validarCIF("A15000127"));
        System.out.println(CheckIdDocuments.validarCIF("P2302900A"));  
        
        // CIF que si cumplen todas las validaciones.
        System.out.println("Válidos");
        System.out.println(CheckIdDocuments.CheckIdDocument("CIF", "B92253467"));
        System.out.println(CheckIdDocuments.validarCIF("A15000128"));
        System.out.println(CheckIdDocuments.validarCIF("P2302900B"));        
    }
    
}
