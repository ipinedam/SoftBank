import utilities.Functions;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class RoundTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Comprobación de round()
        System.out.println("round(123.4567, 2) " + Functions.round(123.4567, 2));
        System.out.println("round(123.4567, 3) " + Functions.round(123.4567, 3));
        System.out.println("round(11.382938293292, 2) " + Functions.round(11.382938293292, 2));
        System.out.println("round(11.382938293292, 4) " + Functions.round(11.382938293292, 4));
    }

}
