/*
 * Copyright (C) 2014 Elia Calligaris
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package neoSavegameScavenger;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import static javax.swing.BoxLayout.LINE_AXIS;
import static javax.swing.BoxLayout.PAGE_AXIS;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Utente
 */
public class OptionsFrame extends JFrame {

    private final InputManager inManager;
    private final NeoSavegameScavenger parent;
    private final JButton applyBtn, cancelBtn;
    private final Color backgroundColor;
    private final JTextField op1Field, op2Field;

    public OptionsFrame(NeoSavegameScavenger nss) {
        parent = nss;
        inManager = new InputManager();
        setBounds(50, 30, 350, 175);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setTitle("NeoSavegameScavenger - Options");
        rootPane.setBorder(BorderFactory.createLineBorder(Color.black, 2, true));
        backgroundColor = parent.getBgColor();
        setResizable(false);
        setFocusable(true);
        addKeyListener(inManager);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, PAGE_AXIS));
        panel.add(Box.createVerticalStrut(5));
        panel.setBackground(backgroundColor);

        JLabel titleLabel = new JLabel("Options");
        titleLabel.setForeground(Color.white);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(5));

        //<editor-fold desc="Savegame Path option"> 
        JPanel op1Wrapper = new JPanel();
        op1Wrapper.setLayout(new BoxLayout(op1Wrapper, LINE_AXIS));
        op1Wrapper.setBackground(backgroundColor);

        JPanel op1Panel = new JPanel();
        op1Panel.setBackground(backgroundColor);
        op1Panel.setLayout(new BoxLayout(op1Panel, PAGE_AXIS));

        JLabel op1Label = new JLabel("Save folder path: ");
        op1Label.setForeground(Color.white);
        op1Panel.add(op1Label);

        op1Panel.add(Box.createVerticalStrut(5));

        op1Field = new JTextField();
        op1Field.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
        op1Panel.add(op1Field);

        op1Wrapper.add(Box.createRigidArea(new Dimension(15, 1)));
        op1Wrapper.add(op1Panel);
        op1Wrapper.add(Box.createRigidArea(new Dimension(15, 1)));
        panel.add(op1Wrapper);
        //</editor-fold>

        panel.add(Box.createVerticalStrut(10));

        //<editor-fold desc="Backup folder path option panel">
        JPanel op2Wrapper = new JPanel();
        op2Wrapper.setLayout(new BoxLayout(op2Wrapper, LINE_AXIS));
        op2Wrapper.setBackground(backgroundColor);

        JPanel op2Panel = new JPanel();
        op2Panel.setBackground(backgroundColor);
        op2Panel.setLayout(new BoxLayout(op2Panel, PAGE_AXIS));

        JLabel op2Label = new JLabel("Backup folder path: ");
        op2Label.setForeground(Color.white);
        op2Panel.add(op2Label);

        op2Panel.add(Box.createVerticalStrut(5));

        op2Field = new JTextField(30);
        op2Field.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
        op2Panel.add(op2Field);

        op2Wrapper.add(Box.createRigidArea(new Dimension(15, 1)));
        op2Wrapper.add(op2Panel);
        op2Wrapper.add(Box.createRigidArea(new Dimension(15, 1)));
        panel.add(op2Wrapper);
        //</editor-fold>

        panel.add(Box.createVerticalGlue());

        //<editor-fold desc="Buttons panel">
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(backgroundColor);
        btnPanel.setLayout(new BoxLayout(btnPanel, LINE_AXIS));
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.setMinimumSize(new Dimension(Short.MAX_VALUE, 30));
        btnPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, 30));

        applyBtn = new JButton("Confirm & Exit");
        applyBtn.addActionListener(inManager);
        applyBtn.setAlignmentX(CENTER_ALIGNMENT);
        btnPanel.add(applyBtn);

        btnPanel.add(Box.createHorizontalGlue());

        cancelBtn = new JButton("Cancel & Exit");
        cancelBtn.addActionListener(inManager);
        cancelBtn.setAlignmentX(CENTER_ALIGNMENT);
        btnPanel.add(cancelBtn);

        btnPanel.add(Box.createHorizontalGlue());
        panel.add(btnPanel);
        //</editor-fold>

        panel.add(Box.createVerticalStrut(10));
        add(panel);
        init();
        setVisible(true);
    }

    private void closeWindow() {
        this.dispose();
        parent.requestFocus();
    }

    /**
     * Update the config file with the changes made by the user in the options
     * menu, then close the window.
     */
    private void applyChanges() {
        File config = new File("./config.txt");
        // Check if the config file exists
        if (!config.exists()) {
            //Create the config file
            try {
                config.createNewFile();
            } catch (java.io.IOException ex) {
                System.err.println("Can't create the config file: " + ex.getMessage());
            }
        }
        // Write on the config
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(config));
            String s = "# Config file for Neo Savegame Scavenger, DO NOT MODIFY MANUALLY!";
            out.write(s);
            out.newLine();
            s = "SaveFolderPath=" + op1Field.getText();
            out.write(s);
            out.newLine();
            s = "BackupFolderPath=" + op2Field.getText();
            out.write(s);
            out.newLine();
            out.close();
        } catch (IOException ex) {
            System.err.println("I/O Write Failure: " + ex.getMessage());
        } finally {
            closeWindow();
            parent.readConfig();
        }
    }

    private void init() {
        File config = new File("./config.txt");
        if (config.exists()) {
            // Read the config
            try {
                BufferedReader in = new BufferedReader(new FileReader(config));
                // ignore the config comment
                in.readLine();
                // Read save folder path
                String s = in.readLine();
                StringTokenizer st = new StringTokenizer(s, "=");
                if (st.countTokens() > 1) {
                    st.nextToken();
                    op1Field.setText(st.nextToken());
                }

                // Read backup folder path
                s = in.readLine();
                st = new StringTokenizer(s, "=");
                if (st.countTokens() > 1) {
                    st.nextToken();
                    op2Field.setText(st.nextToken());
                }
                in.close();
            } catch (IOException ex) {
                System.out.println("I/O Read Error: " + ex.getMessage());
            }
        }
        requestFocus();
    }

    private class InputManager implements ActionListener, KeyListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == applyBtn) {
                applyChanges();
            }
            if (e.getSource() == cancelBtn) {
                closeWindow();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                closeWindow();
            }
        }
    }
}
