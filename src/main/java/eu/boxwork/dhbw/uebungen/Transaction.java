package eu.boxwork.dhbw.uebungen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Transaktionsklasse, welche die rundimentüren Funktionen und Eigenschaften einer Transaktion kapselt.
 */
public class Transaction {
    public static final String DATEPATTERN = "ddMMyy-HH:mm:ss";
    protected static Logger Log = LogManager.getLogger(Transaction.class);
    /*
     * Klassenvariable
     * */
    private int id = 0; // ID = 0 ist nicht definiert, wir fangen bei 1 an zu zühlen
    private Date time = Calendar.getInstance().getTime(); // Das Zeitobjekt, dass bereits beim Erstellen des Objekt erstellt wird
    private int gid = 0; // Gruppen-ID
    private int pid = 0; // Prozess-ID
    private String text = ""; // Text, der spüter vom "Nutzer" gesetzt wird
    private String hash = ""; // String-Reprüsentation des SHA256
    private boolean errorParsing = false;

    /**
     * Es gibt einen Default-Header
     */
    public Transaction() {
    }

    /**
     * @param id   ID der Transaktion
     * @param gid  Gruppen-ID
     * @param pid  Prozess-ID
     * @param text Text, der spüter vom "Nutzer" gesetzt wird
     */
    public Transaction(int id, int gid, int pid, String text) {
        super();
        this.id = id;
        this.gid = gid;
        this.pid = pid;
        this.text = text;
    }


    /**
     * @param id   ID der Transaktion
     * @param time Zeitstempel
     * @param gid  Gruppen-ID
     * @param pid  Prozess-ID
     * @param text Text, der spüter vom "Nutzer" gesetzt wird
     */
    public Transaction(int id, Date time, int gid, int pid, String text) {
        super();
        this.id = id;
        this.time = time;
        this.gid = gid;
        this.pid = pid;
        this.text = text;
    }

    /**
     * Erstellt eine Transaktion aus dem Eintrag in einer Datei
     *
     * @param content Inhalt, wie durch getEntry() erstellt.
     */
    public Transaction(String content) {
        this.setEntry(content);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * @return the gid
     */
    public int getGid() {
        return gid;
    }

    /**
     * @param gid the gid to set
     */
    public void setGid(int gid) {
        this.gid = gid;
    }

    /**
     * @return the pid
     */
    public int getPid() {
        return pid;
    }

    /**
     * @param pid the pid to set
     */
    public void setPid(int pid) {
        this.pid = pid;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * Baut den Transaktionseintrag zusammen und gibt diesen zurück.
     * Der HASH-Wert wird hinzugefügt, wenn dieser existent ist. Sonst leer.
     *
     * @return {@link String}, UTF-8 formatiert, da Text schon UTF-8 reinkommet
     */
    public String getEntry() {
        String idString = ("00000000" + id);
        idString = idString.substring(idString.length() - 8, idString.length());

        String timeString = new SimpleDateFormat(DATEPATTERN).format(this.time);

        String gidString = ("00" + gid);
        gidString = gidString.substring(gidString.length() - 2, gidString.length());

        String pidString = ("00" + pid);
        pidString = pidString.substring(pidString.length() - 2, pidString.length());

        String ret = idString + ";" + timeString + ";" + gidString + ";" + pidString + ";" + text + ";" + hash;

        return ret;
    }

    /**
     * Baut den Transaktion aus einem Eintrag zusammen und setzt die Werte hier.
     *
     * @param entry Eintrag, wie aus getEntry() zusammengestellt.
     */
    private void setEntry(String entry) {
        String[] elements = entry.split(";");


        String idString = elements[0];
        this.id = Integer.parseInt(idString);

        String timeString = elements[1];
        DateFormat timeFormat = new SimpleDateFormat(DATEPATTERN);
        try {
            this.time = timeFormat.parse(timeString);
        } catch (ParseException e) {
            Log.error(e.getLocalizedMessage());
            this.errorParsing = true;
        }


        String gidString = elements[2];
        this.gid = Integer.parseInt(gidString);

        String pidString = elements[3];
        this.pid = Integer.parseInt(pidString);

        this.text = elements[4];
        this.hash = elements[5];
    }

    /**
     * zeigt an, ob beim Parsen ein Fehler aufgetreten ist.
     *
     * @return <code>true</code>, wenn die Transaktion nicht korrekt geparsed werden konnte, sonst false
     */
    public boolean isErrorParsing() {
        return errorParsing;
    }

}
