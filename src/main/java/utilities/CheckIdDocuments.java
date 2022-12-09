package utilities;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class CheckIdDocuments {
    
    /**
     * <p>Tipos de documentos de identificación para los 
     * {@link model.entity.Cliente Clientes} de la aplicación.</p>
     * 
     * <br>{@code DNI} Para personas físicas nacionales.
     * <br>{@code CIF} Para personas jurídicas.
     * <br>{@code NIE} Para residentes extranjeros.
     * <br>{@code PASAPORTE} Para no residentes extranjeros.
     */    
    public static enum IdDocumentType {
        DNI,
        CIF,
        NIE,
        PASAPORTE
    }
    
    /**
     * Tabla de conversión para la operación módulo 23 en el cálculo DNI y NIE.
     * <table>
     * <tr>|-------|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|</tr>
     * <tr>| Resto | 0| 1| 2| 3| 4| 5| 6| 7| 8| 9|10|11|12|13|14|15|16|17|18|19|20|21|22|</tr>
     * <tr>|-------|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|</tr>
     * <tr>| Letra | T| R| W| A| G| M| Y| F| P| D| X| B| N| J| Z| S| Q| V| H| L| C| K| E|</tr>
     * <tr>|-------|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|</tr>
     * </table>
     */
    private static final String DIGITO_CONTROL = "TRWAGMYFPDXBNJZSQVHLCKE";

    /** 
     * Validación de DNI.
     * 
     * <p>(1) Lo primero que hacemos es asegurarnos de que el String recibido no 
     * es ninguno de los DNI no válidos que fija el Ministerio de Interior.
     * <p>(2) Después, mediante una expresión regular, validamos que el String 
     * tenga 8 números y una letra al final (en este punto, solo estamos 
     * validando que la cadena de texto esté bien formada).
     * <p>(3) Para terminar, no basta con que el último dígito sea una letra, 
     * tiene que cumplir que el último carácter se corresponda con el dígito 
     * de control de la tabla después de aplicar la operación módulo 23 con
     * la parte numérica del DNI (ver tabla de conversión en 
     * {@link DIGITO_CONTROL}).
     * 
     * @param dni El DNI a .
     * @return <b>true</b> El DNI es válido.
     * <br><b>false</b> El DNI NO es válido.
     */
    public static boolean validarDNI(String dni) {
        final Pattern REGEXP = Pattern.compile("[0-9]{8}[A-Z]");
        final String[] INVALIDOS = new String[]{"00000000T", "00000001R", "99999999R"};
        
        return Arrays.binarySearch(INVALIDOS, dni) < 0  // (1)
                && REGEXP.matcher(dni).matches()        // (2)
                && dni.charAt(8) == DIGITO_CONTROL.charAt(Integer.parseInt(dni.substring(0, 8)) % 23); // (3)
    }

    /**
     * Validación de NIE.
     * 
     * <p>(1) Mediante una expresión regular, validamos que el String 
     * comience por X, Y o Z, tenga 7 números y una letra al final (en este 
     * punto, solo estamos validando que la cadena de texto esté bien formada).
     * <p>(2) Substituimos el primer carácter del NIE por un valor, con la 
     * siguiente conversion:
     * <p>X -> 0 | Y -> 1 | Z -> 2
     * <p>Por tanto, ahora tendremos un NIE formado por 8 dígitos (el primer dígito
     * resultado de la conversión, y los 7 restantes los originales).
     * <p>(3) El último carácter debe corresponder con el dígito de control de la 
     * misma tabla usada en la validación del DNI, después de aplicar la 
     * operación módulo 23 con la parte numérica del NIE de 8 dígitos obtenido
     * en el paso (2).
     * 
     * @param nie El NIE a validar.
     * @return <b>true</b> El NIE es válido.
     * <br><b>false</b> El NIE NO es válido.
     */
    public static boolean validarNIE(String nie) {
        final Pattern REGEXP = Pattern.compile("[X-Z][0-9]{7}[A-Z]");
        
        if (!REGEXP.matcher(nie).matches())     // (1)
            return false;
        
        switch (nie.substring(0, 1)) {          // (2)
            case "X" -> nie = "0" + nie.substring(1);
            case "Y" -> nie = "1" + nie.substring(1);
            case "Z" -> nie = "2" + nie.substring(1);
            default -> throw new AssertionError();
        }
        
        return nie.charAt(8) == DIGITO_CONTROL.charAt(Integer.parseInt(nie.substring(0, 8)) % 23); // (3)
    }

    /**
     * Validación de CIF.
     * 
     * <p>(1) Mediante una expresión regular, validamos que el String 
     * comience por ABCDEFGHJKLMNPQRSUVW, tenga 7 números y un número o una 
     * letra del grupo ABCDEFGHIJ al final (en este punto, solo estamos 
     * validando que la cadena de texto esté bien formada).
     * <p>(2) Se cálcula el dígito o carácter de control (en función de la
     * primera letra del CIF) del CIF introducido.
     * <p>(3) Por último, comprobamos que el dígito de control calculado
     * coincide con el proporcionado.
     * 
     * @see <a href="https://www.clasesdeinformaticaweb.com/programas-en-java/cif-calcular-el-cif-en-java/">
     * "Cálcular el CIF en Java"</a>
     * 
     * @param cif
     * @return <b>true</b> El CIF es válido.
     * <br><b>false</b> El CIF NO es válido.
     */
    public static boolean validarCIF(String cif) {
        // Patrón de un código CIF.
        final Pattern REGEXP = Pattern.compile("[A-HJ-NP-SUVW]{1}[0-9]{7}[0-9A-J]{1}");

        /**
         * Tabla de conversión de dígito de control a letra.
         * 
         * <table>
         * <tr>|-------|-|-|-|-|-|-|-|-|-|-|</tr>
         * <tr>| Resto |0|1|2|3|4|5|6|7|8|9|</tr>
         * <tr>|-------|-|-|-|-|-|-|-|-|-|-|</tr>
         * <tr>| Letra |J|A|B|C|D|E|F|G|H|I|</tr>
         * <tr>|-------|-|-|-|-|-|-|-|-|-|-|</tr>
         * </table>
         */
        final String DIGITO_CONTROL_CIF = "JABCDEFGHI";
        /**
         * Códigos CIF cuyo dígito de control se convierte en letra
         * según la tabla anterior.
         */
        final String CIF_LETRA = "KPQRSNW";

        // Si el formato del CIF proporcionado no es correcto, lo
        // rechazamos.
        if (!REGEXP.matcher(cif).matches())     // (1)
            return false;

        // Proseguimos con el cálculo del dígito de control.
        String str = cif.substring(1, 8);       // (2)
        String cabecera = cif.substring(0, 1);
        
        // Cálculo del digito de control según el algoritmo de Luhn.
        int digitoControl = LuhnAlgorithm(str);

        // Si el CIF comienza con alguna de las letras que implica la
        // conversión de digito de control a letra de control, realizamos
        // dicha transformación.
        if (CIF_LETRA.contains(cabecera)) {
            str = "" + DIGITO_CONTROL_CIF.charAt(digitoControl);
        } else {
            str = "" + digitoControl;
        }

        return cif.charAt(8) == str.charAt(0);  // (3)
    }

    /**
     * Validación de DNI, CIF, NIE y PASAPORTE.
     * 
     * @param documentType El tipo de documento a validar.
     * @param documentNumber El número de documento a validar.
     * @return <b>true</b> El documento es válido.
     * <br><b>false</b> El documento NO es válido.
     */
    public static boolean CheckIdDocument(String documentType, String documentNumber) {
        switch (IdDocumentType.valueOf(documentType)) {
            case DNI:
                return validarDNI(documentNumber);
            case CIF:
                return validarCIF(documentNumber);
            case NIE:
                return validarNIE(documentNumber);
            case PASAPORTE:
                return true;                
            default:
                throw new AssertionError();
        }
    }
    
    /**
     * Cálculo de un dígito de control según el algoritmo de Luhn.
     * 
     * @see <a href="https://es.wikipedia.org/wiki/Algoritmo_de_Luhn">
     * "Cálcular el dígito de control con el algoritmo de Luhn"</a>
     * @param str La cadena numérica de la que calcular el dígito de control.
     * @return Dígito de control.
     */
    public static int LuhnAlgorithm(String str) {
        int sumaPar = 0;
        int sumaImpar = 0;
        int sumaTotal;

        for (int i = 1; i < str.length(); i += 2) {
            int aux = Integer.parseInt("" + str.charAt(i));
            sumaPar += aux;
        }

        for (int i = 0; i < str.length(); i += 2) {
            sumaImpar += posicionImpar("" + str.charAt(i));
        }

        sumaTotal = sumaPar + sumaImpar;
        sumaTotal = 10 - (sumaTotal % 10);

        if (sumaTotal == 10) {
            sumaTotal = 0;
        }
        
        return sumaTotal;
    }

    private static int posicionImpar(String str) {
        int aux = Integer.parseInt(str);
        aux = aux * 2;
        aux = (aux / 10) + (aux % 10);

        return aux;
    }

}
