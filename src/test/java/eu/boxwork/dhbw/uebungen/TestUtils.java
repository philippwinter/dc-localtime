package eu.boxwork.dhbw.uebungen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class TestUtils {

    private static Logger Log = LogManager.getLogger(TestUtils.class);

    public static void setUpOutputFile(String name) {
        String filename = "results/junit_transactions_" + name + ".txt";
        File f = new File(filename);
        if (f.exists()) {
            boolean deleted = f.delete();
            Log.info("cleaned result file: " + deleted);
        } else {
            boolean created = false;
            try {
                created = f.createNewFile();
            } catch (IOException e) {
                Log.error("Could not create output file", e);
            }
            Log.info("created new file: " + created + " at path " + f.getAbsolutePath());
        }
        Persistence.getInstance().setFile(filename);
        OptimisedPersistence.getInstance().setFile(filename);
    }

}
