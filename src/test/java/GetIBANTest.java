import utilities.Banking;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class GetIBANTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Comprobaciónn de CCC
        System.out.println(Banking.getCCC(1000, 1000000001));
        System.out.println(Banking.getCCC(1000, 1000000002));
        System.out.println(Banking.getCCC(2001, 1000000003));
        
        // Comprobación de IBAN
        System.out.println(Banking.getIBAN(1000, 1000000001));
        System.out.println(Banking.getIBAN(1000, 1000000002));
        System.out.println(Banking.getIBAN(2001, 1000000003));
        System.out.println(Banking.getIBAN(2002, 1282226178));        
    }

}
