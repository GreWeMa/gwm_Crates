package ua.gwm.sponge_plugin.crates.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Optional;
import java.util.function.Function;

public class AdvancedTextArea extends JTextArea {

    private String tip;
    private Optional<Function<String, String>> function = Optional.empty();

    public AdvancedTextArea(String tip) {
        this.tip = tip;
        setText(tip);
        setForeground(Color.GRAY);
        addFocusListener(new AdvancedTextArea.GWMFocusListener());
    }

    public boolean hasText() {
        String text = super.getText();
        return !text.equals("") && !text.equals(tip);
    }

    @Override
    public String getText() {
        String text = super.getText();
        return text.equals(tip) ? "" : text;
    }

    @Override
    public void setText(String s) {
        setForeground(Color.BLACK);
        super.setText(s);
    }

    public void reset() {
        setText(tip);
        setForeground(Color.GRAY);
    }

    public class GWMFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent event) {
            if (AdvancedTextArea.super.getText().equals(tip)) {
                setText("");
                setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent event) {
            String text = AdvancedTextArea.super.getText();
            if (text.equals("")) {
                setText(tip);
                setForeground(Color.GRAY);
            } else if (function.isPresent()) {
                text = function.get().apply(text);
                if (text.equals("")) {
                    setText(tip);
                    setForeground(Color.GRAY);
                } else {
                    AdvancedTextArea.super.setText(text);
                }
            }
        }
    }

    public Optional<Function<String, String>> getFunction() {
        return function;
    }

    public void setFunction(Optional<Function<String, String>> function) {
        this.function = function;
    }
}
