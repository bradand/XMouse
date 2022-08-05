package com.stripe1.xmouse;

import android.content.Context;
import android.view.LayoutInflater;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

@RunWith(DataProviderRunner.class)
public class CustomKeyboardButtonAdapterTest {
    @DataProvider
    public static Object[][] commandTestData() {
        return new Object[][]{
                {"xdotool key ctrl+alt+f1"  , "ydotool key 29:1 56:1 59:1 59:0 56:0 29:0" , "xdotool key ctrl+alt+f1"},
                {"xdotool windowsize 1"     , "xdotool windowsize 1"                      , "xdotool windowsize 1"},
                {"key ctrl+alt+f1"          , "ydotool key 29:1 56:1 59:1 59:0 56:0 29:0" , "xdotool key ctrl+alt+f1"},
                {"mousemoverelative -12 5"  , "ydotool mousemove -- -12 5"                , "xdotool mousemove_relative -- -12.0 5.0"},
                {"mouseclick 1"             , "ydotool click 0xC0"                        , "xdotool click 1"},
                {"mousedown 1"              , "ydotool click 0x40"                        , "xdotool mousedown 1"},
                {"mouseup 1"                , "ydotool click 0x80"                        , "xdotool mouseup 1"},
                {"mousewheelup"             , "ydotool key 103:1 103:0"                   , "xdotool click 4"},
                {"mousewheeldown"           , "ydotool key 108:1 108:0"                   , "xdotool click 5"},
                {"type hello"               , "ydotool type hello"                        , "xdotool type hello"},
                {"ssh hello@world"          , "ssh hello@world"                           , "ssh hello@world"},
        };
    }

    @Test
    @UseDataProvider("commandTestData")
    public void execYdoTool(String command, String yDoToolCmd, String xDoToolCmd) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        MyConnectionHandler connectionHandler = Mockito.mock(MyConnectionHandler.class);
        runExecTest(command, yDoToolCmd, new YDoTool(connectionHandler), connectionHandler);
    }

    @Test
    @UseDataProvider("commandTestData")
    public void execXdoTool(String command, String yDoToolCmd, String xDoToolCmd) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        MyConnectionHandler connectionHandler = Mockito.mock(MyConnectionHandler.class);
        runExecTest(command, xDoToolCmd, new XDoTool(connectionHandler), connectionHandler);
    }

    private void runExecTest(String command, String shellCommand, IDoTool doTool, MyConnectionHandler connectionHandler) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        MockedStatic<LayoutInflater> layoutInflater = Mockito.mockStatic(LayoutInflater.class);
        CustomKeyboardButtonAdapter buttonAdapter = new CustomKeyboardButtonAdapter(Mockito.mock(Context.class), new LinkedList<>());
        layoutInflater.close();

        Method method = CustomKeyboardButtonAdapter.class.getDeclaredMethod("execCommand", String.class, IDoTool.class, MyConnectionHandler.class);
        method.setAccessible(true);

        method.invoke(buttonAdapter, command, doTool, connectionHandler);
        Mockito.verify(connectionHandler, Mockito.times(1)).executeShellCommand(shellCommand);
    }
}
