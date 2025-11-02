package com.avez.visualizer;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Handles UI rendering with Unicode/ASCII fallback support
 */
public class UIRenderer {

    private static boolean useUnicode = true;

    // Icons
    public static final String ICON_CHART = useUnicode ? "📊" : "[CHART]";
    public static final String ICON_FILE = useUnicode ? "📁" : "[FILE]";
    public static final String ICON_SUCCESS = useUnicode ? "✅" : "[OK]";
    public static final String ICON_ERROR = useUnicode ? "❌" : "[ERROR]";
    public static final String ICON_LOADING = useUnicode ? "⏳" : "[...]";
    public static final String ICON_INFO = useUnicode ? "💡" : "[INFO]";
    public static final String ICON_WARNING = useUnicode ? "⚠️" : "[WARN]";
    public static final String ICON_SAVE = useUnicode ? "💾" : "[SAVE]";
    public static final String ICON_NUMBER = useUnicode ? "🔢" : "[#]";
    public static final String ICON_RELOAD = useUnicode ? "🔄" : "[RELOAD]";
    public static final String ICON_WAVE = useUnicode ? "👋" : "[BYE]";
    public static final String ICON_PIN = useUnicode ? "📍" : "[->]";
    public static final String ICON_ART = useUnicode ? "🎨" : "[ART]";
    public static final String ICON_ROWS = useUnicode ? "📊" : "[ROWS]";
    public static final String ICON_COLS = useUnicode ? "📋" : "[COLS]";
    public static final String ICON_GRAPH = useUnicode ? "📈" : "[GRAPH]";

    // Box drawing characters
    private static final String BOX_TL = useUnicode ? "╔" : "+";  // Top-left
    private static final String BOX_TR = useUnicode ? "╗" : "+";  // Top-right
    private static final String BOX_BL = useUnicode ? "╚" : "+";  // Bottom-left
    private static final String BOX_BR = useUnicode ? "╝" : "+";  // Bottom-right
    private static final String BOX_H = useUnicode ? "═" : "=";   // Horizontal
    private static final String BOX_V = useUnicode ? "║" : "|";   // Vertical

    /**
     * Initialize UTF-8 encoding for console output
     */
    public static void initializeEncoding() {
        try {
            // Try to set UTF-8 encoding
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            System.setErr(new PrintStream(System.err, true, "UTF-8"));

            // Check if UTF-8 is actually supported
            useUnicode = Charset.defaultCharset().name().toUpperCase().contains("UTF");

            if (!useUnicode) {
                System.out.println("[INFO] Unicode not fully supported, using ASCII mode");
            }

        } catch (UnsupportedEncodingException e) {
            // Fallback to ASCII
            useUnicode = false;
            System.out.println("[INFO] UTF-8 encoding not available, using ASCII mode");
        }

        // Update icon values based on useUnicode flag (this is done via getters)
    }

    /**
     * Force ASCII mode (useful for testing or when Unicode causes issues)
     */
    public static void forceASCIIMode() {
        useUnicode = false;
    }

    /**
     * Print a welcome box
     */
    public static void printWelcomeBox(String title, String subtitle) {
        // Simplified box without centering to avoid emoji width issues
        int width = 44;
        String line = repeat(BOX_H, width);

        System.out.println(BOX_TL + line + BOX_TR);
        System.out.println(BOX_V + "  " + title + repeat(" ", Math.max(0, width - getDisplayWidth(title) - 2)) + BOX_V);
        System.out.println(BOX_V + "  " + subtitle + repeat(" ", Math.max(0, width - subtitle.length() - 2)) + BOX_V);
        System.out.println(BOX_BL + line + BOX_BR);
        System.out.println();
    }

    /**
     * Print a section header
     */
    public static void printSectionHeader(String text) {
        System.out.println("\n" + repeat("=", 50));
        System.out.println(text);
        System.out.println(repeat("=", 50));
    }

    /**
     * Get the display width of text (emojis count as 2 characters)
     */
    private static int getDisplayWidth(String text) {
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // Emojis and other wide characters
            if (Character.isSupplementaryCodePoint(text.codePointAt(i))) {
                width += 2;
                i++; // Skip the next char as it's part of the surrogate pair
            } else if (c > 0x7F) {
                // Non-ASCII character (emoji, etc.) - count as 2
                width += 2;
            } else {
                width += 1;
            }
        }
        return width;
    }

    /**
     * Repeat a string n times
     */
    private static String repeat(String str, int count) {
        return new String(new char[count]).replace("\0", str);
    }

    /**
     * Get icon based on current mode (Unicode or ASCII)
     */
    public static String getIcon(String iconType) {
        switch (iconType) {
            case "chart": return useUnicode ? "📊" : "[CHART]";
            case "file": return useUnicode ? "📁" : "[FILE]";
            case "success": return useUnicode ? "✅" : "[OK]";
            case "error": return useUnicode ? "❌" : "[ERROR]";
            case "loading": return useUnicode ? "⏳" : "[...]";
            case "info": return useUnicode ? "💡" : "[INFO]";
            case "warning": return useUnicode ? "⚠️" : "[WARN]";
            case "save": return useUnicode ? "💾" : "[SAVE]";
            case "number": return useUnicode ? "🔢" : "[#]";
            case "reload": return useUnicode ? "🔄" : "[RELOAD]";
            case "wave": return useUnicode ? "👋" : "[BYE]";
            case "pin": return useUnicode ? "📍" : "[->]";
            case "art": return useUnicode ? "🎨" : "[ART]";
            case "rows": return useUnicode ? "📊" : "[ROWS]";
            case "cols": return useUnicode ? "📋" : "[COLS]";
            case "graph": return useUnicode ? "📈" : "[GRAPH]";
            default: return useUnicode ? "•" : "*";
        }
    }
}
