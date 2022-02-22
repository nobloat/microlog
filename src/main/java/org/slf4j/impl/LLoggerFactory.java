package org.slf4j.impl;

import org.nobloat.log.L;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;


public class LLoggerFactory implements ILoggerFactory {

    static {
        L.SKIP_STACK_ELEMENTS = 3;
    }

    @Override
    public Logger getLogger(String name) {
        return new LLoggerAdapter(name);
    }
}
