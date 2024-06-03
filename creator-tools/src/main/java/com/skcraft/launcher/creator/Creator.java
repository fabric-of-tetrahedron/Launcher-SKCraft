/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.skcraft.launcher.creator;

import com.formdev.flatlaf.FlatLightLaf;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.creator.controller.WelcomeController;
import com.skcraft.launcher.creator.dialog.WelcomeDialog;
import com.skcraft.launcher.creator.model.creator.CreatorConfig;
import com.skcraft.launcher.creator.model.creator.RecentEntry;
import com.skcraft.launcher.creator.model.creator.Workspace;
import com.skcraft.launcher.persistence.Persistence;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.util.SharedLocale;
import lombok.Getter;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import static com.skcraft.launcher.Launcher.setUIFont;
import static com.skcraft.launcher.Launcher.uiStyle;

public class Creator {

    @Getter private final File dataDir;
    @Getter private final CreatorConfig config;
    @Getter private final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    public Creator() {
        SharedLocale.loadBundle("com.skcraft.launcher.creator.lang.Creator", Locale.getDefault());

        this.dataDir = getAppDataDir();
        this.config = Persistence.load(new File(dataDir, "config.json"), CreatorConfig.class);

        // Remove deleted workspaces
        List<RecentEntry> recentEntries = config.getRecentEntries();
        Iterator<RecentEntry> it = recentEntries.iterator();
        while (it.hasNext()) {
            RecentEntry workspace = it.next();
            if (!Workspace.getWorkspaceFile(workspace.getPath()).exists()) {
                it.remove();
            }
        }
    }

    public void showWelcome() {
        WelcomeDialog dialog = new WelcomeDialog();
        WelcomeController controller = new WelcomeController(dialog, this);
        controller.show();
    }

    private static File getFileChooseDefaultDir() {
        JFileChooser chooser = new JFileChooser();
        FileSystemView fsv = chooser.getFileSystemView();
        return fsv.getDefaultDirectory();
    }

    private static File getAppDataDir() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return new File(getFileChooseDefaultDir(), "SKCraft Modpack Creator");
        } else {
            return new File(System.getProperty("user.home"), ".skcraftcreator");
        }
    }

    public static void main(String[] args) throws Exception {
        Launcher.setupLogger();
        System.setProperty("skcraftLauncher.killWithoutConfirm", "true");

        final Creator creator = new Creator();

        SwingUtilities.invokeAndWait(() -> {
            SwingHelper.setSwingProperties("Modpack Creator");
            SwingHelper.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            try {
                uiStyle(14f);
            } catch (FontFormatException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                creator.showWelcome();
            } catch (Exception e) {
                SwingHelper.showErrorDialog(null, "Failed to start the modpack creator program.", "Start Error", e);
            }
        });
    }

}
