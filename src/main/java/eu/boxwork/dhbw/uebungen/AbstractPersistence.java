package eu.boxwork.dhbw.uebungen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;

/**
 * Klasse, die sich um die Persistierung der Transaktionen kümmert
 */
abstract class AbstractPersistence implements IPersistence {
    public static final String ENCODING = "UTF-8";
    public static final String ENTRY_SEPARATOR = "\n";
    public static final char ENTRY_SEPARATOR_CHAR = '\n';
    protected static Logger Log = LogManager.getLogger(AbstractPersistence.class);
    protected String pathPersistenceFile = "";
    protected File persistFileObject = null;
    protected BufferedWriter bw = null;
    protected BufferedReader br = null;

    /**
     * Datei, in der die Daten gespeichert werden sollen
     *
     * @param pathPersistenceFileIn the pathPersistenceFile to set
     */
    public void setFile(String pathPersistenceFileIn) {
        this.pathPersistenceFile = pathPersistenceFileIn;
        this.persistFileObject = new File(pathPersistenceFileIn);
        // existiert die Datei noch nicht, dann erstelle sie
        if (!this.persistFileObject.exists()) {
            try {
                if (this.persistFileObject.createNewFile()) {
                    Log.debug("File '" + pathPersistenceFile + "' initially created.");
                } else {
                    Log.error("Unable to create file '" + pathPersistenceFile + "'");
                }
            } catch (IOException e) {
                Log.error("Unable to create file '" + pathPersistenceFile + "'" + e.getLocalizedMessage());
            }
        }
    }

    /**
     * üffnet die zuvor gesetzte Datei
     *
     * @param writing <code>true</code>, wenn die Date zum Schreiben geüffnet werden soll, sonst <code>false</code>
     * @return <code>true</code>, wenn erfolgreich geüffnet, sonst <code>false</code>
     */
    protected boolean openFile(boolean writing) {
        try {
            // try to close first, if already open
            close();
            this.persistFileObject = new File(pathPersistenceFile);
            if (!persistFileObject.exists())
                persistFileObject.createNewFile();

            if (writing) {
                if (persistFileObject.canWrite()) {
                    this.bw = new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(persistFileObject, true), ENCODING));
                } else {
                    Log.error("Unable to open file '" + pathPersistenceFile + "' for writing.");
                    return false;
                }
            } else {
                if (persistFileObject.canRead()) {
                    br = new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(persistFileObject), ENCODING));
                } else {
                    Log.error("Unable to open file '" + pathPersistenceFile + "' for reading.");
                    return false;
                }
            }
        } catch (Exception e) {
            Log.error("Unable to open file '" + pathPersistenceFile + "' for writing: " + e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    /**
     * Schließt die Datei
     */
    protected void close() {
        try {
            if (this.bw != null) {
                bw.flush();
                bw.close();
            }
            if (this.br != null) {
                br.close();
            }
        } catch (Exception e) {
            if (e.getLocalizedMessage().equals("Stream closed")) {
            } else
                Log.warn("WARNING while try to close: " + e.getLocalizedMessage());
        }
        this.bw = null;
        this.br = null;
        this.persistFileObject = null;
    }

    /**
     * liest den alten Eintrag ein und gibt den Text zurück
     *
     * @return null im Fehlerfall, sonst "" oder der Text
     */
    public String readTransaktions() {
        String content = "";
        if (openFile(false)) {
            try {
                char[] buff = new char[256];
                int cnt = br.read(buff);
                while (cnt > 0) {
                    char[] line = Arrays.copyOf(buff, cnt);
                    content = content + new String(line);
                    cnt = br.read(buff);
                }
                close();

            } catch (Exception e) {
                Log.error("Unable to read transactions: " + e.getLocalizedMessage());
            }
        } else {
            // Datei kann nicht geüffnet werden.
        }
        Log.debug("Read transaction content: " + content);
        return content;
    }

    /**
     * Schreibt eine Transaktion in die Datei; dazu wird die Datei geüffnet, geschrieben
     * und dann wieder geschlossen
     *
     * @param in {@link Transaction}
     * @return <code>true</code> wenn erfolgreich, sonst <code>false</code>
     */
    public boolean writeTransaktion(Transaction in) {
        String tr = in.getEntry();
        if (openFile(true)) {
            try {
                bw.write(tr + ENTRY_SEPARATOR);
            } catch (IOException e) {
                Log.error("Unable to write to file '" + pathPersistenceFile + "': " + e.getLocalizedMessage());
                return false;
            }
            try {
                bw.flush();
            } catch (IOException e) {
                Log.error("Unable to flush the file '" + pathPersistenceFile + "': " + e.getLocalizedMessage());
                return false;
            }
            Log.info("eu.boxwork.dhbw.uebungen.Transaction written to file '" + pathPersistenceFile + "': " + in.getEntry());

            close();

            return true;
        } else return false;
    }
}
