/*
 * Martian Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.foxstudio.martianlauncher.game.control.zalith.model;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

/**
 * Gson mirror of ZalithLauncher's net.kdt.pojavlaunch.customcontrols.CustomControls.
 * This is the top-level shape of a Zalith control layout JSON file.
 */
@Keep
public class ZLCustomControls {
    public int version = 8;
    public float scaledAt = 100f;
    public String name = "";
    public String author = "";
    public String versionName = "";
    public List<ZLControlData> mControlDataList = new ArrayList<>();
    public List<ZLControlDrawerData> mDrawerDataList = new ArrayList<>();
    public List<ZLControlJoystickData> mJoystickDataList = new ArrayList<>();

    public ZLCustomControls() {
    }
}
