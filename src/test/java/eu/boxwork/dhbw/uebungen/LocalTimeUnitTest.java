package eu.boxwork.dhbw.uebungen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LocalTimeUnitTest {

    private static Logger Log = LogManager.getLogger(LocalTimeUnitTest.class);

    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testRegexSemicolon() {
        assertEquals(false, Localtime.isCleanString("Test;"));
    }

    @Test
    public void testRegexNewLine() {
        assertEquals(false, Localtime.isCleanString("Test\n"));
    }

    @Test
    public void testRegexSpace() {
        assertEquals(true, Localtime.isCleanString("Test Test"));
    }

    @Test
    public void transactionFactoryTestFirst() {
        TestUtils.setUpOutputFile("transactionFactoryTestFirst");

        Date time = new Date(2017, 9, 4, 10, 0, 0);
        int gid = 0;
        int pid = 1;
        String text = "Testü";

        Transaction tr = TransactionFactory.getInstance().createTransaction(
                TransactionCreationMethod.FULLDATA, time, gid, pid, text);
        if (tr != null) {
            assertEquals("267C4D5033ED7F96B43216FD8C871E4B96F1221204312AD6F43362F2D12C9B29", tr.getHash());
        } else fail("no transaction created.");
    }

    @Test
    public void transactionFactoryTestSecond() {
        TestUtils.setUpOutputFile("transactionFactoryTestSecond");

        Date time = new Date(2017, 9, 4, 10, 0, 0);
        int gid = 0;
        int pid = 1;
        String text = "Testü";

        Transaction tr = TransactionFactory.getInstance().createTransaction(TransactionCreationMethod.FULLDATA, time, gid, pid, text);

        if (!Persistence.getInstance().writeTransaktion(tr)) fail();

        time = new Date(2017, 9, 4, 10, 1, 0);
        gid = 0;
        pid = 1;
        text = "Test2";

        tr = TransactionFactory.getInstance().createTransaction(TransactionCreationMethod.FULLDATA, time, gid, pid, text);

        if (!Persistence.getInstance().writeTransaktion(tr)) fail();

        assertEquals("6CB09B876CA855D7F3D8168E2001594E354D2EFEC110F5D5FCED478641E96C9C", tr.getHash());
    }


}
