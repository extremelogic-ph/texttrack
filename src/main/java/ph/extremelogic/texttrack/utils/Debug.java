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

    public static void printDataArray(byte[] data, int size) {
        if (size > 200) return;
        print("Data array: [", true);
        for (int i = 0; i < size; i++) {
            print(String.format("%02X ", data[i] & 0xFF), true);
        }
        print("] SIZE: " + size, false);
    }
}
