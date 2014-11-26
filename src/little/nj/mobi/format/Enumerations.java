/**
 * Copyright (C) 2013
 * Nicholas J. Little <arealityfarbetween@googlemail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package little.nj.mobi.format;

import java.nio.charset.Charset;
import java.util.EnumSet;

public final class Enumerations {

    public static interface Value<T>
    {
        T getValue ();
    }

    public final static
    <E, T extends Enum<T> & Value<E>> T parse(E value, Class<T> clz)
    throws IllegalArgumentException
    {
        EnumSet<T> values = EnumSet.allOf (clz);

        for (T i : values)
        {
            if (i.getValue ().equals (value))
            {
                return i;
            }
        }

        throw new IllegalArgumentException ();
    }

    public final static class Result<T> implements Value<T>
    {
        final T value;
        Result(T value)
        {
            this.value = value;
        }

        Result()
        {
            this.value = null;
        }

        @Override
        public T getValue ()
        {
            return value;
        }

        public boolean hasValue ()
        {
            return null != value;
        }

        @Override
        public String toString ()
        {
            return null == value ? "null" : value.toString ();
        }
    }

    public final static
    <E, T extends Enum<T> & Value<E>> Result<T> tryParse(E value, Class<T> clz)
    {
        try
        {
            return new Result<T> (parse (value, clz));
        }
        catch (IllegalArgumentException ex)
        {
            return new Result<T> ();
        }
    }

    public static enum Compression implements Value<Short>
    {

        HUFF_CDIC(17480), NONE(1), PALMDOC(2);

        final short value;

        private Compression(int x)
        {
            value = (short) x;
        }

        @Override
        public Short getValue ()
        {
            return value;
        }
    }

    public static enum Encoding implements Value<Integer>
    {

        CP1252(1252) {

            @Override
            public Charset getCharset() {
                return Charset.forName("windows-1252");
            }
        },
        UTF8(65001) {

            @Override
            public Charset getCharset() {
                return Charset.forName("UTF8");
            }
        };

        final int value;

        private Encoding(int x)
        {
            value = x;
        }

        public Integer getValue ()
        {
            return value;
        }

        public abstract Charset getCharset();
    }

    public static enum Language implements Value<Short>
    {

        NONE (0), ENGLISH(9);

        private Language(int value)
        {
            this.value = (short) value;
        }

        final short value;

        @Override
        public Short getValue ()
        {
            return value;
        }
    }

    public static enum Dialect implements Value<Short>
    {

        NONE (0), BRITISH(8), AMERICAN(4);

        public final short value;

        private Dialect(int value)
        {
            this.value = (short) value;
        }

        @Override
        public Short getValue ()
        {
            return value;
        }
    }

    public static enum MobiType implements Value<Integer>
    {

        AUDIO(4), KF8_KINDLEGEN2(248), MOBI_BOOK(2), MOBI_KINDLEGEN1_2(232),
        NEWS(257), NEWS_FEED(258), NEWS_MAGAZINE(259), PALM_BOOK(3),
        PICS(513), PPT(516), TEXT(517), WORD(514), XLS(515), HTML(518);

        final int value;

        private MobiType(int x)
        {
            value = x;
        }

        @Override
        public Integer getValue ()
        {
            return value;
        }
    }

}
