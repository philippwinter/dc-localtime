package eu.boxwork.dhbw.uebungen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.fail;

public class PerformanceTests {

    private static Logger Log = LogManager.getLogger(PerformanceTests.class);

    public static int rounds = 200;

    @Test
    public void timecheckWithWriting() {
        TestUtils.setUpOutputFile("timecheckWithWriting");

        int gid = 0;
        int pid = 1;
        String text = "";
        text = "Testü";


        long start = Calendar.getInstance().getTimeInMillis();

        Transaction tr = TransactionFactory.getInstance().createTransaction(
                TransactionCreationMethod.FULLDATA,
                Calendar.getInstance().getTime(), gid, pid, text);
        if (!Persistence.getInstance().writeTransaktion(tr)) fail();

        long end = Calendar.getInstance().getTimeInMillis();
        long diff = end - start;

        Log.info("WITH WRITING: 1 round = " + diff + "ms");


        long start2 = Calendar.getInstance().getTimeInMillis();
        for (int i = 0; i < rounds; i++) {
            tr = TransactionFactory.getInstance().createTransaction(
                    TransactionCreationMethod.FULLDATA,
                    Calendar.getInstance().getTime(), gid, pid, text);
            if (!Persistence.getInstance().writeTransaktion(tr)) fail();
        }

        long end2 = Calendar.getInstance().getTimeInMillis();
        long diff2 = end2 - start2;


        Log.info("WITH WRITING: " + rounds + " rounds = " + diff2 + "ms");
    }

    @Test
    public void timecheckOptimisedWithoutWritingLastCached() {
        TestUtils.setUpOutputFile("timecheckOptimisedWithoutWritingLastCached");

        int gid = 0;
        int pid = 1;
        String text = "";
        text = "Testü";


        long start = Calendar.getInstance().getTimeInMillis();

        Transaction tr = TransactionFactory.getInstance().createTransaction(
                TransactionCreationMethod.CASHED_LAST_ENTRY,
                Calendar.getInstance().getTime(), gid, pid, text);
//		if (!eu.boxwork.dhbw.uebungen.Persistence.getInstance().writeTransaktion(tr)) fail();

        long end = Calendar.getInstance().getTimeInMillis();
        long diff = end - start;

        Log.info("OPTIMISED WITHOUT WRITING (cached): 1 round = " + diff + "ms");


        long start2 = Calendar.getInstance().getTimeInMillis();
        for (int i = 0; i < rounds; i++) {
            tr = TransactionFactory.getInstance().createTransaction(
                    TransactionCreationMethod.CASHED_LAST_ENTRY,
                    Calendar.getInstance().getTime(), gid, pid, text);
//			if (!eu.boxwork.dhbw.uebungen.Persistence.getInstance().writeTransaktion(tr)) fail();
        }

        long end2 = Calendar.getInstance().getTimeInMillis();
        long diff2 = end2 - start2;


        Log.info("OPTIMISED WITHOUT WRITING (cached): " + rounds + " rounds = " + diff2 + "ms");
    }

    @Test
    public void timecheckOptimisedWithWritingLastCached() {
        TestUtils.setUpOutputFile("timecheckOptimisedWithWritingLastCached");

        int gid = 0;
        int pid = 1;
        String text = "";
        text = "Testü";


        long start = Calendar.getInstance().getTimeInMillis();

        Transaction tr = TransactionFactory.getInstance().createTransaction(
                TransactionCreationMethod.CASHED_LAST_ENTRY,
                Calendar.getInstance().getTime(), gid, pid, text);
        if (!OptimisedPersistence.getInstance().writeTransaktion(tr)) fail();

        long end = Calendar.getInstance().getTimeInMillis();
        long diff = end - start;

        Log.info("OPTIMISED WITH WRITING (cached): 1 round = " + diff + "ms");


        long start2 = Calendar.getInstance().getTimeInMillis();
        for (int i = 0; i < rounds; i++) {
            tr = TransactionFactory.getInstance().createTransaction(
                    TransactionCreationMethod.CASHED_LAST_ENTRY,
                    Calendar.getInstance().getTime(), gid, pid, text);
            if (!OptimisedPersistence.getInstance().writeTransaktion(tr)) fail();
        }

        long end2 = Calendar.getInstance().getTimeInMillis();
        long diff2 = end2 - start2;


        Log.info("OPTIMISED WITH WRITING (cached): " + rounds + " rounds = " + diff2 + "ms");
    }

    @Test
    public void timecheckOptimisedWithWriting() {
        TestUtils.setUpOutputFile("timecheckOptimisedWithWriting");

        int gid = 0;
        int pid = 1;
        String text = "";
        text = "Testü";


        long start = Calendar.getInstance().getTimeInMillis();

        Transaction tr = TransactionFactory.getInstance().createTransaction(
                TransactionCreationMethod.OPTIMISED_LAST_ENTRY,
                Calendar.getInstance().getTime(), gid, pid, text);
        if (!OptimisedPersistence.getInstance().writeTransaktion(tr)) fail();

        long end = Calendar.getInstance().getTimeInMillis();
        long diff = end - start;

        Log.info("OPTIMISED WITH WRITING: 1 round = " + diff + "ms");


        long start2 = Calendar.getInstance().getTimeInMillis();
        for (int i = 0; i < rounds; i++) {
            tr = TransactionFactory.getInstance().createTransaction(
                    TransactionCreationMethod.OPTIMISED_LAST_ENTRY,
                    Calendar.getInstance().getTime(), gid, pid, text);
            if (!OptimisedPersistence.getInstance().writeTransaktion(tr)) fail();
        }

        long end2 = Calendar.getInstance().getTimeInMillis();
        long diff2 = end2 - start2;


        Log.info("OPTIMISED WITH WRITING: " + rounds + " rounds = " + diff2 + "ms");
    }

    @Test
    public void timecheckWithoutWriting() {
        int gid = 0;
        int pid = 1;
        String text = "";
        text = "Testü";


        long start = Calendar.getInstance().getTimeInMillis();

        Transaction tr = TransactionFactory.getInstance().createTransaction(
                TransactionCreationMethod.FULLDATA,
                Calendar.getInstance().getTime(), gid, pid, text);

        long end = Calendar.getInstance().getTimeInMillis();
        long diff = end - start;

        Log.info("WITHOUT WRITING: 1 round = " + diff + "ms");


        long start2 = Calendar.getInstance().getTimeInMillis();
        for (int i = 0; i < rounds; i++) {
            tr = TransactionFactory.getInstance().createTransaction(
                    TransactionCreationMethod.FULLDATA,
                    Calendar.getInstance().getTime(), gid, pid, text);
        }

        long end2 = Calendar.getInstance().getTimeInMillis();
        long diff2 = end2 - start2;


        Log.info("WITHOUT WRITING: " + rounds + " rounds = " + diff2 + "ms");
    }

}
