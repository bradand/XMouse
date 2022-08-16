package com.stripe1.xmouse;

public interface IDoTool {
    int MOUSE_LEFT = 1;
    int MOUSE_MIDDLE = 2;
    int MOUSE_RIGHT = 3;

    void mouseMoveRelative(float dx, float dy);
    void mouseClick(int btn);
    void mouseDown(int btn);
    void mouseUp(int btn);
    void mouseWheelUp();
    void mouseWheelDown();
    void key(String key);
    void type(String text);
}
