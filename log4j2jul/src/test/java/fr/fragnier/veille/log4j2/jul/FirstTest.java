package fr.fragnier.veille.log4j2.jul;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

public class FirstTest {

    private final static String LOG_MANAGER_BRIDGE_CLASSNAME = "org.apache.logging.log4j.jul.LogManager";

    private static boolean canLoadBridgeLogManager = false;

    static {
        silentInit();
    }

    private final static Logger JUL_LOGGER = Logger.getLogger(FirstTest.class.getName() + ".jul");

    private final static org.apache.logging.log4j.Logger LOG4J_LOGGER = LogManager.getLogger(FirstTest.class);

    @BeforeClass
    public static void init() throws URISyntaxException {

        checkJul();

        final String resourceName = "/" + FirstTest.class.getName().replaceAll("\\.", "/") +"-log4j2.properties";
        final URL res = FirstTest.class.getResource(resourceName);
        LoggerContext.getContext(false).setConfigLocation(res.toURI());
    }

    @Test
    public void testJulRedirection() {
        JUL_LOGGER.info("toto");

        LOG4J_LOGGER.info("titi");
    }


    private static void silentInit() {
        try {
            ClassLoader.getSystemClassLoader().loadClass(LOG_MANAGER_BRIDGE_CLASSNAME);
            System.setProperty("java.util.logging.manager", LOG_MANAGER_BRIDGE_CLASSNAME);
            canLoadBridgeLogManager = true;
        } catch (ClassNotFoundException e) {
            canLoadBridgeLogManager = false;
        }
    }

    public static void checkJul() {

        if(!canLoadBridgeLogManager) {
            throw new IllegalStateException("Jars not in system class path.");
        }

        java.util.logging.LogManager logManager = java.util.logging.LogManager.getLogManager();
        if (!LOG_MANAGER_BRIDGE_CLASSNAME.equals(logManager.getClass().getName())) {
            throw new IllegalStateException("Found " + logManager.getClass().getName() + " set as launch param instead.");
        }
    }
}
