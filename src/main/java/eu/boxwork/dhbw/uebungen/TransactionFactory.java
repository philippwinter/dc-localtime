package eu.boxwork.dhbw.uebungen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * diese Klasse ist für die Erstellung der Transaktionen zustündig.
 */
public class TransactionFactory {
    private static final int MAX_IDS = 1000000;
    private static final String HASHTYPE = "SHA-256";
    private static Logger Log = LogManager.getLogger(TransactionFactory.class);
    /**
     * eu.boxwork.dhbw.uebungen.TransactionFactory wird als Singleton implementiert
     */
    private static TransactionFactory instance = null;
    private Transaction lastTransaction = null;

    /**
     * privater Konstruktor, da Singleton
     */
    private TransactionFactory() {
    }

    public static synchronized TransactionFactory getInstance() {
        if (instance == null) instance = new TransactionFactory();
        return instance;
    }

    /**
     * Erstellt eine neue Transaktion unter Verwendung der entsprechenden Methode
     *
     * @param method {@link TransactionCreationMethod} Methode, die verwendet werden soll
     * @param gid    Gruppen-ID
     * @param pid    Prozess-ID
     * @param text   Text
     * @return null im Fehlerfall oder eine valide Transaktion
     */
    public Transaction createTransaction(TransactionCreationMethod method, int gid, int pid, String text) {
        switch (method) {
            case FULLDATA: {
                lastTransaction = null;
                return createTransaction(gid, pid, text);
            }
            case OPTIMISED_LAST_ENTRY: {
                lastTransaction = null;
                return createTransactionOptimisedLastEntry(gid, pid, text);
            }
            case CASHED_LAST_ENTRY: {
                Transaction newTransaction = createTransactionOptimisedLastEntry(gid, pid, text);
                lastTransaction = newTransaction;
                return newTransaction;
            }
            default:
                return null;
        }
    }

    /**
     * Erstellt eine neue Transaktion unter Verwendung der entsprechenden Methode
     *
     * @param method {@link TransactionCreationMethod} Methode, die verwendet werden soll
     * @param gid    Gruppen-ID
     * @param pid    Prozess-ID
     * @param text   Text
     * @return null im Fehlerfall oder eine valide Transaktion
     */
    public Transaction createTransaction(TransactionCreationMethod method, Date time, int gid, int pid, String text) {
        switch (method) {
            case FULLDATA: {
                lastTransaction = null;
                return createTransaction(time, gid, pid, text);
            }
            case OPTIMISED_LAST_ENTRY: {
                lastTransaction = null;
                return createTransactionOptimisedLastEntry(time, gid, pid, text);
            }
            case CASHED_LAST_ENTRY: {
                Transaction newTransaction = createTransactionOptimisedLastEntry(time, gid, pid, text);
                lastTransaction = newTransaction;
                return newTransaction;
            }
            default:
                return null;
        }
    }

    /**
     * Erstellt eine neue Transaktion
     *
     * @param gid  Gruppen-ID
     * @param pid  Prozess-ID
     * @param text Text
     * @return null im Fehlerfall oder eine valide Transaktion
     */
    private Transaction createTransaction(int gid, int pid, String text) {
        int id = getNextLocalID();
        if (id == 0) {
            // error, no id set
            return null;
        }
        if (gid > 99 || gid < 0) {
            Log.error("Unable to create transaction, GID must be between 0-99");
            return null;
        }
        if (pid > 99 || pid < 0) {
            Log.error("Unable to create transaction, PID must be between 0-99");
            return null;
        }
        Transaction trans = new Transaction(id, gid, pid, text);
        if (!addHash(trans)) {
            return null;
        }

        return trans;
    }

