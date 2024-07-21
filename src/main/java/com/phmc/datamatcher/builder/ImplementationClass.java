package com.phmc.datamatcher.builder;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class ImplementationClass {
    private final byte[] bytes;
    private final String className;

    public ImplementationClass(String className, String body) {
        this(className, body.getBytes(StandardCharsets.UTF_8));
    }

    public ImplementationClass(String className, byte[] bodyBytes) {
        if (StringUtils.isBlank(className)) throw new IllegalArgumentException("Class name can not be empty");
        if (bodyBytes == null || bodyBytes.length == 0) throw new IllegalArgumentException("Class body can not be empty");
        this.className = className;
        this.bytes = bodyBytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        String simpleName = getClassSimpleName();
        return className.replace(".".concat(simpleName), "");
    }

    public String getClassSimpleName() {
        String[] s = this.className.split("\\.");
        return s.length == 1 ? s[0] : s[s.length - 1];
    }

    public String beautify() {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        List<String> doubleBreakLineWords = Arrays.asList("package","class", "}");
        List<String> breakLineWords = Arrays.asList("import","public","private","protected","@","&&","||");
        List<String> breakLineTabWords = Arrays.asList("&&","||");

        StringBuilder sb = new StringBuilder();
        char[] chArray = new String(bytes, StandardCharsets.UTF_8).toCharArray();
        String tab = "";
        boolean newLine = true;
        StringBuilder line = new StringBuilder();
        for (char ch : chArray) {
            switch (ch) {
                case '{':
                    tab = tab + "    ";
                case '}':
                    tab = tab.substring(0, tab.length() - 4);
                case ';':
                    // Break line
                    line.append(ch).append("\n").append(tab);
                    newLine = true;
                    break;
                case ' ':
                    if(!newLine || line.length() > 0)
                        line.append(ch);
                    break;
                default:
                    line.append(ch);
//                    sb.append(ch);
                    newLine = false;
            }

            String lineStr = line.toString();
            if (!sb.toString().endsWith("\n") && !line.toString().endsWith("\n")) {
                StringBuilder aux = line;
                if (breakLineTabWords.stream().anyMatch(lineStr::contains)) {
                    tab = tab + "    ";
                }
                if (breakLineWords.stream().anyMatch(lineStr::contains)) {
                    line = new StringBuilder().append(tab).append("\n").append(aux);
                }
            }
            else if (doubleBreakLineWords.stream().anyMatch(lineStr::contains)) {
                line.append("\n"); // Double break
            }

            if (newLine) {
                sb.append(line);
                line = new StringBuilder();
//                newLine = false;
            }
        }

        System.out.println(sb);
        return sb.toString();
    }
}
