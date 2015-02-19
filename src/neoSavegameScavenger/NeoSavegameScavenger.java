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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Main class and frame for the NSS.
 *
 * @author Elia Calligaris
 */
public class NeoSavegameScavenger extends JFrame {

    private final JButton backupBtn, optionsBtn, restoreBtn, restoreLastBtn, clearBtn, helpBtn;
    private final InputManager inManager;
    protected String backupFolderPath = "",
            savegameFolderPath = "";
    // protected final static String configPath = "/neoSavegameScavenger/config.txt";

    /**
     * Pattern for "filename.sol"
     */
    private final Pattern saveFilePattern = Pattern.compile("\\w*\\.sol");
    /**
     * Pattern for "YYYY_MM_DD HH-MM-SS anythingelse"
     */
    private final Pattern bkpDirPattern = Pattern.compile("\\d\\d\\d\\d_\\d\\d_\\d\\d \\d\\d-\\d\\d-\\d\\d.*");
    /**
     * FilenameFilter that accepts all directories.
     *
     * @see FilenameFilter
     */
    private final FilenameFilter directoryFilter = new FilenameFilter() {
        @Override
        public boolean accept(File current, String name) {
            return new File(current, name).isDirectory();
        }
    };
    /**
     * FilenameFilter that accepts all files with .sol extension.
     *
     * @see FilenameFilter
     */
    private final FilenameFilter savefileFilter = new FilenameFilter() {
        @Override
        public boolean accept(File current, String name) {
            return saveFilePattern.matcher(name).matches();
        }
    };
    /**
     * FilenameFilter that accepts directories that match bkpDirPattern.
     *
     * @see FilenameFilter
     */
    private final FilenameFilter bkpFolderFilter = new FilenameFilter() {
        @Override
        public boolean accept(File current, String name) {
            return saveFilePattern.matcher(name).matches() && new File(current, name).isDirectory();
        }
    };
    private final int BTN_OFFSET = 5;
    private final int BTN_PANEL_Y_PADDING = 10;
    private final int INSETS_X = 2, INSETS_Y = 0;
    private final float VERSION = 1.001f;
    private final Color backgroundColor;

    public static void main(String[] args) {
        new NeoSavegameScavenger();
    }

    public NeoSavegameScavenger() {
        setBounds(50, 30, 300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("NSS v" + VERSION);
        setLayout(new BorderLayout());
        setResizable(true);
        inManager = new InputManager();
        addKeyListener(inManager);
        setFocusable(true);
        backgroundColor = Color.darkGray;

        //<editor-fold desc="Logo pane">
        // Panel containing the title/logo
        // TODO: create a proper logo
        JPanel logoPane = new JPanel();
        JLabel logo = new JLabel("NeoSavegameScavenger");
        logo.setForeground(Color.white);
        logoPane.add(Box.createVerticalGlue());
        logoPane.add(logo);
        logoPane.add(Box.createVerticalGlue());
        logoPane.setBackground(backgroundColor);

        add(logoPane, BorderLayout.PAGE_START);
        //</editor-fold>

        //<editor-fold desc="Credit pane">        
        JPanel footPane = new JPanel();
        footPane.setBackground(backgroundColor);
        footPane.setLayout(new BoxLayout(footPane, BoxLayout.PAGE_AXIS));

        JLabel credits = new JLabel("Made by evil_genius (prez_enquiry@hotmail.com).");
        credits.setForeground(Color.white);
        credits.setFont(new Font("Arial", Font.PLAIN, 10));
        credits.setAlignmentX(CENTER_ALIGNMENT);
        footPane.add(credits);

        JLabel credits2 = new JLabel("This program is provided without any warranty.");
        credits2.setForeground(Color.white);
        credits2.setFont(new Font("Arial", Font.PLAIN, 10));
        credits2.setAlignmentX(CENTER_ALIGNMENT);
        footPane.add(credits2);

        footPane.add(Box.createVerticalStrut(5));

        add(footPane, BorderLayout.PAGE_END);
        //</editor-fold>

        //<editor-fold desc="Central pane">
        // Central part of the layout, contains the backup and restore sections.
        JPanel centerPane = new JPanel();
        centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.LINE_AXIS));
        centerPane.setBackground(backgroundColor);
        centerPane.add(Box.createHorizontalStrut(BTN_OFFSET));
        centerPane.add(Box.createHorizontalGlue());

