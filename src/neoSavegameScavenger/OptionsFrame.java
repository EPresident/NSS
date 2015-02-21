/* The MIT License
 * Copyright (C) 2014 Elia Calligaris
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
import javax.swing.Box;
import javax.swing.BoxLayout;
import static javax.swing.BoxLayout.LINE_AXIS;
import static javax.swing.BoxLayout.PAGE_AXIS;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OptionsFrame extends JFrame {

    private final InputManager inManager;
    private final NeoSavegameScavenger parent;
    private final JButton applyBtn, cancelBtn;
    private final Color backgroundColor;
    private final JTextField op1Field, op2Field;

    public OptionsFrame(NeoSavegameScavenger nss) {
        parent = nss;
        inManager = new InputManager();
        setBounds(50, 30, 650, 220);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(false);
        setTitle("NeoSavegameScavenger - Options");
        backgroundColor = parent.getBgColor();
        setResizable(true);
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
        panel.add(Box.createVerticalStrut(10));

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
        // Validate paths before recording
        if (validatePaths(op1Field.getText(), op2Field.getText())) {
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
    }

    /**
     * Makes sure the user inserted valid paths in the options menu.
     *
     * @param savPath Absolute path to the savegame folder.
     * @param bkpPath Absolute path to the backup folder.
     * @return true if the paths are valid.
     */
    private boolean validatePaths(String savPath, String bkpPath) {
        File saveDir = new File(savPath);
        File backDir = new File(bkpPath);
        boolean val1 = saveDir.exists() && saveDir.isDirectory(),
                val2 = backDir.exists() && backDir.isDirectory();
        if (val1 && val2) {
            return true;
        } else {
            if (!val1) {
                JOptionPane.showMessageDialog(this, "The path you specified for the savegame folder is invalid."
                        + " Please insert a valid one.", "Invalid savegame path", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (!val2) {
                //<editor-fold desc="Ask to create the backup directory">
                int confirmation = JOptionPane.showConfirmDialog(this,
                        "The specified backup folder doesn't exist. Do you want to create it?",
                        "Invalid backup path", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmation == 0) {
                    // Create the new backup directory
                    backDir.mkdirs();
                    // Check if creation succeded
                    if (!backDir.exists()) {
                        JOptionPane.showMessageDialog(this, "Failed to create the backup folder!", "Creation failure", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    return true;
                } else {
                    // Failure
                    JOptionPane.showMessageDialog(this, "The path you specified for the backup folder is invalid."
                        + " Please insert a valid one.", "Invalid backup path", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                //</editor-fold>
            }
        }
        return false;
    }

    /**
     * Read config file for text field initialization and request focus.
     */
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
