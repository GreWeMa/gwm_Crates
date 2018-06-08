package org.gwmdevelopments.sponge_plugin.crates.util;

import java.util.function.Function;

public class CheckIntListFunction implements Function<String, String> {

    @Override
    public String apply(String s) {
        try {
            for (String str : s.split(" ")) {
                Integer.valueOf(str);
            }
            return s;
        } catch (Exception e) {
            return "";
        }
    }
}
