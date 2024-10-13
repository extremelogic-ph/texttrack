package ph.extremelogic.texttrack.utils;

import ph.extremelogic.texttrack.TextTrack;

public class Debug {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Debug() {
        // Prevent instantiation
    }

    public static void print(String data) {
        print(data, false);
    }

    public static void print(String data, boolean noLineBreak) {
        if (TextTrack.DEBUG) {
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
