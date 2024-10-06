package ph.extremelogic.texttrack.utils;

import ph.extremelogic.texttrack.TextTrack;

public class Debug {
    public static void print(String data) {
        print(data, false);
    }
    public static void print(String data, boolean noLineBreak) {
        if (TextTrack.debug) {
            if (noLineBreak) {
                System.out.print(data);
            } else {
                System.out.println(data);
            }
        }
    }
}
