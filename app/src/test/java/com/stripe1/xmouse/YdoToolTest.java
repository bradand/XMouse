package com.stripe1.xmouse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class YdoToolTest {
    @DataProvider
    public static Object[][] keyTestData() {
        return new Object[][]{
            {"XF86AudioRaiseVolume", "ydotool key 115:1 115:0"},
            {"Alt+F4", "ydotool key 56:1 62:1 62:0 56:0"},
            {"Ctrl+plus", "ydotool key 29:1 78:1 78:0 29:0"},
            {"ctrl+t", "ydotool key 29:1 20:1 20:0 29:0"},
            {"Ctrl+alt+t", "ydotool key 29:1 56:1 20:1 20:0 56:0 29:0"},
            {"Ctrl+Alt+F1", "ydotool key 29:1 56:1 59:1 59:0 56:0 29:0"},
        };
    }

    @Test
    @UseDataProvider("keyTestData")
    public void key(String key, String cmd) {
        MyConnectionHandler connectionHandler = Mockito.mock(MyConnectionHandler.class);
        YDoTool doTool = new YDoTool(connectionHandler);
        doTool.key(key);
        Mockito.verify(connectionHandler, Mockito.times(1)).executeShellCommand(cmd);
    }
}
