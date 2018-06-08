package org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.drop;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.gwmdevelopments.sponge_plugin.crates.gui.AddButton;
import org.gwmdevelopments.sponge_plugin.crates.gui.CommandPanel;
import org.gwmdevelopments.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.DropConfigurationDialog;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CommandsDropConfigurationDialog extends DropConfigurationDialog {

    private JLabel commands_label;
    private JPanel commands_panel;
    private JScrollPane commands_scroll_pane;
    private AddButton add_command_panel_button;

    private List<CommandPanel> commands = new ArrayList<CommandPanel>();

    public CommandsDropConfigurationDialog(ConfigurationNode node) {
        super("COMMANDS", node);
        commands_label = new JLabel("Commands");
        commands_label.setLocation(404, 10);
        commands_label.setSize(170, 20);
        add(commands_label);
        commands_panel = new JPanel();
        commands_panel.setLayout(new BoxLayout(commands_panel, BoxLayout.Y_AXIS));
        commands_scroll_pane = new JScrollPane(commands_panel);
        commands_scroll_pane.setLocation(404, 30);
        commands_scroll_pane.setSize(190, 100);
        add(commands_scroll_pane);
        add_command_panel_button = new AddButton();
        add_command_panel_button.setLocation(574, 10);
        add_command_panel_button.addActionListener(e -> {
            CommandPanel panel = new CommandPanel();
            panel.getDeleteButton().addActionListener(e2 -> {
                commands_panel.remove(panel);
                commands.remove(panel);
                commands_panel.revalidate();
                commands_panel.repaint();
            });
            commands_panel.add(panel);
            commands.add(panel);
            commands_panel.revalidate();
            commands_panel.repaint();
        });
        add(add_command_panel_button);
        load();
    }

    private void load() {
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode commands_node = node.getNode("COMMANDS");
            if (!commands_node.isVirtual()) {
                for (ConfigurationNode command_node : commands_node.getChildrenList()) {
                    ConfigurationNode cmd_node = command_node.getNode("COMMAND");
                    ConfigurationNode console_node = command_node.getNode("CONSOLE");
                    CommandPanel panel = new CommandPanel();
                    panel.getDeleteButton().addActionListener(e2 -> {
                        commands_panel.remove(panel);
                        commands.remove(panel);
                        commands_panel.revalidate();
                        commands_panel.repaint();
                    });
                    commands_panel.add(panel);
                    commands.add(panel);
                    panel.getCommandField().setText(cmd_node.getString());
                    panel.getConsoleCheckBox().setSelected(console_node.getBoolean());
                    commands_panel.revalidate();
                    commands_panel.repaint();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception loading Commands Drop Configuration Dialog!", e);
        }
    }

    @Override
    public void save() {
        super.save();
        try {
            ConfigurationNode node = getNode();
            ConfigurationNode commands_node = node.getNode("COMMANDS");
            if (commands.isEmpty()) {
                commands_node.setValue(null);
            } else {
                List<ConfigurationNode> command_nodes = new ArrayList<ConfigurationNode>();
                for (CommandPanel command_panel : commands) {
                    if (command_panel.getCommandField().hasText()) {
                        ConfigurationNode command_node = SimpleConfigurationNode.root();
                        command_node.getNode("COMMAND").setValue(command_panel.getCommandField().getText());
                        command_node.getNode("CONSOLE").setValue(command_panel.getConsoleCheckBox().isSelected());
                        command_nodes.add(command_node);
                    }
                }
                commands_node.setValue(command_nodes);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception saving Commands Drop Configuration Dialog!", e);
        }
    }
}
