import utilities.Banking;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class GetPANTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Comprobación de PAN
        System.out.println(Banking.getPAN(4000000001L));
        System.out.println(Banking.getPAN(4000000002L));
        System.out.println(Banking.getPAN(4000000003L));
    }

}
