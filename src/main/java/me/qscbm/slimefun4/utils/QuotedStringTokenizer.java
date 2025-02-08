/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package me.qscbm.slimefun4.utils;

import java.util.ArrayList;
import java.util.List;

public class QuotedStringTokenizer {
    private final char[] chars;
    private final int length;
    private int cursor;

    public QuotedStringTokenizer(String string) {
        this.chars = string.toCharArray();
        this.length = chars.length;
    }

    public List<String> tokenize() {
        final List<String> output = new ArrayList<>(5);
        while (cursor < length) {
            output.add(readString());
        }
        return output;
    }

    private String readString() {
        final char c = chars[cursor];
        return (c == '"') ? readQuotedString() : readUnquotedString();
    }

    private String readUnquotedString() {
        final int start = cursor;
        while (cursor < length && chars[cursor] != ' ') {
            cursor++;
        }
        final int end = cursor;

        if (cursor < length) {
            cursor++;
        }

        return new String(chars, start, end - start);
    }

    private String readQuotedString() {
        cursor++;

        final int start = cursor;
        while (cursor < length && chars[cursor] != '"') {
            cursor++;
        }
        final int end = cursor;

        if (cursor < length) {
            cursor++;
            if (cursor < length && chars[cursor] == ' ') {
                cursor++;
            }
        }

        return new String(chars, start, end - start);
    }
}