        //<editor-fold desc="Backup panel">
        // This panel holds the backup button and the options and help buttons.
        JPanel backupPanel = new JPanel();
        backupPanel.setLayout(new BoxLayout(backupPanel, BoxLayout.PAGE_AXIS));
        backupPanel.setBackground(backgroundColor);
        backupPanel.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));

        backupPanel.add(Box.createVerticalStrut(BTN_PANEL_Y_PADDING));

        backupBtn = new JButton("Backup");
        backupBtn.setAlignmentX(CENTER_ALIGNMENT);
        backupBtn.addActionListener(inManager);
        backupBtn.setMargin(new java.awt.Insets(INSETS_Y, INSETS_X, INSETS_Y, INSETS_X));
        backupBtn.setToolTipText("Create a new backup of the current save.");
        backupPanel.add(backupBtn);

        backupPanel.add(Box.createVerticalStrut(BTN_OFFSET));
        //<editor-fold desc="Utility buttons panel">   
        // Small buttons for the options menu and help file go here.
        JPanel utilBtnPanel = new JPanel();
        utilBtnPanel.setLayout(new BoxLayout(utilBtnPanel, BoxLayout.LINE_AXIS));
        utilBtnPanel.setBackground(backgroundColor);

        utilBtnPanel.add(Box.createHorizontalGlue());

        optionsBtn = new JButton(createImageIcon("/resources/Cog.png"));
        optionsBtn.addActionListener(inManager);
        optionsBtn.setMargin(new java.awt.Insets(INSETS_Y, INSETS_Y, INSETS_Y, INSETS_Y));
        optionsBtn.setToolTipText("Open options menu");
        utilBtnPanel.add(optionsBtn);

        utilBtnPanel.add(Box.createRigidArea(new Dimension(BTN_OFFSET, BTN_OFFSET)));

        helpBtn = new JButton(createImageIcon("/resources/Help.png"));
        helpBtn.addActionListener(inManager);
        helpBtn.setMargin(new java.awt.Insets(INSETS_Y, INSETS_Y, INSETS_Y, INSETS_Y));
        helpBtn.setToolTipText("Help & Guide");
        utilBtnPanel.add(helpBtn);

        utilBtnPanel.add(Box.createHorizontalGlue());

        backupPanel.add(utilBtnPanel);
        //</editor-fold> 

        int w = (int) backupBtn.getSize().getWidth();
        Dimension minSize = new Dimension(w + BTN_OFFSET, BTN_PANEL_Y_PADDING / 2);
        Dimension prefSize = new Dimension(w + 20, BTN_PANEL_Y_PADDING);
        Dimension maxSize = new Dimension(w + 50, BTN_PANEL_Y_PADDING);
        backupPanel.add(new Box.Filler(minSize, prefSize, maxSize));

        centerPane.add(backupPanel);
        //</editor-fold>

        centerPane.add(Box.createHorizontalGlue());
        centerPane.add(Box.createHorizontalStrut(BTN_OFFSET));
        centerPane.add(Box.createHorizontalGlue());

        //<editor-fold desc="Restore panel">
        // Panel with all the restore buttons, plus the clear button.
        JPanel restorePanel = new JPanel();
        restorePanel.setLayout(new BoxLayout(restorePanel, BoxLayout.PAGE_AXIS));
        restorePanel.setBackground(backgroundColor);
        restorePanel.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));

        restorePanel.add(Box.createVerticalStrut(BTN_PANEL_Y_PADDING));

        restoreLastBtn = new JButton("Quick restore");
        restoreLastBtn.setAlignmentX(CENTER_ALIGNMENT);
        restoreLastBtn.addActionListener(inManager);
        restoreLastBtn.setMargin(new java.awt.Insets(INSETS_Y, INSETS_X, INSETS_Y, INSETS_X));
        restoreLastBtn.setToolTipText("Instantly restore the most recent backup.");
        restorePanel.add(restoreLastBtn);

        restorePanel.add(Box.createVerticalStrut(BTN_OFFSET));

        restoreBtn = new JButton("Restore...");
        restoreBtn.setAlignmentX(CENTER_ALIGNMENT);
        restoreBtn.addActionListener(inManager);
        restoreBtn.setMargin(new java.awt.Insets(INSETS_Y, INSETS_X, INSETS_Y, INSETS_X));
        restoreBtn.setToolTipText("Choose a backup to restore.");
        restorePanel.add(restoreBtn);

        restorePanel.add(Box.createVerticalStrut(BTN_OFFSET));

        clearBtn = new JButton("Clear save");
        clearBtn.setAlignmentX(CENTER_ALIGNMENT);
        clearBtn.addActionListener(inManager);
        clearBtn.setMargin(new java.awt.Insets(INSETS_Y, INSETS_X, INSETS_Y, INSETS_X));
        clearBtn.setToolTipText("Clear the savegame folder.");
        restorePanel.add(clearBtn);

        w = (int) Math.max(restoreBtn.getSize().getWidth(), Math.max(restoreLastBtn.getSize().getWidth(), clearBtn.getSize().getWidth()));
        minSize = new Dimension(w + BTN_OFFSET, BTN_PANEL_Y_PADDING / 2);
        prefSize = new Dimension(w + 20, BTN_PANEL_Y_PADDING);
        maxSize = new Dimension(w + 50, BTN_PANEL_Y_PADDING);
        restorePanel.add(new Box.Filler(minSize, prefSize, maxSize));

        centerPane.add(restorePanel);
        //</editor-fold>

        centerPane.add(Box.createHorizontalGlue());
        centerPane.add(Box.createHorizontalStrut(BTN_OFFSET));
        add(centerPane, BorderLayout.CENTER);
        //</editor-fold>

        setVisible(true);

        readConfig();
    }

    private void performBackup() {
        if (!savegameFolderPath.equals("") && !backupFolderPath.equals("")) {
            // Backup the savegame
            File bakDir = new File(backupFolderPath);
            if (!bakDir.exists()) {
                // The backup dir doesn't exist
                JOptionPane.showMessageDialog(this, "The backup directory doesn't exist.", "Backup error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create the new backup folder, using the date of creation as name
            DateFormat date = new SimpleDateFormat("yyyy_MM_dd HH-mm-ss");
            File saveBak = new File("" + backupFolderPath + "/" + date.format(Calendar.getInstance().getTime()));
            saveBak.mkdir();
            // Check if creation succeded
            if (!saveBak.exists()) {
                JOptionPane.showMessageDialog(this, "Failed to create the new save backup.", "Backup error", JOptionPane.ERROR_MESSAGE);
            }
            // Get all save files (*.sol)
            File[] sourceFiles = new File(savegameFolderPath).listFiles(savefileFilter);
            // Copy the files from the current save to the backup folder
            for (File f : sourceFiles) {
                File copy = new File(saveBak, f.getName());
                try {
                    Files.copy(Paths.get(f.getPath()), Paths.get(copy.getPath()), NOFOLLOW_LINKS);
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }

            JOptionPane.showMessageDialog(this, sourceFiles.length + " files backed up in folder "
                    + saveBak.getName(), "Backup done", JOptionPane.INFORMATION_MESSAGE);

        } else {
            // Some paths are missing!
            JOptionPane.showMessageDialog(this, "Savegame and/or backup folder paths are missing. "
                    + "Please check the options menu before reattempting this operation.",
                    "Backup error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void restoreLastSave() {
        // Ask for confirmation
        // Yes is 0 and No is 1
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to restore your last backup? \nThe current savegame will be overwritten, if present.",
                "Confirm restore last save", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmation == 0) {
            // Perform the actual restore
            File backupFolder = new File(backupFolderPath);
            if (backupFolder.exists()) {
                // get all backup names
                String[] subDirs = backupFolder.list(bkpFolderFilter);
                // Restore the last save
                performRestore(backupFolderPath + "/" + getNewestFile(subDirs));
            } else {
                JOptionPane.showMessageDialog(this, "Error: the backup folder doens't exist!", "Backup folder not found", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Copies the savegame from the selected backup to the savegame folder
     *
     * @param backupPath The full (absolute) path to the selected backup's
     * directory
     */
    protected void performRestore(String backupPath) {
        File backupFolder = new File(backupPath);
        // Test if the backup to restore still exists
        if (backupFolder.exists()) {
            // Test if the savegame directory exists
            File saveFolder = new File(savegameFolderPath);
            if (!saveFolder.exists()) {
                //<editor-fold desc="Save Folder not existent: create it or abort">
                // Create the folder, but ask for confirmation
                // Yes is 0 and No is 1
                int confirmation = JOptionPane.showConfirmDialog(this,
                        "The specified folder for NEOScavenger savegames doens't exist. \nDo you want to create it?",
                        "Missing savegame directory", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmation == 0) {
                    saveFolder.mkdirs();
                    // Check if creation succeded
                    if (!saveFolder.exists()) {
                        JOptionPane.showMessageDialog(this, "Failed to create the savegame folder!", "Aborting restore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Restore aborted! Please specify a valid path for the savegame directory",
                            "Aborting restore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //</editor-fold>
            }
            //<editor-fold desc="Actual restore">
            // Get all save files (*.sol)
            File[] sourceFiles = backupFolder.listFiles(savefileFilter);
            // Copy the files from the backup folder to the current save directory
            for (File f : sourceFiles) {
                File copy = new File(savegameFolderPath, f.getName());
                try {
                    Files.copy(Paths.get(f.getPath()), Paths.get(copy.getPath()), NOFOLLOW_LINKS);
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
            //</editor-fold>
            JOptionPane.showMessageDialog(this, "Restored backup \"" + backupFolder.getName() + "\" successfully.",
                    "Restore done", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error while restoring: the backup folder doens't exist!\n"
                    + "This is probably an invalid path: " + backupFolder.getPath() + "!",
                    "Restore error: Backup folder not found", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes all the files within the savegame directory.
     */
    private void performClear() {
        // Ask for confirmation
        // Yes is 0 and No is 1
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear your current savegame?\nAll files in the savegame folder will be DELETED.", "Confirm clear savegame", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmation == 0) {
            // Delete everything!
            File saveFolder = new File(savegameFolderPath);
            if (saveFolder.exists()) {
                for (File f : saveFolder.listFiles()) {
                    if (f.exists()) {
                        f.delete();
                        if (f.exists()) {
                            JOptionPane.showMessageDialog(this, "Error: can't delete file " + f.getPath(), "Deletion Error!", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
                JOptionPane.showMessageDialog(this, "The savegame directory has been cleared.", "Done clearing", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error: the savegame folder doens't exist!", "Savegame folder not found", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public Color getBgColor() {
        return backgroundColor;
    }

    private void openOptionsMenu() {
        new OptionsFrame(this);
    }

    private void openRestoreMenu() {
        RestoreFrame rf = new RestoreFrame(this);
        rf.init();
    }

    private void openDocumentation() {
        try {
            Desktop.getDesktop().open(new File(new File("./Readme.html").getCanonicalPath()));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "It seems the documentation (Readme.html) is missing.", "File not found", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fetches the folder paths from the config file
     */
    protected void readConfig() {
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
                    savegameFolderPath = st.nextToken();
                }

                // Read backup folder path
                s = in.readLine();
                st = new StringTokenizer(s, "=");
                if (st.countTokens() > 1) {
                    st.nextToken();
                    backupFolderPath = st.nextToken();
                }
                in.close();
            } catch (IOException ex) {
                System.out.println("I/O Read Error: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "It seems it's the first time you run this program.\nPlease open the options menu to provide the needed folder paths.", "Config file missing", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * @param names An array of filenames in the "YYYY_MM_DD HH-MM-SS" format.
     * @return
     */
    private String getNewestFile(String[] names) {
        if (names.length > 0) {
            String max = names[0];
            for (int i = 1; i < names.length; i++) {
                if (max.compareTo(names[i]) < 0) {
                    max = names[i];
                }
            }
            return max;
        } else {
            return "";
        }
    }

    private class InputManager implements ActionListener, KeyListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == backupBtn) {
                performBackup();
            }
            if (e.getSource() == optionsBtn) {
                openOptionsMenu();
            }
            if (e.getSource() == helpBtn) {
                openDocumentation();
            }
            if (e.getSource() == restoreBtn) {
                openRestoreMenu();
            }
            if (e.getSource() == restoreLastBtn) {
                restoreLastSave();
            }
            if (e.getSource() == clearBtn) {
                performClear();
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
                System.exit(0);
            }
        }
    }
}
