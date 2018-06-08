package org.gwmdevelopments.sponge_plugin.crates.util;

import java.util.function.Function;

public class CheckLongFunction implements Function<String, String> {

    @Override
    public String apply(String s) {
        try {
            Long.parseLong(s);
            return s;
        } catch (NumberFormatException e) {
            return "";
        }
    }
}
