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

/**
 * Gson mirror of ZalithLauncher's net.kdt.pojavlaunch.customcontrols.ControlData.
 * Field names must match exactly so imported Zalith JSON parses correctly.
 */
@Keep
public class ZLControlData {

    public static final int SPECIALBTN_KEYBOARD = -1;
    public static final int SPECIALBTN_TOGGLECTRL = -2;
    public static final int SPECIALBTN_MOUSEPRI = -3;
    public static final int SPECIALBTN_MOUSESEC = -4;
    public static final int SPECIALBTN_VIRTUALMOUSE = -5;
    public static final int SPECIALBTN_MOUSEMID = -6;
    public static final int SPECIALBTN_SCROLLUP = -7;
    public static final int SPECIALBTN_SCROLLDOWN = -8;
    public static final int SPECIALBTN_MENU = -9;

    /** Dynamic position expressions, e.g. "${margin}" or "0.5 * ${screen_width}" */
    public String dynamicX = "0";
    public String dynamicY = "0";
    public boolean isToggle = false;
    public boolean passThruEnabled = false;
    public String name = "";
    /** Up to 4 keycodes; >=0 means an LWJGL index, <0 means a SPECIALBTN_* action */
    public int[] keycodes = new int[]{ 0, 0, 0, 0 };
    public float opacity = 1f;
    public int bgColor = 0x4D000000;
    public int strokeColor = 0xFFFFFFFF;
    /** Dp */
    public float strokeWidth = 0f;
    /** 0-100 percent of half of the smaller side */
    public float cornerRadius = 0f;
    public boolean isSwipeable = false;
    public boolean displayInGame = true;
    public boolean displayInMenu = true;
    /** Dp (stored, converted to px at render time) */
    public float width = 50f;
    public float height = 50f;

    public ZLControlData() {
    }

    public boolean containsSpecial() {
        for (int keycode : keycodes) {
            if (keycode < 0) return true;
        }
        return false;
    }
}
