package org.slf4j.impl;

import org.nobloat.log.L;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class LLoggerAdapter implements Logger {
        String name;

        public LLoggerAdapter(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isTraceEnabled() {
            return L.minLevel == L.Level.TRACE;
        }

        @Override
        public void trace(String s) {
            L.trace(s);
        }

        @Override
        public void trace(String s, Object o) {
            L.trace(s, o);
        }

        @Override
        public void trace(String s, Object o, Object o1) {
            L.trace(s, o, o1);
        }

        @Override
        public void trace(String s, Object... objects) {
            L.trace(s, objects);
        }

        @Override
        public void trace(String s, Throwable throwable) {
            L.trace(s, throwable);
        }

        @Override
        public boolean isTraceEnabled(Marker marker) {
            return isTraceEnabled();
        }

        @Override
        public void trace(Marker marker, String s) {
            L.trace(s);
        }

        @Override
        public void trace(Marker marker, String s, Object o) {
            L.trace(s,o);
        }

        @Override
        public void trace(Marker marker, String s, Object o, Object o1) {
            L.trace(s,o,o1);
        }

        @Override
        public void trace(Marker marker, String s, Object... objects) {
            L.trace(s,objects);
        }

        @Override
        public void trace(Marker marker, String s, Throwable throwable) {
            L.trace(s,throwable);
        }

        @Override
        public boolean isDebugEnabled() {
            return isTraceEnabled() || L.minLevel == L.Level.DEBUG;
        }

        @Override
        public void debug(String s) {
            L.debug(s);
        }

        @Override
        public void debug(String s, Object o) {
            L.debug(s,o);
        }

        @Override
        public void debug(String s, Object o, Object o1) {
            L.debug(s,o,o1);
        }

        @Override
        public void debug(String s, Object... objects) {
            L.debug(s,objects);
        }

        @Override
        public void debug(String s, Throwable throwable) {
            L.debug(s, throwable);
        }

        @Override
        public boolean isDebugEnabled(Marker marker) {
            return isDebugEnabled();
        }

        @Override
        public void debug(Marker marker, String s) {
            L.debug(s);
        }

        @Override
        public void debug(Marker marker, String s, Object o) {
            L.debug(s,o);
        }

        @Override
        public void debug(Marker marker, String s, Object o, Object o1) {
            L.debug(s,o,o1);
        }

        @Override
        public void debug(Marker marker, String s, Object... objects) {
            L.debug(s, objects);
        }

        @Override
        public void debug(Marker marker, String s, Throwable throwable) {
            L.debug(s, throwable);
        }

        @Override
        public boolean isInfoEnabled() {
            return isDebugEnabled() || L.minLevel == L.Level.INFO;
        }

        @Override
        public void info(String s) {
            L.info(s);
        }

        @Override
        public void info(String s, Object o) {
            L.info(s,o);
        }

        @Override
        public void info(String s, Object o, Object o1) {
            L.info(s,o, o1);
        }

        @Override
        public void info(String s, Object... objects) {
            L.info(s, objects);
        }

        @Override
        public void info(String s, Throwable throwable) {
            L.info(s, throwable);
        }

        @Override
        public boolean isInfoEnabled(Marker marker) {
            return isInfoEnabled();
        }

        @Override
        public void info(Marker marker, String s) {
            L.info(s);
        }

        @Override
        public void info(Marker marker, String s, Object o) {
            L.info(s,o);
        }

        @Override
        public void info(Marker marker, String s, Object o, Object o1) {
            L.info(s,o,o1);
        }

        @Override
        public void info(Marker marker, String s, Object... objects) {
            L.info(s,objects);
        }

        @Override
        public void info(Marker marker, String s, Throwable throwable) {
            L.info(s, throwable);
        }

        @Override
        public boolean isWarnEnabled() {
            return isInfoEnabled() || L.minLevel == L.Level.WARNING;
        }

        @Override
        public void warn(String s) {
            L.warn(s);
        }

        @Override
        public void warn(String s, Object o) {
            L.warn(s,o);
        }

        @Override
        public void warn(String s, Object... objects) {
            L.warn(s, objects);
        }

        @Override
        public void warn(String s, Object o, Object o1) {
            L.warn(s,o,o1);
        }

        @Override
        public void warn(String s, Throwable throwable) {
            L.warn(s, throwable);
        }

        @Override
        public boolean isWarnEnabled(Marker marker) {
            return isWarnEnabled();
        }

        @Override
        public void warn(Marker marker, String s) {
            L.warn(s);
        }

        @Override
        public void warn(Marker marker, String s, Object o) {
            L.warn(s, o);
        }

        @Override
        public void warn(Marker marker, String s, Object o, Object o1) {
            L.warn(s,o,o1);
        }

        @Override
        public void warn(Marker marker, String s, Object... objects) {
            L.warn(s,objects);
        }

        @Override
        public void warn(Marker marker, String s, Throwable throwable) {
            L.warn(s,throwable);
        }

        @Override
        public boolean isErrorEnabled() {
            return true;
        }

        @Override
        public void error(String s) {
            L.error(s, null);
        }

        @Override
        public void error(String s, Object o) {
            L.error(s, null, o);
        }

        @Override
        public void error(String s, Object o, Object o1) {
            L.error(s, null, o, o1);
        }

        @Override
        public void error(String s, Object... objects) {
            L.error(s, null, objects);
        }

        @Override
        public void error(String s, Throwable throwable) {
            L.error(s, throwable);
        }

        @Override
        public boolean isErrorEnabled(Marker marker) {
            return true;
        }

        @Override
        public void error(Marker marker, String s) {
            L.error(s, null);
        }

        @Override
        public void error(Marker marker, String s, Object o) {
            L.error(s, null, o);
        }

        @Override
        public void error(Marker marker, String s, Object o, Object o1) {
            L.error(s, null, o, o1);
        }

        @Override
        public void error(Marker marker, String s, Object... objects) {
            L.error(s, null, objects);
        }

        @Override
        public void error(Marker marker, String s, Throwable throwable) {
            L.error(s, throwable);
        }
}
