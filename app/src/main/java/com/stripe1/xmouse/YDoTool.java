package com.stripe1.xmouse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class YDoTool implements IDoTool {
    private static Map<String, String> KEYS_MAPPING;
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("CTRL", "LEFTCTRL");
        aMap.put("ALT", "LEFTALT");
        aMap.put("PLUS", "KPPLUS");
        aMap.put("MINUS", "KPMINUS");
        aMap.put("XF86AUDIORAISEVOLUME", "VOLUMEUP");
        aMap.put("XF86AUDIOLOWERVOLUME", "VOLUMEDOWN");
        aMap.put("XF86AUDIOMUTE", "MUTE");
        aMap.put("XF86AUDIOPREV", "PREVIOUSSONG");
        aMap.put("XF86AUDIONEXT", "NEXTSONG");
        aMap.put("XF86AUDIOPLAY", "PLAYPAUSE");
        aMap.put("XF86BACK", "BACK");
        aMap.put("XF86FORWARD", "FORWARD");
        aMap.put("XF86MONBRIGHTNESSUP", "BRIGHTNESSUP");
        aMap.put( "XF86MONBRIGHTNESSDOWN", "BRIGHTNESSDOWN");
        aMap.put( "SUPER", "LEFTMETA");
        aMap.put( "PRIOR", "PAGEUP");
        aMap.put( "NEXT", "PAGEDOWN");
        KEYS_MAPPING = Collections.unmodifiableMap(aMap);
    };

    private final MyConnectionHandler connectionHandler;

    public YDoTool(MyConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    private void exec(String cmd) {
        connectionHandler.executeShellCommand("ydotool " + cmd);
    }

    public void mouseMoveRelative(float dx, float dy) {
        exec("mousemove -- " + (int)dx + " " + (int)dy);
    }

    private int getMouseBtn(int btn) {
        switch(btn) {
            case IDoTool.MOUSE_LEFT: return 0;
            case IDoTool.MOUSE_MIDDLE: return 2;
            case IDoTool.MOUSE_RIGHT: return 1;
            default: throw new RuntimeException("button code " + btn + " not expected");
        }
    }

    public void mouseClick(int btn) {
        exec("click 0xC" + getMouseBtn(btn));
    }

    @Override
    public void mouseDown(int btn) {
        exec("click 0x4" + getMouseBtn(btn));
    }

    @Override
    public void mouseUp(int btn) {
        exec("click 0x8" + getMouseBtn(btn));
    }

    @Override
    public void mouseWheelUp() {
        key("up");
    }

    @Override
    public void mouseWheelDown() {
        key("down");
    }

    private String getInputKeyName(String key) {
        String keyFormatted = key.toUpperCase(Locale.ROOT);
        keyFormatted = KEYS_MAPPING.containsKey(keyFormatted) ? KEYS_MAPPING.get(keyFormatted) : keyFormatted;
        return "KEY_" + keyFormatted;
    }

    @Override
    public void key(String key) {
        String[] keys = key.split("\\+");
        StringBuilder down = new StringBuilder("");
        StringBuilder up = new StringBuilder("");
        for (String k: keys) {
            int keyCode = InputEventCodes.getCode(getInputKeyName(k));
            down.append(" ").append(keyCode).append(":1");
            up.insert(0, ":0").insert(0, keyCode).insert(0, " ");
        }
        exec("key" + down + up);
    }

    @Override
    public void type(String text) {
        exec("type " + text);
    }
}
