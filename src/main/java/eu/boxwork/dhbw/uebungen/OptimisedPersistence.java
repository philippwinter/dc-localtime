package eu.boxwork.dhbw.uebungen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Klasse, die sich um die Persistierung der Transaktionen kümmert, schreibt den
 * aktuellsten Eintrag immer vorne an, damit das Schreiben schnell geht
 */
public class OptimisedPersistence extends AbstractPersistence {
    /**
     * eu.boxwork.dhbw.uebungen.TransactionFactory wird als Singleton implementiert
     */
    private static OptimisedPersistence instance = null;
    File tmp = new File("tmp.txt");

    /**
     * privater Konstruktor, da Singleton
     */
    private OptimisedPersistence() {
    }

    public static synchronized OptimisedPersistence getInstance() {
        if (instance == null) instance = new OptimisedPersistence();
        return instance;
    }

    /**
     * gibt die letzte Transaktion zurück
     *
     * @return {@link Transaction} die letzte Transaktion oder null, wenn keine Transaktion vorhanden
     * ist.
     */
    public Transaction getLastEntry() {
        Transaction ret = null;
        try {
            String content = getLastLine(new File(pathPersistenceFile));

            if (content != null && !content.equals("") && !content.equals(ENTRY_SEPARATOR)) {
                ret = new Transaction(content);
                if (ret.isErrorParsing()) {
                    Log.error("Unable to parse last transaction.");
                }
            }
            close();
        } catch (Exception e) {
            Log.error("Unable to read last transaction: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        if (ret != null) Log.debug("Read last transaction content: " + ret.getEntry());
        else Log.debug("No last transaction read.");
        return ret;
    }

    /**
     * liest die letzte Zeile einer Datei ein
     *
     * @param file Datei
     * @return {@link String} Zeile, oder ""
     */
    private String getLastLine(File file) throws FileNotFoundException, IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        StringBuilder builder = new StringBuilder();
        long length = file.length();
        if (length == 0) {
            randomAccessFile.close();
            return "";
        }
        length--;
        randomAccessFile.seek(length);
        String ret = "";
        for (long seek = length; seek >= 0; --seek) {
            randomAccessFile.seek(seek);
            char c = (char) randomAccessFile.read();
            builder.append(c);
            if (c == ENTRY_SEPARATOR_CHAR) {
                builder = builder.reverse();
                ret = builder.toString();
                break;
            }
        }
        randomAccessFile.close();
        return ret;
    }
}
