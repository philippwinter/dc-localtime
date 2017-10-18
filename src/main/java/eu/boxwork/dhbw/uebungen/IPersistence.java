package eu.boxwork.dhbw.uebungen;

/**
 * Interface, die sich um die Persistierung der Transaktionen kümmert
 */
public interface IPersistence {
    /**
     * Datei, in der die Daten gespeichert werden sollen
     *
     * @param pathPersistenceFileIn the pathPersistenceFile to set
     */
    public void setFile(String pathPersistenceFileIn);

    /**
     * liest den alten Eintrag ein und gibt den Text zurück
     *
     * @return null im Fehlerfall, sonst "" oder der Text
     */
    public String readTransaktions();
}
