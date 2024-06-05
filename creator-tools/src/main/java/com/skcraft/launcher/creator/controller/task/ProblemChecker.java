/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.skcraft.launcher.creator.controller.task;

import com.google.common.collect.Lists;
import com.skcraft.concurrency.ProgressObservable;
import com.skcraft.launcher.builder.BuilderOptions;
import com.skcraft.launcher.creator.model.creator.Pack;
import com.skcraft.launcher.creator.model.creator.Problem;
import com.skcraft.launcher.util.SharedLocale;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

public class ProblemChecker implements Callable<List<Problem>>, ProgressObservable {

    private final Pack pack;

    public ProblemChecker(Pack pack) {
        this.pack = pack;
    }

    @Override
    public List<Problem> call() throws Exception {
        List<Problem> problems = Lists.newArrayList();

        File packDir = pack.getDirectory();
        File srcDir = pack.getSourceDir();

        File loadersDir = new File(packDir, BuilderOptions.DEFAULT_LOADERS_DIRNAME);
        File modsDir = new File(srcDir, "mods");
        boolean hasLoaders = hasFiles(loadersDir);
        boolean hasMods = hasFiles(modsDir);

        String[] files;

        if (new File(packDir, "_CLIENT").exists()) {
            problems.add(new Problem(SharedLocale.tr("problem.root_client"), SharedLocale.tr("problem.root_client_message")));
        }

        if (new File(packDir, "_SERVER").exists()) {
            problems.add(new Problem(SharedLocale.tr("problem.root_server"), SharedLocale.tr("problem.root_server_message")));
        }

        if (new File(packDir, "mods").exists()) {
            problems.add(new Problem(SharedLocale.tr("problem.root_mods"), SharedLocale.tr("problem.root_mods_message")));
        }

        if (new File(packDir, "config").exists()) {
            problems.add(new Problem(SharedLocale.tr("problem.root_config"), SharedLocale.tr("problem.root_config_message")));
        }

        if (new File(packDir, "version.json").exists()) {
            problems.add(new Problem(SharedLocale.tr("problem.legacy_version"), SharedLocale.tr("problem.legacy_version_message")));
        }

        if (hasMods && !hasLoaders) {
            problems.add(new Problem(SharedLocale.tr("problem.no_loaders"), SharedLocale.tr("problem.no_loaders_message")));
        }

        return problems;
    }

    @Override
    public double getProgress() {
        return -1;
    }

    @Override
    public String getStatus() {
        return "Checking for problems...";
    }

    private static boolean hasFiles(File dir) {
        String[] contents = dir.list();
        return contents != null && contents.length > 0;
    }

}
