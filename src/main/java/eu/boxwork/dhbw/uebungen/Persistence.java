package eu.boxwork.dhbw.uebungen;

/**
 * Klasse, die sich um die Persistierung der Transaktionen kümmert
 */
public class Persistence extends AbstractPersistence {

    /**
     * eu.boxwork.dhbw.uebungen.TransactionFactory wird als Singleton implementiert
     */
    private static Persistence instance = null;

    /**
     * privater Konstruktor, da Singleton
     */
    private Persistence() {
    }

    public static synchronized Persistence getInstance() {
        if (instance == null) instance = new Persistence();
        return instance;
    }


}
