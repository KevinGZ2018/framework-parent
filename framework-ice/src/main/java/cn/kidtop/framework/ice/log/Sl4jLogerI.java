package cn.kidtop.framework.ice.log;

import Ice.Logger;
import org.slf4j.LoggerFactory;

public class Sl4jLogerI implements Ice.Logger {
    private final org.slf4j.Logger logger;

    public Sl4jLogerI(String loggerName) {
        logger = LoggerFactory.getLogger(loggerName);
    }

    @Override
    public void print(String message) {
        logger.info(message);

    }

    @Override
    public void trace(String category, String message) {
        logger.debug(category + " " + message);
    }

    @Override
    public void warning(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message) {
        logger.error(message);

    }

    @Override
    public Logger cloneWithPrefix(String prefix) {
        return new Sl4jLogerI(prefix);
    }

    @Override
    public String getPrefix() {
        // TODO Auto-generated method stub
        return null;
    }

}
