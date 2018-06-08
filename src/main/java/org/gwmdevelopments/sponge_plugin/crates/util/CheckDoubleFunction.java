package org.gwmdevelopments.sponge_plugin.crates.util;

import java.util.function.Function;

public class CheckDoubleFunction implements Function<String, String> {

    @Override
    public String apply(String s) {
        try {
            Double.parseDouble(s);
            return s;
        } catch (NumberFormatException e) {
            return "";
        }
    }
}
