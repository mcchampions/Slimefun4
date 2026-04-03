package me.qscbm.slimefun4.utils;


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
            encode(in, ip, l);
            sb.append(SYSTEM_LINE_SEPARATOR);
        }

        return sb.toString();
    }
    private static final String systemLineSeparator = System.getProperty("line.separator");
    private static final char[] map1 = new char[64];
    private static final byte[] map2;

     public static char[] encode(byte[] in, int iOff, int iLen) {
        int oDataLen = (iLen * 4 + 2) / 3;
        int oLen = (iLen + 2) / 3 * 4;
        char[] out = new char[oLen];
        int ip = iOff;
        int iEnd = iOff + iLen;

        for(int op = 0; ip < iEnd; ++op) {
            int i0 = in[ip++] & 255;
            int i1 = ip < iEnd ? in[ip++] & 255 : 0;
            int i2 = ip < iEnd ? in[ip++] & 255 : 0;
            int o0 = i0 >>> 2;
            int o1 = (i0 & 3) << 4 | i1 >>> 4;
            int o2 = (i1 & 15) << 2 | i2 >>> 6;
            int o3 = i2 & 63;
            out[op++] = map1[o0];
            out[op++] = map1[o1];
            out[op] = op < oDataLen ? map1[o2] : 61;
            ++op;
            out[op] = op < oDataLen ? map1[o3] : 61;
        }

        return out;
    }

    static {
        int i = 0;

        char c;
        for(c = 'A'; c <= 'Z'; map1[i++] = c++) {
        }

        for(c = 'a'; c <= 'z'; map1[i++] = c++) {
        }

        for(c = '0'; c <= '9'; map1[i++] = c++) {
        }

        map1[i++] = '+';
        map1[i++] = '/';
        map2 = new byte[128];

        for(i = 0; i < map2.length; ++i) {
            map2[i] = -1;
        }

        for(i = 0; i < 64; ++i) {
            map2[map1[i]] = (byte)i;
        }

    }
}