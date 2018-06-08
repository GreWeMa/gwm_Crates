package org.gwmdevelopments.sponge_plugin.crates.util;

import java.util.function.Function;

public class CheckIntegerFunction implements Function<String, String> {

    @Override
    public String apply(String s) {
        try {
            Integer.parseInt(s);
            return s;
        } catch (NumberFormatException e) {
            return "";
        }
    }
}
