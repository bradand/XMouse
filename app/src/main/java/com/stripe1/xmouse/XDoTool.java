package com.stripe1.xmouse;

public class XDoTool implements IDoTool {
    private final MyConnectionHandler connectionHandler;

    public XDoTool(MyConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    private void exec(String cmd) {
        connectionHandler.executeShellCommand("xdotool " + cmd);
    }

    public void mouseMoveRelative(float dx, float dy) {
        exec("mousemove_relative -- " + dx + " " + dy);
    }

    private int getMouseBtn(int btn) {
        switch(btn) {
            case IDoTool.MOUSE_LEFT: return 1;
            case IDoTool.MOUSE_MIDDLE: return 2;
            case IDoTool.MOUSE_RIGHT: return 3;
            default: throw new RuntimeException("button code " + btn + " not expected");
        }
    }

    public void mouseClick(int btn) {
        exec("click " + getMouseBtn(btn));
    }

    @Override
    public void mouseDown(int btn) {
        exec("mousedown " + getMouseBtn(btn));
    }

    @Override
    public void mouseUp(int btn) {
        exec("mouseup " + getMouseBtn(btn));
    }

    @Override
    public void mouseWheelUp() {
        exec("click 4" );
    }

    @Override
    public void mouseWheelDown() {
        exec("click 5" );
    }

    @Override
    public void key(String key) {
        exec("key " + key);
    }

    @Override
    public void type(String text) {
        exec("type " + text);
    }
}
