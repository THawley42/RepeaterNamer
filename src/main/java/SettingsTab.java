// Burp Extension RepeaterNamer
import burp.api.montoya.MontoyaApi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

//SettingsTab is the UI for the RepeaterNamer settings
//It constructs this UI in rows and takes the burp api and uses it to set setting base on button/checkboxes
public class SettingsTab extends JPanel {
    private final MontoyaApi api;

    SettingsTab(MontoyaApi api) {
        this.api = api;
        this.setLayout(new GridBagLayout());
        addFirstRow();
        addSecondRow();
        addThirdRow();
        addFourthRow();
        debugRow();
    }
    private void addFirstRow() {
        GridBagConstraints c = new GridBagConstraints();

        // Grid row 0, col 0: label
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        this.add(new JLabel("Methods to send to repeater"), c);

        // Grid row 0, col 1: text
        JTextField textFieldMethod = new JTextField(api.persistence().preferences().getString(SettingsTab.SETTINGS_KEY+"Method"), 50);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        this.add(textFieldMethod, c);

        // Grid row 0, col 2: button
        JButton addMethodButton = new JButton("Save Methods");
        addMethodButton.addActionListener(e -> api.persistence().preferences().setString(SettingsTab.SETTINGS_KEY+"Method", textFieldMethod.getText()));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        this.add(addMethodButton, c);
    }
    private void addSecondRow() {
        GridBagConstraints c = new GridBagConstraints();

        // Grid row 0, col 0: label
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        this.add(new JLabel("File extensions to send to repeater"), c);

        // Grid row 0, col 1: text
        JTextField textFieldExKeep = new JTextField(api.persistence().preferences().getString(SettingsTab.SETTINGS_KEY+"ExtentionKeep"), 50);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        this.add(textFieldExKeep, c);

        // Grid row 0, col 2: button
        JButton addButtonExKeep = new JButton("Save File Extensions");
        addButtonExKeep.addActionListener(e -> api.persistence().preferences().setString(SettingsTab.SETTINGS_KEY+"ExtentionKeep", textFieldExKeep.getText()));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 1;
        this.add(addButtonExKeep, c);
    }
    private void addThirdRow() {
        GridBagConstraints c = new GridBagConstraints();

        // Grid row 0, col 0: label
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        this.add(new JLabel("File extensions to send to repeater with the below restrictions"), c);

        // Grid row 0, col 1: text
        JTextField textFieldEx = new JTextField(api.persistence().preferences().getString(SettingsTab.SETTINGS_KEY+"Extention"), 50);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        this.add(textFieldEx, c);

        // Grid row 0, col 2: button
        JButton addButtonEx = new JButton("Save File Extensions");
        addButtonEx.addActionListener(e -> api.persistence().preferences().setString(SettingsTab.SETTINGS_KEY+"Extention", textFieldEx.getText()));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 2;
        this.add(addButtonEx, c);
    }
    private void addFourthRow() {
        GridBagConstraints c = new GridBagConstraints();

        JCheckBox checkOneOf = new JCheckBox("Send only 1 of each file extension", api.persistence().preferences().getBoolean(SettingsTab.SETTINGS_KEY+"OneOf"));
        checkOneOf.addItemListener(e -> api.persistence().preferences().setBoolean(SettingsTab.SETTINGS_KEY+"OneOf", e.getStateChange()==ItemEvent.SELECTED));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        this.add(checkOneOf, c);

        // Grid row 0, col 1: text
        JCheckBox checkEachPath = new JCheckBox("Send 1 of each extension from each path", api.persistence().preferences().getBoolean(SettingsTab.SETTINGS_KEY+"EachPath"));
        checkEachPath.addItemListener(e -> api.persistence().preferences().setBoolean(SettingsTab.SETTINGS_KEY+"EachPath", e.getStateChange()==ItemEvent.SELECTED));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 3;
        this.add(checkEachPath, c);

        JCheckBox checkMostParam = new JCheckBox("Send the request with the most parameters (wip)", api.persistence().preferences().getBoolean(SettingsTab.SETTINGS_KEY+"MostParam"));
        checkMostParam.addItemListener(e -> api.persistence().preferences().setBoolean(SettingsTab.SETTINGS_KEY+"MostParam", e.getStateChange()==ItemEvent.SELECTED));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 3;
        this.add(checkMostParam, c);
    }
    private void debugRow() {
        GridBagConstraints c = new GridBagConstraints();

        JButton addButtonlog = new JButton("Log settings");
        addButtonlog.addActionListener(e -> {
            api.logging().logToOutput("Repeaternamer.settings.init: "+api.persistence().preferences().getBoolean("Repeaternamer.settings.init"));
            api.logging().logToOutput("Repeaternamer.settings.Method: "+api.persistence().preferences().getString("Repeaternamer.settings.Method"));//
            api.logging().logToOutput("Repeaternamer.settings.ExtentionKeep: "+api.persistence().preferences().getString("Repeaternamer.settings.ExtentionKeep"));//
            api.logging().logToOutput("Repeaternamer.settings.Extention: "+api.persistence().preferences().getString("Repeaternamer.settings.Extention"));//
            api.logging().logToOutput("Repeaternamer.settings.OneOf: "+api.persistence().preferences().getBoolean("Repeaternamer.settings.OneOf"));//
            api.logging().logToOutput("Repeaternamer.settings.EachPath: "+api.persistence().preferences().getBoolean("Repeaternamer.settings.EachPath"));//
            api.logging().logToOutput("Repeaternamer.settings.MostParam: "+api.persistence().preferences().getBoolean("Repeaternamer.settings.MostParam"));

        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        this.add(addButtonlog, c);
        JButton addButtonclear= new JButton("Delete settings");
        addButtonclear.addActionListener(e -> {
            api.persistence().preferences().deleteBoolean("Repeaternamer.settings.init");
            api.persistence().preferences().deleteString("Repeaternamer.settings.Method");//
            api.persistence().preferences().deleteString("Repeaternamer.settings.ExtentionKeep");//
            api.persistence().preferences().deleteString("Repeaternamer.settings.Extention");//
            api.persistence().preferences().deleteBoolean("Repeaternamer.settings.OneOf");//
            api.persistence().preferences().deleteBoolean("Repeaternamer.settings.EachPath");//
            api.persistence().preferences().deleteBoolean("Repeaternamer.settings.MostParam");
            api.logging().logToOutput("Settings cleared. Please reload the extension or hit the Reset settings button to reset to default settings.");

        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 4;
        this.add(addButtonclear, c);
        JButton addButtonreset= new JButton("Reset settings");
        addButtonreset.addActionListener(e -> {
            api.persistence().preferences().setBoolean("Repeaternamer.settings.init", true);
            api.persistence().preferences().setString("Repeaternamer.settings.Method", "GET, POST, PUT, DELETE, PATCH");//
            api.persistence().preferences().setString("Repeaternamer.settings.ExtentionKeep", ".html");//
            api.persistence().preferences().setString("Repeaternamer.settings.Extention", ".png, .svg, .js, .gif, .jpg, .png, .css");//
            api.persistence().preferences().setBoolean("Repeaternamer.settings.OneOf", true);//
            api.persistence().preferences().setBoolean("Repeaternamer.settings.EachPath", true);//
            api.persistence().preferences().setBoolean("Repeaternamer.settings.MostParam", true);
            api.logging().logToOutput("Settings reset.");

        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 4;
        this.add(addButtonreset, c);
    }
    private static final String SETTINGS_KEY = "Repeaternamer.settings.";
}
