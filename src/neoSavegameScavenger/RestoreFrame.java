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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.BorderFactory;
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

/**
 *
 * @author Utente
 */
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
        setUndecorated(true);
        setTitle("NeoSavegameScavenger - Restore");
        rootPane.setBorder(BorderFactory.createLineBorder(Color.black, 2, true));
        getContentPane().setBackground(backgroundColor);
        setResizable(false);        
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
