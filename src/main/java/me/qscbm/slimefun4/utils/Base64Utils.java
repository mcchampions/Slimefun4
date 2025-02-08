package me.qscbm.slimefun4.utils;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class Base64Utils {
    private static final String SYSTEM_LINE_SEPARATOR = System.lineSeparator();

    private static final int LINE_SEPARATOR_LENGTH = SYSTEM_LINE_SEPARATOR.length();

    public static String encodeLines(byte[] in) {
        final int blockLen = 57;

        final int iLen = in.length;

        int lines = (iLen + 56) / 57;
        int builderLen = ((iLen + 2) / 3 << 2) + lines * LINE_SEPARATOR_LENGTH;
        StringBuilder sb = new StringBuilder(builderLen);

        int l;
        for (int ip = 0; ip < iLen; ip += l) {
            l = Math.min(iLen - ip, blockLen);
            sb.append(Base64Coder.encode(in, ip, l));
            sb.append(SYSTEM_LINE_SEPARATOR);
        }

        return sb.toString();
    }
}