    /**
     * Erstellt eine neue Transaktion, optimierte Version,
     * in der nur der HASH des letzten Eintrags verwendet wird
     *
     * @param gid  Gruppen-ID
     * @param pid  Prozess-ID
     * @param text Text
     * @return null im Fehlerfall oder eine valide Transaktion
     */
    private Transaction createTransactionOptimisedLastEntry(int gid, int pid, String text) {
        if (lastTransaction == null) {
            lastTransaction = OptimisedPersistence.getInstance().getLastEntry();
        }

        if (lastTransaction != null && lastTransaction.isErrorParsing()) {
            Log.error("Unable to create new transaction due to error parsing last transaction.");
            return null;
        }
        int id = getNextLocalID(lastTransaction);
        if (id == 0) {
            // error, no id set
            return null;
        }
        if (gid > 99 || gid < 0) {
            Log.error("Unable to create transaction, GID must be between 0-99");
            return null;
        }
        if (pid > 99 || pid < 0) {
            Log.error("Unable to create transaction, PID must be between 0-99");
            return null;
        }
        Transaction trans = new Transaction(id, gid, pid, text);
        if (!addHash(trans, lastTransaction)) {
            Log.error("Unable to create new transaction due to error adding hash.");
            return null;
        }

        return trans;
    }

