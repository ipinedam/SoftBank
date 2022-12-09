import java.text.NumberFormat;
import java.util.Scanner;
import utilities.Functions;

public class AmortizationTableTest {

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        System.out.println("Introduce el importe del préstamo (€): ");
        String principalS = console.nextLine();
        System.out.println("Introduce la duración del préstamo, en meses: ");
        String lengthS = console.nextLine();
        System.out.println("Introduce el tipo de interes anual (sin el signo %): ");                
        String interestS = console.nextLine();

        double capitalPendiente = Double.parseDouble(principalS);
        int duracionMeses = Integer.parseInt(lengthS);
        double tipoAnual = Double.parseDouble(interestS);

        double tipoMensual = tipoAnual / (12 * 100);
        double cuotaMensual = capitalPendiente * (tipoMensual / (1 - Math.pow((1 + tipoMensual), (duracionMeses * -1))));
        
        final int PAYMENT_WIDTH = 15;
        final int AMOUNT_WIDTH = 15;
        final int PRINCIPAL_WIDTH = 15;
        final int INTEREST_WIDTH = 15;
        final int BALANCE_WIDTH = 15;

        String pattern = "%" + PAYMENT_WIDTH + "s%" + AMOUNT_WIDTH + "s%" + PRINCIPAL_WIDTH + "s%" + INTEREST_WIDTH + "s%" + BALANCE_WIDTH + "s";

        System.out.printf(pattern, "PAGO", "CUOTA", "CAPITAL", "INTERESES", "PENDIENTE");
        System.out.println();

        NumberFormat nf = NumberFormat.getCurrencyInstance();

        double sumPagoMensual = 0;
        double sumCapital = 0;
        double sumIntereses = 0;
        
        
        for (int x = 1; x <= duracionMeses; x++) {
//            double intereses = capitalPendiente * tipoMensual;
            double intereses = Functions.round((capitalPendiente * tipoMensual), 2);
            double capitalAmortizado = cuotaMensual - intereses;
            capitalPendiente = capitalPendiente - capitalAmortizado;
            if (x == duracionMeses) {
                intereses = intereses - capitalPendiente;
                capitalAmortizado = cuotaMensual - intereses;
                capitalPendiente = 0;
            }

            System.out.printf(pattern, x, nf.format(cuotaMensual), nf.format(capitalAmortizado), nf.format(intereses), nf.format(capitalPendiente));
            System.out.println();
            
            sumPagoMensual += cuotaMensual;
            sumCapital += capitalAmortizado;
            sumIntereses += intereses;
        }
        
        // Resumen de los pagos.
        System.out.printf(pattern, "", "--------------", "--------------", "--------------", "");
        System.out.println();
        System.out.printf(pattern, "TOTAL", nf.format(sumPagoMensual), nf.format(sumCapital), nf.format(sumIntereses), "");
        System.out.println();

        console.close();
    }
}