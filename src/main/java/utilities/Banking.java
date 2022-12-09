package utilities;

import java.math.BigInteger;

import static utilities.Constants.COD_BANCO_SOFTBANK;
import static utilities.Constants.COD_PAIS_SOFTBANK;

/**
 * Clase con funciones específicas de la actividad bancaria.
 *
 * @author Ignacio Pineda Martín
 */
public class Banking {

    /**
     * Cálculo del CCC (Código de Cuenta de Cliente).
     * 
     * @see <a href="https://es.wikipedia.org/wiki/C%C3%B3digo_cuenta_cliente">
     * "Cálcular el CCC"</a>
     * 
     * @param iCodSucursal El código de sucursal de la cuenta.
     * @param iNumeroCuenta El número de cuenta.
     * @return Cadena de 20 carácteres compuesta de: Banco (4) + Sucursal (4) +
     * Dígitos de control (2) + Número de cuenta (10)
     */
    public static String getCCC(int iCodSucursal, int iNumeroCuenta) {
        // Variables auxiliares para obtener el CCC.
        String digitosControl;
        String codSucursal;
        String numeroCuenta;
        int nResto;
        // Factores asociados a las posiciones.
        int aPesos[] = {1, 2, 4, 8, 5, 10, 9, 7, 3, 6};
        
        // Convertimos el código de sucursal a una cadena de 4 carácteres.
        codSucursal = String.format("%04d", iCodSucursal);
        // Convertirmos el número de cuenta a una cadena de 10 caracteres.
        numeroCuenta = String.format("%010d", iNumeroCuenta);
        
        // Obtención del digito de control asociado a Banco + Sucursal.
        // Cómo son sólo 8 carácteres, sólo se usan los factores de las 
        // posiciones 3 a la 10 (índices 2 a 9).
        digitosControl = "";
        nResto = 0;
        for (int nItem = 0; nItem < (COD_BANCO_SOFTBANK + codSucursal).length(); nItem++) {
            nResto += Integer.parseInt((COD_BANCO_SOFTBANK + codSucursal).substring(nItem, nItem+1)) * aPesos[nItem + 2];
        }

        nResto = 11 - (nResto % 11);
        if (nResto == 11) {
            nResto = 0;
        } else if (nResto == 10) {
            nResto = 1;
        }
        digitosControl = String.valueOf(nResto);    

        // Obtención del dígito de control asociado a número de cuenta.
        // Ahora si usamos todos los factores, ya que el número de cuenta tiene 
        // 10 carácteres.
        nResto = 0;
        for (int nItem = 0; nItem < numeroCuenta.length(); nItem++) {
            nResto += Integer.parseInt(numeroCuenta.substring(nItem, nItem+1)) * aPesos[nItem];
        }

        nResto = 11 - (nResto % 11);
        if (nResto == 11) {
            nResto = 0;
        } else if (nResto == 10) {
            nResto = 1;
        }
        digitosControl = digitosControl + String.valueOf(nResto);

        // Devolvemos el CCC calculado.
        return String.format("%s%s%s%s", COD_BANCO_SOFTBANK, codSucursal, digitosControl, numeroCuenta);
    }
    
    /**
     * Cálculo del IBAN (International Bank Account Number).
     * 
     * @see <a href="https://medium.com/@manuelmato/c%C3%B3mo-validar-una-cuenta-bancaria-en-java-49268b2596c6">
     * "Cálcular el IBAN"</a>
     * 
     * @param codSucursal El código de sucursal de la cuenta.
     * @param numeroCuenta El número de cuenta.
     * @return Cadena de 24 carácteres compuesta de: "ES" (2) + 
     * Dígitos de control (2) + CCC (20)
     */
    public static String getIBAN(int codSucursal, int numeroCuenta) {
        // Obtenemos el CCC.
        String CCC = getCCC(codSucursal, numeroCuenta);
        // Sumamos la cadena numérica correspondiente a "ES".
        String preIBAN = CCC + "142800";
        
        // Calculamos el módulo 97 de la cifra de 26 dígitos obtenida, y lo
        // restamos de 98 para obtener los dígitos de control.        
        int resto = (new BigInteger(preIBAN).mod(new BigInteger("97"))).intValue();
        int dc = 98 - resto;
        
        // Devolvemos el IBAN calculado.
        return String.format("%s%s%s", COD_PAIS_SOFTBANK, String.format("%02d", dc), CCC);
    }
    
    /**
     * Cálculo del PAN (Personal Account Number) de una tarjeta. Para obtener el
     * PAN tendremos que calcular el dígito de control según el algoritmo de
     * Luhn.
     *
     * @param numeroTarjeta El número de tarjeta.
     * @return Cadena de 16 carácteres compuesta de: "5" (1) + Código de entidad
     * (4) + Nº de tarjeta (10) + Dígito de control (1)
     */
    public static long getPAN(long numeroTarjeta) {
        // Componemos la cadena de la que hay que obtener su dígito de control.
        // El dígito "5" corresponde al sistema MASTERCARD.
        String prePAN = "5" + COD_BANCO_SOFTBANK + String.format("%10d", numeroTarjeta);
        int digitoControl = CheckIdDocuments.LuhnAlgorithm(prePAN);
        return Long.parseLong(prePAN + digitoControl);
    }
    
    /**
     * Función para separar una cadena en grupos de 4 dígitos. Utilizada para
     * mostrar el IBAN y el PAN de forma más clara.
     *
     * @param in La cadena a separar.
     * @return La cadena original, separada en grupos de 4 dígitos.
     */
    public static String fourDigitGroup(String in) {
        String fdg = "";
        String[] arrSplit = in.split("(?<=\\G.{4})");
        for (String s : arrSplit) {
            fdg = fdg + (s) + " ";
        }
        return fdg.trim();
    }

    /**
     * Función para separar una cadena en grupos de 4 dígitos. Utilizada para
     * mostrar el IBAN y el PAN de forma más clara.
     *
     * @param in La cadena a separar.
     * @return La cadena original, separada en grupos de 4 dígitos.
     */    
    public static String fourDigitGroup(long in) {
        return fourDigitGroup(Long.toString(in));
    }
    
    /**
     * Función para calcular el TAE (Tasa Anual Equivalente) en el tipo de
     * interes de las cuentas corrientes y tarjetas de crédito.
     *
     * @param tipoInteres El tipo de interes de la tarjeta.
     * @return El TAE correspondiente al tipo dado.
     */
    public static double getTAE(double tipoInteres) {
        return Functions.round(tipoInteres * 365 / 360, 2);
    }

    /**
     * Función para calcular el TAE (Tasa Anual Equivalente) en el tipo de
     * interes de los préstamos (amortización francesa).
     *
     * @param tipoInteres El tipo de interes de la tarjeta.
     * @return El TAE correspondiente al tipo dado.
     */
    public static double getCompoundTAE(double tipoInteres) {
        return Functions.round((Math.pow((1 + tipoInteres/1200), 12) - 1) * 100, 2);        
    }
    
}
