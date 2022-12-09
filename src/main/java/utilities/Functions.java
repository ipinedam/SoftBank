package utilities;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import java.math.BigDecimal;
import java.net.URL;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
//</editor-fold>

/**
 * Clase con utilidades para el desarrollo de la aplicación.
 * 
 * @author Ignacio Pineda Martín
 */
public class Functions {

    /**
     * Función para convertir un objeto {@code Date} a texto en formato
     * "DD/MM/YYYY".
     *
     * @param date la fecha a convertir en formato "DD/MM/YYYY"
     * @return Fecha convertida en cadena con formato "DD/MM/YYYY"
     */
    public static String formatDate(Date date) {
        // Formato de fecha.
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }
    
    /**
     * Función para formatear importes con el signo de moneda €.
     *
     * @param object El objeto numérico a formatear.
     * @return Cadena con el número formateado.
     */
    public static String formatAmount(Object object) {
        NumberFormat nfc = NumberFormat.getCurrencyInstance();
        return nfc.format(object);
    }
    
    /**
     * Función para obtener un número desde una cadena formateada como importe
     * con el signo de moneda.
     *
     * @param value La cadena de origen de información.
     * @return El objeto numérico obtenido.
     */
    public static Number getNumberFromFormattedAmount(String value) {
        Number n = 0;
        try {
            n = NumberFormat.getCurrencyInstance().parse(value);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        return n;
    }
   
    /**
     * Función para formatear numeros.
     *
     * @param object El objeto numérico a formatear.
     * @return Cadena con el número formateado.
     */
    public static String formatNumber(Object object) {
        NumberFormat nfn = NumberFormat.getNumberInstance();
        return nfn.format(object);
    }

    /**
     * Función para obtener un número desde una cadena formateada como importe
     * sin el signo de moneda.
     *
     * @param value La cadena de origen de información.
     * @return El objeto numérico obtenido.
     */
    public static Number getNumberFromFormattedNumber(String value) {
        Number n = 0;
        try {
            n = NumberFormat.getNumberInstance().parse(value);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        return n;
    }
    
    /**
     * Función para obtener un objeto {@link BigDecimal} desde una cadena
     * formateada como importe sin el signo de moneda.
     *
     * @param value La cadena de origen de información.
     * @return El objeto objeto {@link BigDecimal} obtenido.
     */   
    public static BigDecimal getBigDecimalFromFormattedNumber(String value) {
        return new BigDecimal(getNumberFromFormattedNumber(value).toString());
    }
    
    /**
     * Función para redondear un número con el número de decimales especificado.
     *
     * @param number El número a redondear.
     * @param numDecimals El número de decimales a tener en cuenta.
     * @return Número redondeado.
     */
    public static double round(double number, int numDecimals) {
        return Math.round(number * Math.pow(10, numDecimals)) / Math.pow(10, numDecimals);
    }
    
    /**
     * Función para obtener el final del día de hoy ("DD/MM/YYYY 23:59:59.99")
     *
     * @return Objeto {@link Date} con la fecha obtenida.
     */
    public static Date getEndOfDay() {
        return getEndOfDay(new Date());
    }
    
    /**
     * Función para obtener el final del día de una fecha dada ("DD/MM/YYYY
     * 23:59:59.99")
     *
     * @param date La fecha a la que obtener su final de dia.
     * @return Objeto {@link Date} con la fecha obtenida.
     */
    public static Date getEndOfDay(Date date) {
        LocalDateTime endOfDate = getLocalDateFromDate(date).atTime(LocalTime.MAX);
        return Date.from(endOfDate.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Función para obtener el final del mes actual ("DD/MM/YYYY 23:59:59.99")
     *
     * @return Objeto {@link Date} con la fecha obtenida.
     */
    public static Date getEndOfMonth() {
        return getEndOfMonth(new Date());
    }
    
    /**
     * Función para obtener el final del mes de una fecha dada ("DD/MM/YYYY
     * 23:59:59.99")
     *
     * @param date La fecha a la que obtener su final de mes.
     * @return Objeto {@link Date} con la fecha obtenida.
     */
    public static Date getEndOfMonth(Date date) {
        LocalDateTime monthEnd = getLocalDateFromDate(date).atStartOfDay().plusMonths(1).withDayOfMonth(1).minusDays(1);
        LocalDateTime endOfDate = monthEnd.toLocalDate().atTime(LocalTime.MAX);
        return Date.from(endOfDate.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Función para obtener el número de días del mes de una fecha dada.
     *
     * @param date La fecha a la que obtener el número de días del mes.
     * @return El número de días del mes.
     */
    public static long getDaysOfMonth(Date date) {
        return ChronoUnit.DAYS.between(getLocalDateFromDate(date).atStartOfDay().withDayOfMonth(1),
                Functions.getLocalDateFromDate(getEndOfMonth(date)).atStartOfDay()) + 1;
    }
    
    /**
     * Función para convertir información de tipo LocalDate a Date.
     *
     * @param localDate La fecha de tipo LocalDate.
     * @return La misma fecha en tipo Date.
     */
    public static Date getDateFromLocalDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Función para convertir información de tipo Date a LocalDate.
     *
     * @param date La fecha de tipo Date.
     * @return La misma fecha en tipo LocalDate.
     */
    public static LocalDate getLocalDateFromDate(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Función para validar la entrada de números en campos de datos en
     * pantalla.
     * <p>
     * Los caracteres admitidos son {@code 0-9}, el punto y coma decimal, el
     * signo "-", la tecla {@code Backspace} y la tecla {@code Delete}
     *
     * @param e Evento que ha disparado la validación
     * @return <b>true</b> El caracter introducido es válido
     * <br><b>false</b> El caracter introducido NO es válido
     */
    public static boolean checkNumber(KeyEvent e) {
        if (!((e.getKeyChar() >= '0' && e.getKeyChar() <= '9')
                || (Integer.toHexString(e.getKeyChar()).equals(Integer.toHexString(KeyEvent.VK_BACK_SPACE)))
                || (Integer.toHexString(e.getKeyChar()).equals(Integer.toHexString(KeyEvent.VK_DELETE)))
                || (e.getKeyChar() == '-')
                || (e.getKeyChar() == ',')
                || (e.getKeyChar() == '.'))) {
            e.consume();
            return false;
        }
        return true;
    }

    /**
     * Función para validar la entrada de fechas en campos de datos en pantalla.
     * <p>
     * Los caracteres admitidos son {@code 0-9}, {@code /}, la tecla
     * {@code Backspace} y la tecla {@code Delete}.
     *
     * @param e Evento que ha disparado la validación
     * @return <b>true</b> El caracter introducido es válido
     * <br><b>false</b> El caracter introducido NO es válido
     */
    public static boolean checkDate(KeyEvent e) {
        if (!((e.getKeyChar() >= '0' && e.getKeyChar() <= '9')
                || (Integer.toHexString(e.getKeyChar()).equals(Integer.toHexString(KeyEvent.VK_BACK_SPACE)))
                || (Integer.toHexString(e.getKeyChar()).equals(Integer.toHexString(KeyEvent.VK_DELETE)))
                || (e.getKeyChar() == '/'))) {
            e.consume();
            return false;
        }
        return true;
    }

    /**
     * Función para obtener todos los objetos Swing individuales contenidos
     * en un objeto Swing compuesto (p.e: todos los objetos de un JPanel).
     * 
     * @param container El objeto Swing compuesto.
     * @return Lista de objetos individuales contenidos en el objeto pasado 
     * por parámetro, incluido el propio objeto.
     */
    public static Component[] getComponents(Component container) {
        return getComponents(container, true);
    }
    
    /**
     * Función auxiliar para obtener todos los objetos Swing individuales 
     * contenidos en un objeto Swing compuesto (p.e: todos los objetos de 
     * un JPanel).
     * 
     * @param container
     * @param first Si es "true", la lista incluirá el objeto que lo invoca.
     * Si es "false", no se incluirá.
     * @return Lista de objetos individuales contenidos en el objeto pasado 
     * por parámetro.
     */
    private static Component[] getComponents(Component container, boolean first) {
        ArrayList<Component> list = null;

        try {
            list = new ArrayList<Component>(Arrays.asList(
                    ((Container) container).getComponents()));
            for (int index = 0; index < list.size(); index++) {
                for (Component currentComponent : getComponents(list.get(index), false)) {
                    list.add(currentComponent);
                }
            }
        } catch (ClassCastException e) {
            list = new ArrayList<Component>();
        }

        if (first)
            list.add(container);
        
        return list.toArray(new Component[list.size()]);
    }
    
    /**
     * Método para obtener iconos desde el directorio deseado, siempre y cuando
     * esté ubicado en la carpeta "resources" del proyecto.
     *
     * @param pathAndFileName Ruta hacía la imagen/icono deseado.
     * @return Imagen requerida.
     */
    public static Image getImage(final String pathAndFileName) {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(pathAndFileName);
        return Toolkit.getDefaultToolkit().getImage(url);
    }

}
