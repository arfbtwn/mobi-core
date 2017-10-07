/**
 * Copyright (C) 2013 Nicholas J. Little <arealityfarbetween@googlemail.com>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package little.nj.mobi.codecs;

import static little.nj.util.ConversionUtil.convert;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import little.nj.algorithms.KmpSearch;

public class PalmDocCodec implements Codec {

    /**
     * The default back track distance for repeated string
     * compression (0x7ff == 2047)
     */
    public static final int DEFAULT_DISTANCE = 0x7ff;

    /**
     * The default set of techniques, all of them
     */
    public static final EnumSet<Technique> DEFAULT_TECHNIQUES =
            EnumSet.allOf(Technique.class);

    /**
     * A class to set options pertaining to PalmDoc compression
     *
     * @author Nicholas Little
     *
     */
    public static class CompressionOptions {

        private EnumSet<Technique> techniques = DEFAULT_TECHNIQUES;

        private int distance = DEFAULT_DISTANCE;

        /**
         * @return the techniques
         */
        public EnumSet<Technique> getTechniques() {
            return techniques;
        }

        /**
         * @param techniques
         *            the techniques to set
         */
        public void setTechniques(EnumSet<Technique> techniques) {
            this.techniques = techniques;
        }

        /**
         * @return the distance
         */
        public int getDistance() {
            return distance;
        }

        /**
         * @param distance
         *            the distance to set
         */
        public void setDistance(int distance) {
            this.distance = distance;
        }

    }

    /**
     * An object to track the compression job
     *
     * @author Nicholas
     *
     */
    public static class CompressionStats {

        int i_length = 0;

        int o_length = 0;

        private Map<Byte[], Integer> rep_ba_pos = new HashMap<Byte[], Integer>();

        int repeats = 0;

        int spaces = 0;

        public int getPosition(byte[] bytes) {
            Byte[] barr = convert(bytes);
            Integer rv = rep_ba_pos.get(barr);
            return rv == null ? -1 : rv;
        }

        public void putBytes(byte[] bytes, int pos) {
            Byte[] barr = convert(bytes);
            rep_ba_pos.put(barr, pos);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            double ratio = (double) i_length / o_length;
            return String.format(
                    "Repeats: %3d, Unique Strings: %3d, Spaces: %3d\n"
                            + "Input: %,d, Output: %,d\n"
                            + "Compression Ratio: %2.1f", repeats,
                    rep_ba_pos.size(), spaces, i_length, o_length, ratio);
        }
    }

    /**
     * An enum of compression techniques to use
     *
     * @author Nicholas
     *
     */
    public enum Technique {
        REPEATS, SPACES
    }

    /*
     * Fields
     */
    private ByteBuffer raw;
    private CompressionStats stats;
    private CompressionOptions options;

    /**
     * Default construction
     */
    public PalmDocCodec() {
        options = new CompressionOptions();
        stats = new CompressionStats();
    }

    /**
     * @return the options
     */
    public CompressionOptions getOptions() {
        return options;
    }

    /**
     * @param options
     *            the options to set
     */
    public void setOptions(CompressionOptions options) {
        this.options = options;
    }

    /*
     * (non-Javadoc)
     *
     * @see algorithms.ICodec#compress(byte[])
     */
    @Override
    public byte[] compress(byte[] input) {
        stats.i_length = input.length;
        ByteArrayOutputStream o_stream = new ByteArrayOutputStream(input.length);
        main: for (int i = 0; i < input.length;) {
            short b = (short) (input[i] & 0xff);
            short s;
            /*
             * Repeatable sequences of 3-10 bytes in the last 2047
             */
            if (options.techniques.contains(Technique.REPEATS))
                repeats: for (int j = 10; j >= 3; --j) {

                    if (i + j >= input.length || i < j)
                        continue repeats;

                    byte[] pattern = new byte[j];
                    System.arraycopy(input, i, pattern, 0, pattern.length);

                    /*
                     * 0x7ff == 2047 == 11 bits of length
                     */
                    int res = stats.getPosition(pattern);
                    if (res < 0 || i - res > options.distance)
                        res = KmpSearch.indexOf(input, pattern, i
                                - options.distance, i);

                    if (res > -1 && res < i) {
                        // Make a note of this one for later
                        stats.putBytes(pattern, res);

                        // Encode distance & length
                        int dis = i - res, len = j - 3;
                        b = (short) (0x8000 + (dis << 3) + len);
                        o_stream.write(b >>> 8);
                        o_stream.write(b & 0xff);
                        i += j;
                        ++stats.repeats;
                        continue main;
                    }
                }
            /*
             * Collapse spaces into the next character if they fall within '@'
             * and '~' range
             */
            if (options.techniques.contains(Technique.SPACES)) {
                s = i + 1 < input.length ? (short) (input[i + 1] & 0xff) : 0x0;
                if (b == 0x20 && s >= 0x40 && s <= 0x7e) {
                    b = (short) (s | 0x80); // Mark the high bit
                    o_stream.write(b);
                    i += 2;
                    ++stats.spaces;
                    continue main;
                }
            }
            /*
             * Count high range characters and write them plus their count
             */
            if (b >= 0x80 && b <= 0xff) {
                int j = i;
                do {
                    if (j >= input.length - 1 || j - i >= 8)
                        break;
                    s = (short) (input[++j] & 0xff);
                } while (s >= 0x80 && s <= 0xff);

                if (j - i > 0) {
                    o_stream.write(j - i);
                    for (; i < j; ++i)
                        o_stream.write(input[i]);
                } else {
                    o_stream.write(1);
                    o_stream.write(b);
                    ++i;
                }
                continue main;
            }
            /*
             * Represent other bytes
             */
            if (b == 0x00 || b >= 0x09 && b <= 0x7f) {
                o_stream.write(b);
                ++i;
            }
        }
        o_stream.write(0x0); // HACK: Finish off with a null byte

        stats.o_length = o_stream.size();

        System.out.println(stats.toString());

        return o_stream.toByteArray();
    }

    /*
     * (non-Javadoc)
     *
     * @see algorithms.ICodec#decompress(byte[])
     */
    @Override
    public byte[] decompress(byte[] input) {
        raw = ByteBuffer.allocate(input.length * 4);
        int len = decompressPalmDoc(ByteBuffer.wrap(input));
        raw.rewind();
        byte[] rv = new byte[len];
        raw.get(rv);
        return rv;
    }

    private int decompressPalmDoc(ByteBuffer in) {
        try {
            do {
                short b = (short) (in.get() & 0xff);
                if (b == 0x00) {
                    /*
                     * Null represents itself
                     */
                    raw.put((byte) b);
                } else if (b >= 0x1 && b <= 0x8)
                    /*
                     * 1 - 8 Literals (high range characters)
                     */
                    for (byte i = 0; i < b; ++i)
                        raw.put(in.get());
                else if (b >= 0x9 && b <= 0x7f)
                    /*
                     * Literals
                     */
                    raw.put((byte) b);
                else if (b >= 0x80 && b <= 0xbf) {
                    /*
                     * Distance - Length pairs (see compression for detail)
                     */
                    b = (short) (b << 8);
                    b = (short) (b | in.get() & 0xff);

                    short back = (short) (b >>> 3 & 0x7ff); // 11 bits
                    byte length = (byte) (b & 0x7); // 3 bits

                    int pos = raw.position();
                    raw.position(pos - back);
                    byte[] tmp = new byte[length + 3];
                    raw.get(tmp);
                    raw.position(pos);
                    raw.put(tmp);

                } else if (b >= 0xc0 && b <= 0xff) {
                    /*
                     * Collapsed space
                     */
                    raw.put((byte) 0x20);
                    b = (short) (b ^ 0x80);
                    raw.put((byte) b);
                }

                if (!in.hasRemaining())
                    break;

            } while (in.position() < in.capacity());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return raw.position();
    }

    public CompressionStats getStats() {
        return stats;
    }
}