    /**
     * Fügt den korrekten HASH-Wert hinzu, nimmt dazu den HASH der alten Transaktion, sofern vorhanden
     *
     * @param trans     Transaktion
     * @param lastTrans letzte Transaktion, wenn null, dann gab es kein und es ist der erste Eintrag
     * @return <code>true</code>, wenn der HASH-Wert berechnet werden konnte, sonst <code>false</code>
     */
    private boolean addHash(Transaction trans, Transaction lastTrans) {
        String hashinput = trans.getEntry();
        if (lastTrans != null) {
            Log.debug("last hash input: " + lastTrans.getHash());
            hashinput = hashinput + lastTrans.getHash();
        } else {
            // es wird nicht hinzugefügt.
        }


        try {
            byte[] bytes = hashinput.getBytes(AbstractPersistence.ENCODING);
            Log.debug("hash input: " + hashinput);

            String dataStringLowerCase = Hex.toHexString(bytes);
            Log.debug("creating hash for Input: " + dataStringLowerCase.toUpperCase());

            MessageDigest md = MessageDigest.getInstance(HASHTYPE);
            md.update(bytes);
            byte[] digest = md.digest();
            md.reset();

            String hashStringLowerCase = Hex.toHexString(digest);
            String hashStringUpperCase = hashStringLowerCase.toUpperCase();

            Log.debug("created hash for Input: " + hashStringUpperCase);

            trans.setHash(hashStringUpperCase);
            return true;
        } catch (UnsupportedEncodingException e) {
            Log.error("Unable to get bytes for hash: " + e.getLocalizedMessage());
            return false;
        } catch (NoSuchAlgorithmException e) {
            Log.error("Unable to get bytes for hash: " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Fügt den korrekten HASH-Wert hinzu
     *
     * @param trans Transaktion
     * @return <code>true</code>, wenn der HASH-Wert berechnet werden konnte, sonst <code>false</code>
     */
    private boolean addHash(Transaction trans) {
        // add hash-value
        String lasttransactions = Persistence.getInstance().readTransaktions();
        if (lasttransactions != null) {
            String hashinput = lasttransactions + trans.getEntry();
            try {
                byte[] bytes = hashinput.getBytes(AbstractPersistence.ENCODING);
                Log.debug("hash input: " + hashinput);

                String dataStringLowerCase = Hex.toHexString(bytes);
                Log.debug("creating hash for Input: " + dataStringLowerCase.toUpperCase());

                MessageDigest md = MessageDigest.getInstance(HASHTYPE);
                md.update(bytes);
                byte[] digest = md.digest();
                md.reset();

                String hashStringLowerCase = Hex.toHexString(digest);
                String hashStringUpperCase = hashStringLowerCase.toUpperCase();

                Log.debug("created hash for Input: " + hashStringUpperCase);

                trans.setHash(hashStringUpperCase);
                return true;
            } catch (UnsupportedEncodingException e) {
                Log.error("Unable to get bytes for hash: " + e.getLocalizedMessage());
                return false;
            } catch (NoSuchAlgorithmException e) {
                Log.error("Unable to get bytes for hash: " + e.getLocalizedMessage());
                return false;
            }
        } else {
            Log.error("Unable to set hash. Last transactions not read.");
            return false; // ERROR
        }
    }

    /**
     * Erstellt eine neue Transaktion
     *
     * @param time timestamp to set
     * @param gid  Gruppen-ID
     * @param pid  Prozess-ID
     * @param text Text
     * @return null im Fehlerfall oder eine valide Transaktion
     */
    private Transaction createTransaction(Date time, int gid, int pid, String text) {
        int id = getNextLocalID();
        if (id == 0) {
            // error, no id set
            return null;
        }
        if (time == null) {
            // error, no time set
            return null;
        }
        if (gid > 99 || gid < 0) {
            Log.error("Unable to create transaction, GID must be between 0-99");
            return null;
        }
        if (pid > 99 || pid < 0) {
            Log.error("Unable to create transaction, PID must be between 0-99");
            return null;
        }
        Transaction trans = new Transaction(id, time, gid, pid, text);
        if (!addHash(trans)) {
            Log.error("Unable to add hash to transaction.");
            return null;
        }
        return trans;
    }

    /**
     * Erstellt eine neue Transaktion
     *
     * @param time timestamp to set
     * @param gid  Gruppen-ID
     * @param pid  Prozess-ID
     * @param text Text
     * @return null im Fehlerfall oder eine valide Transaktion
     */
    private Transaction createTransactionOptimisedLastEntry(Date time, int gid, int pid, String text) {
        if (lastTransaction == null) {
            lastTransaction = OptimisedPersistence.getInstance().getLastEntry();
        }

        if (lastTransaction != null && lastTransaction.isErrorParsing()) {
            Log.error("Unable to create new transaction due to error parsing last transaction.");
            return null;
        }
        int id = getNextLocalID(lastTransaction);
        if (id == 0) {
            // error, no id set
            return null;
        }
        if (time == null) {
            // error, no time set
            return null;
        }
        if (gid > 99 || gid < 0) {
            Log.error("Unable to create transaction, GID must be between 0-99");
            return null;
        }
        if (pid > 99 || pid < 0) {
            Log.error("Unable to create transaction, PID must be between 0-99");
            return null;
        }
        Transaction trans = new Transaction(id, time, gid, pid, text);
        if (!addHash(trans, lastTransaction)) {
            Log.error("Unable to add hash to transaction.");
            return null;
        }
        return trans;
    }

    /**
     * bestimmt die nüchste freie ID
     *
     * @param lastTransaction letzte Transaktion
     * @return gibt die nüchste freie ID zurück, oder 0 im Fehlerfall
     */
    private int getNextLocalID(Transaction lastTransaction) {
        if (lastTransaction == null) return 1;
        int lastID = lastTransaction.getId();
        int ret = lastID + 1;
        ret = (ret % MAX_IDS); // Sicherstellen, das der überlauf funktioniert
        if (ret == 0) ret = 1;
        Log.debug("Next ID is: " + ret);
        return ret;
    }

    /**
     * bestimmt die nüchste freie ID
     *
     * @return gibt die nüchste freie ID zurück, oder 0 im Fehlerfall
     */
    private int getNextLocalID() {
        int ret = 0;
        String transactions = Persistence.getInstance().readTransaktions();
        if (transactions != null) {
            if ("".equals(transactions)) {
                ret = 1; // erste Transaktion
            } else {
                String[] transactionlist = transactions.split(AbstractPersistence.ENTRY_SEPARATOR);
                if (transactionlist.length > 0) {
                    String lastTransaction = transactionlist[transactionlist.length - 1];
                    Transaction lastTr = new Transaction(lastTransaction);
                    ret = getNextLocalID(lastTr);
                } else {
                    Log.error("Unable to parse past transactions.");
                }
            }
        } else {
            Log.error("Unable to get next ID. Unable to read transaction file.");
        }
        return ret;
    }
}
