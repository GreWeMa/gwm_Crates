package org.gwmdevelopments.sponge_plugin.crates.gui;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.gwmdevelopments.sponge_plugin.crates.GWMCrates;
import org.gwmdevelopments.sponge_plugin.crates.util.SuperObjectType;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GWMCratesGUI extends JFrame {

    private static GWMCratesGUI instance;

    public static List<String> ITEM_TYPES;
    public static List<String> SOUND_TYPES;
    public static List<String> ENCHANTMENTS;
    public static List<String> BLOCK_TYPES;

    static {
        ITEM_TYPES = Arrays.stream(ItemTypes.class.getFields()).map(f -> {
            try {
                f.setAccessible(true);
                return ((ItemType) f.get(null)).getId().replaceFirst("minecraft:", "");
            } catch (Exception e) {
                GWMCrates.getInstance().getLogger().warn("Exception parsing Item Types!", e);
                return null;
            }
        }).collect(Collectors.toList());
        SOUND_TYPES = Arrays.stream(SoundTypes.class.getFields()).map(f -> {
            try {
                f.setAccessible(true);
                return ((SoundType) f.get(null)).getId().replaceFirst("minecraft:", "");
            } catch (Exception e) {
                GWMCrates.getInstance().getLogger().warn("Exception parsing Sound Types!", e);
                return null;
            }
        }).collect(Collectors.toList());
        ENCHANTMENTS = Arrays.stream(EnchantmentTypes.class.getFields()).map(f -> {
            try {
                f.setAccessible(true);
                return ((EnchantmentType) f.get(null)).getId().replaceFirst("minecraft:", "");
            } catch (Exception e) {
                GWMCrates.getInstance().getLogger().warn("Exception parsing Enchantments!", e);
                return null;
            }
        }).collect(Collectors.toList());
        BLOCK_TYPES = Arrays.stream(BlockTypes.class.getFields()).map(f -> {
            try {
                f.setAccessible(true);
                return ((BlockType) f.get(null)).getId().replaceFirst("minecraft:", "");
            } catch (Exception e) {
                GWMCrates.getInstance().getLogger().warn("Exception parsing Block Types!", e);
                return null;
            }
        }).collect(Collectors.toList());
    }

    public static GWMCratesGUI getInstance() {
        if (instance == null) {
            throw new RuntimeException("GWMCratesGUI not initialized!");
        }
        return instance;
    }

    public static void initialize() {
        if (instance != null) {
            throw new RuntimeException("GWMCratesGUI already initialized!");
        }
        instance = new GWMCratesGUI();
    }

    private JLabel created_manager_label;
    private GUIOutput output;
    private JButton generate_button;
    private JButton import_button;
    private JButton copy_button;
    private JButton export_button;
    private JButton apply_button;
    private JLabel manager_id_label;
    private AdvancedTextField manager_id_field;
    private JLabel manager_name_label;
    private AdvancedTextField manager_name_field;
    private SuperObjectPanel case_panel;
    private SuperObjectPanel key_panel;
    private SuperObjectPanel open_manager_panel;
    private SuperObjectPanel preview_panel;
    private JLabel drops_label;
    private AddButton add_drop_button;
    private JPanel drops_panel;

    private ArrayList<FlatSuperObjectPanel> drops = new ArrayList<FlatSuperObjectPanel>();

    public GWMCratesGUI() {
        super("GWMCrates " + GWMCrates.VERSION + " GUI");
        setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        createObjects();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                int result = JOptionPane.showConfirmDialog(instance, "Do you want to close " + getTitle() + "? If you have unsaved data, it will be lost permanently!", "Close?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    instance.setVisible(false);
                    instance = null;
                }
            }
        });
        setVisible(true);
    }

    private void createObjects() {
        add((output = new GUIOutput()).getPane());

        generate_button = new JButton("Generate");
        generate_button.setSize(180, 40);
        generate_button.setLocation(20, 20);
        generate_button.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "It will clear all changes made by hand (if they not applied). Are you want to continue?", "Generate?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                generate();
            }
        });
        add(generate_button);

        import_button = new JButton("Import");
        import_button.setSize(110, 40);
        import_button.setLocation(210, 20);
        import_button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new DirectoryAndConfFileFilter());
            chooser.showOpenDialog(this);
            File file = chooser.getSelectedFile();
            if (file == null) {
                JOptionPane.showMessageDialog(this, "No file selected!", "Error!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() ->
                        new BufferedReader(new FileReader(file))).build();
                ConfigurationNode node = loader.load();
                update(node);
                JOptionPane.showMessageDialog(this, "Successfully imported manager from file \"" + file.getName() + "\"!", "Success!", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Exception importing manager! See details in console!", "Error!", JOptionPane.WARNING_MESSAGE);
                GWMCrates.getInstance().getLogger().warn("Exception importing manager to GWMCratesGUI!", ex);
            }
        });
        add(import_button);

        created_manager_label = new JLabel("Created manager");
        created_manager_label.setSize(180, 20);
        created_manager_label.setLocation(350, 5);
        created_manager_label.setFont(new Font("Arial", Font.BOLD, 16));
        add(created_manager_label);

        copy_button = new JButton("copy");
        copy_button.setSize(80, 20);
        copy_button.setLocation(530, 5);
        copy_button.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().
                    setContents(new StringSelection(output.getText()), null);
            JOptionPane.showMessageDialog(this, "Created manager successfully copied to your clipboard!", "Copying created manager", JOptionPane.INFORMATION_MESSAGE);
        });
        add(copy_button);

        export_button = new JButton("export");
        export_button.setSize(80, 20);
        export_button.setLocation(620, 5);
        export_button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new DirectoryAndConfFileFilter());
            chooser.showOpenDialog(this);
            File file = chooser.getSelectedFile();
            if (file == null) {
                JOptionPane.showMessageDialog(this, "No file selected!", "Error!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                if (!file.getName().endsWith(".conf") && !file.exists()) {
                    file = new File(file.getAbsolutePath() + ".conf");
                }
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(output.getText());
                writer.close();
                JOptionPane.showMessageDialog(this, "Manager successfully saved to file \"" + file.getName() + "\"!", "Success!", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Exception saving manager to file!", "Error!", JOptionPane.WARNING_MESSAGE);
                GWMCrates.getInstance().getLogger().warn("Exception saving manager to file!", ex);
            }
        });
        add(export_button);

        apply_button = new JButton("apply");
        apply_button.setLocation(710, 5);
        apply_button.setSize(80, 20);
        apply_button.addActionListener(e -> {
            try {
                HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() ->
                        new BufferedReader(new StringReader(output.getText()))).build();
                ConfigurationNode node = loader.load();
                update(node);
                JOptionPane.showMessageDialog(this, "Successfully applied changes!", "Success!", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Exception applying changes! See details in console!", "Error!", JOptionPane.WARNING_MESSAGE);
                GWMCrates.getInstance().getLogger().warn("Exception applying changes to GWMCratesGUI!", ex);
            }
        });
        add(apply_button);

        manager_id_label = new JLabel("Manager ID");
        manager_id_label.setSize(200, 20);
        manager_id_label.setLocation(20, 70);
        add(manager_id_label);

        manager_id_field = new AdvancedTextField(GUIConstants.ENTER_MANAGER_ID);
        manager_id_field.setToolTipText("Lower case only, no whitespaces.");
        manager_id_field.setSize(300, 20);
        manager_id_field.setLocation(20, 90);
        add(manager_id_field);

        manager_name_label = new JLabel("Manager name");
        manager_name_label.setSize(200, 20);
        manager_name_label.setLocation(20, 120);
        add(manager_name_label);

        manager_name_field = new AdvancedTextField(GUIConstants.ENTER_MANAGER_NAME);
        manager_name_field.setToolTipText("Color codes supported.");
        manager_name_field.setSize(300, 20);
        manager_name_field.setLocation(20, 140);
        add(manager_name_field);

        case_panel = new SuperObjectPanel(true, "Case", SuperObjectType.CASE, GUIConstants.CASE_TYPES);
        case_panel.setLocation(20, 170);
        add(case_panel);

        key_panel = new SuperObjectPanel(true, "Key", SuperObjectType.KEY, GUIConstants.KEY_TYPES);
        key_panel.setLocation(20, 220);
        add(key_panel);

        open_manager_panel = new SuperObjectPanel(true, "Open Manager", SuperObjectType.OPEN_MANAGER, GUIConstants.OPEN_MANAGER_TYPES);
        open_manager_panel.setLocation(20, 270);
        add(open_manager_panel);

        preview_panel = new SuperObjectPanel(true, "Preview", SuperObjectType.PREVIEW, GUIConstants.PREVIEW_TYPES);
        preview_panel.setLocation(20, 320);
        add(preview_panel);

        drops_label = new JLabel("Drops");
        drops_label.setSize(260, 20);
        drops_label.setLocation(20, 370);
        add(drops_label);

        add_drop_button = new AddButton();
        add_drop_button.setSize(20, 20);
        add_drop_button.setLocation(300, 370);
        add_drop_button.addActionListener(e -> addDrop());
        add(add_drop_button);

        drops_panel = new JPanel();
        drops_panel.setLayout(new BoxLayout(drops_panel, BoxLayout.Y_AXIS));

        JScrollPane drops_scroll_pane = new JScrollPane(drops_panel);
        drops_scroll_pane.setLocation(20, 390);
        drops_scroll_pane.setSize(300, 175);
        add(drops_scroll_pane);
    }

    public void update(ConfigurationNode node) {
        output.setText(node);
        manager_id_field.reset();
        manager_name_field.reset();
        case_panel.clear();
        key_panel.clear();
        open_manager_panel.clear();
        preview_panel.clear();
        new ArrayList<>(drops). //Prevents ConcurrentModificationException
                forEach(this::removeDrop);
        ConfigurationNode manager_id_node = node.getNode("ID");
        ConfigurationNode manager_name_node = node.getNode("NAME");
        ConfigurationNode case_node = node.getNode("CASE");
        ConfigurationNode key_node = node.getNode("KEY");
        ConfigurationNode open_manager_node = node.getNode("OPEN_MANAGER");
        ConfigurationNode preview_node = node.getNode("PREVIEW");
        ConfigurationNode drops_node = node.getNode("DROPS");
        if (!manager_id_node.isVirtual()) {
            manager_id_field.setText(manager_id_node.getString().toLowerCase().replace(' ', '_'));
        }
        if (!manager_name_node.isVirtual()) {
            manager_name_field.setText(manager_name_node.getString());
        }
        if (!case_node.isVirtual()) {
            case_panel.setNode(case_node);
        }
        if (!key_node.isVirtual()) {
            key_panel.setNode(key_node);
        }
        if (!open_manager_node.isVirtual()) {
            open_manager_panel.setNode(open_manager_node);
        }
        if (!preview_node.isVirtual()) {
            preview_panel.setNode(preview_node);
        }
        if (!drops_node.isVirtual()) {
            for (ConfigurationNode drop_node : drops_node.getChildrenList()) {
                addDrop().setNode(drop_node);
            }
        }
    }

    public void generate() {
        ConfigurationNode node = SimpleConfigurationNode.root();
        if (!manager_id_field.hasText()) {
            JOptionPane.showMessageDialog(this, "Enter manager ID!", "Error!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        node.getNode("ID").setValue(manager_id_field.getText().toLowerCase().replace(' ', '_'));
        if (!manager_name_field.hasText()) {
            JOptionPane.showMessageDialog(this, "Enter manager name!", "Error!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        node.getNode("NAME").setValue(manager_name_field.getText());
        String case_type = case_panel.getType();
        if (case_type == null) {
            JOptionPane.showMessageDialog(this, "Super Object \"CASE\" type not selected!", "Error!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        node.getNode("CASE").setValue(case_panel.getNode());
        String key_type = key_panel.getType();
        if (key_type == null) {
            JOptionPane.showMessageDialog(this, "Super Object \"KEY\" type not selected!", "Error!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        node.getNode("KEY").setValue(key_panel.getNode());
        String open_manager_type = open_manager_panel.getType();
        if (open_manager_type == null) {
            JOptionPane.showMessageDialog(this, "Super Object \"OPEN_MANAGER\" type not selected!", "Error!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        node.getNode("OPEN_MANAGER").setValue(open_manager_panel.getNode());
        String preview_type = preview_panel.getType();
        if (preview_type == null) {
            JOptionPane.showMessageDialog(this, "Super Object \"PREVIEW\" type not selected!", "Error!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!preview_type.equals("NO PREVIEW")) {
            node.getNode("PREVIEW").setValue(preview_panel.getNode());
        }
        List<ConfigurationNode> drops_node_list = new ArrayList<ConfigurationNode>();
        for (FlatSuperObjectPanel drop : drops) {
            String drop_type = drop.getType();
            if (drop_type == null) {
                JOptionPane.showMessageDialog(this, "Super Object \"DROP\" type not selected!", "Error!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ConfigurationNode drop_node = drop.getNode();
            drops_node_list.add(drop_node);
        }
        node.getNode("DROPS").setValue(drops_node_list);
        output.setText(node);
    }

    public FlatSuperObjectPanel addDrop() {
        FlatSuperObjectPanel drop = new FlatSuperObjectPanel(true, SuperObjectType.DROP, GUIConstants.DROP_TYPES);
        drop.getDeleteButton().addActionListener(e -> {
            if (drop.getType() != null) {
                drop.clear();
            } else {
                removeDrop(drop);
            }
        });
        drops_panel.add(drop);
        drops.add(drop);
        drops_panel.revalidate();
        return drop;
    }

    public void removeDrop(FlatSuperObjectPanel drop) {
        drops_panel.remove(drop);
        drops.remove(drop);
        drops_panel.revalidate();
        drops_panel.repaint();
    }

    public static class DirectoryAndConfFileFilter extends FileFilter {

        @Override
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(".conf");
        }

        @Override
        public String getDescription() {
            return ".conf files";
        }
    }

    public GUIOutput getOutput() {
        return output;
    }

    public SuperObjectPanel getCasePanel() {
        return case_panel;
    }

    public SuperObjectPanel getKeyPanel() {
        return key_panel;
    }

    public SuperObjectPanel getOpenManagerPanel() {
        return open_manager_panel;
    }

    public SuperObjectPanel getPreviewPanel() {
        return preview_panel;
    }

    public JPanel getDropsPanel() {
        return drops_panel;
    }

    public ArrayList<FlatSuperObjectPanel> getDrops() {
        return drops;
    }
}
