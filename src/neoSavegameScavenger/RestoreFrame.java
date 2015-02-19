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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
import static javax.swing.SwingConstants.CENTER;

public class RestoreFrame extends JFrame {

    private final InputManager inManager;
    private final NeoSavegameScavenger parent;
    private final JButton restoreBtn, cancelBtn;
    private final DefaultListModel listModel;
    private final JList backupList;
    private final Color backgroundColor;

    public RestoreFrame(NeoSavegameScavenger nss) {
        parent = nss;
        backgroundColor=parent.getBgColor();
        inManager = new InputManager();
        setBounds(50, 30, 175, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(false);
        setTitle("NeoSavegameScavenger - Restore");
        getContentPane().setBackground(backgroundColor);
        setResizable(true);        
        setFocusable(true);
        addKeyListener(inManager);          
        
        JPanel panel=new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBackground(backgroundColor);

        // Title
        JLabel title = new JLabel("Choose a backup to restore");
        title.setForeground(Color.white);
        title.setHorizontalAlignment(CENTER);
        add(title, BorderLayout.PAGE_START);
        
        add(Box.createVerticalStrut(5),BorderLayout.CENTER);

        // Panel with the list of available backups
        JPanel listPanel = new JPanel();
        listPanel.setBackground(backgroundColor);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.LINE_AXIS));

        // Add the backup list
        listPanel.add(Box.createHorizontalStrut(10));
        listModel = new DefaultListModel();
        backupList = new JList(listModel);
        backupList.setDragEnabled(false);
        backupList.setLayoutOrientation(JList.VERTICAL);
        backupList.setSelectionMode(SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(backupList);
        listScrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        listScrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        listPanel.add(listScrollPane);
        backupList.setSize(100, 100);
        listPanel.add(Box.createHorizontalStrut(10));
        add(listPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(backgroundColor);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.PAGE_AXIS));
        btnPanel.add(Box.createVerticalStrut(3));
        // Add confirm button
        restoreBtn = new JButton("Restore selected");
        restoreBtn.addActionListener(inManager);
        restoreBtn.setAlignmentX(CENTER_ALIGNMENT);
        btnPanel.add(restoreBtn);
        btnPanel.add(Box.createVerticalStrut(3));
        // Add cancel button
        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(inManager);
        cancelBtn.setAlignmentX(CENTER_ALIGNMENT);
        btnPanel.add(cancelBtn);
        btnPanel.add(Box.createVerticalStrut(3));
        add(btnPanel, BorderLayout.PAGE_END);

        setVisible(true);       
    }

    /**
     * Initialize the backup list.
     */
    protected void init() {
        File backupFolder = new File(parent.backupFolderPath);
        if (backupFolder.exists()) {
            // get all backup names
            String[] subDirs = backupFolder.list(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                    return new File(current, name).isDirectory();
                }
            });
            for (String bak : subDirs) {
                listModel.addElement(bak);
            }
            requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, "Error: the backup folder doens't exist!", "Backup folder not found", JOptionPane.ERROR_MESSAGE);           
            closeWindow();
        }
    }

    /**
     * Close the window.
     */
    private void closeWindow() {
        this.dispose();
        parent.requestFocus();
    }

    private void restoreSelectedBackup() {
        if(backupList.getSelectedValue()!=null){
            System.out.println(parent.backupFolderPath+"/"+(String)backupList.getSelectedValue());
            parent.performRestore(parent.backupFolderPath+"/"+(String)backupList.getSelectedValue());
            closeWindow();
        }
    }

    private class InputManager implements ActionListener, KeyListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == restoreBtn) {
                restoreSelectedBackup();
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
