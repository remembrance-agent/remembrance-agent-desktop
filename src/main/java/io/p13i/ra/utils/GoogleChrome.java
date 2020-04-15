package io.p13i.ra.utils;

public class GoogleChrome {
    public static String getURLofActiveTab() {
        if (OS.is(OS.MAC_OS_X)) {
            return OS.appleScript("tell application \"Google Chrome\" to return URL of active tab of front window");
        }

        throw new UnsupportedOperationException(OS.name() + " not supported for call GoogleChrome::getURLofActiveTab");
    }
}
