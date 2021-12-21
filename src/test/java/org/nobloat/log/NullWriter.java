package org.nobloat.log;

import java.io.IOException;

public class NullWriter implements L.Writer {
    @Override
    public void write(L.Level l, CharSequence s) throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
