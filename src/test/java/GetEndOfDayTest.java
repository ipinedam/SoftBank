import java.text.ParseException;
import java.text.SimpleDateFormat;
import utilities.Functions;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class GetEndOfDayTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException {
        // Comprobación de getEndOfDay()
        System.out.println("getEndOfDay() " + Functions.getEndOfDay());
        System.out.println("getEndOfDay() " + Functions.getEndOfDay(new SimpleDateFormat("dd/MM/yyyy").parse("16/06/2022")));        

        // Comprobación de getEndOfMonth()
        System.out.println("getEndOfMonth() " + Functions.getEndOfMonth());
        System.out.println("getEndOfMonth() " + Functions.getEndOfMonth(new SimpleDateFormat("dd/MM/yyyy").parse("16/06/2022")));
        
        // Comprobación de getDaysOfMonth
        System.out.println("getDaysOfMonth() " + Functions.getDaysOfMonth(new SimpleDateFormat("dd/MM/yyyy").parse("15/02/2022")));
        System.out.println("getDaysOfMonth() " + Functions.getDaysOfMonth(new SimpleDateFormat("dd/MM/yyyy").parse("15/02/2024")));
        System.out.println("getDaysOfMonth() " + Functions.getDaysOfMonth(new SimpleDateFormat("dd/MM/yyyy").parse("16/06/2022")));
        System.out.println("getDaysOfMonth() " + Functions.getDaysOfMonth(new SimpleDateFormat("dd/MM/yyyy").parse("01/11/2022")));         
        System.out.println("getDaysOfMonth() " + Functions.getDaysOfMonth(new SimpleDateFormat("dd/MM/yyyy").parse("31/10/2022")));        
    }

}
