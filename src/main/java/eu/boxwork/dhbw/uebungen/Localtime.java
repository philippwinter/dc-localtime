package eu.boxwork.dhbw.uebungen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Einfache Klasse, welche die lokale Zeit ausgibt.
 */
public class Localtime {
    private static final int GID = 00;
    private static final int PID = 00;
    private static Logger Log = LogManager.getLogger(Localtime.class);

    /**
     * Main
     */
    public static void main(String[] args) {
        try {
            // create persistence
            Persistence.getInstance().setFile("transactions.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in, AbstractPersistence.ENCODING));
            System.out.print("Enter String: ");
            String s = br.readLine();
            while (s != null && !("".equals(s))) {
                if (isCleanString(s)) {
                    // create eu.boxwork.dhbw.uebungen.Transaction
                    Transaction tr = TransactionFactory.getInstance().createTransaction(
                            TransactionCreationMethod.FULLDATA,
                            GID, PID, s);
                    if (tr != null) {
                        Log.debug("created transaction.");
                        if (Persistence.getInstance().writeTransaktion(tr)) {
                            System.out.println("eu.boxwork.dhbw.uebungen.Transaction written: " + tr.getEntry());
                            Log.debug("transaction sucessfully written.");
                        } else {
                            System.out.println("eu.boxwork.dhbw.uebungen.Transaction NOT written: " + tr.getEntry());
                        }
                    } else {
                        // no transaction created
                    }
                } else {
                    Log.error("unable to create eu.boxwork.dhbw.uebungen.Transaction, input not valid");
                }
                System.out.print("Enter String: ");
                s = br.readLine();
            }

            System.out.println("");
            System.out.println("Reader done. Terminating");

        } catch (Exception e) {
            Log.debug(e.getLocalizedMessage());
            System.exit(-1);
        }

    }

    /**
     * gibt zurück, ob der String korrekt ist und alle validen Zeichen vorhanden sind.
     * Es werden \r \n \t und ; ausgeklammert, diese sind nicht gültig.
     *
     * @param in {@link String}, der getestet werden soll.
     * @return <code>true</code>, wenn alles OK, sonst <code>false</code>
     */
    public static boolean isCleanString(String in) {
        String regex = "[^\\r\\n\\t;]+";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(in);
        boolean match = matcher.matches();
        return match;
    }
}
