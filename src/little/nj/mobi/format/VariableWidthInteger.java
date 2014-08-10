/**
 * Copyright (C) 2014
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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import javax.xml.bind.DatatypeConverter;

public class VariableWidthInteger
{
    public static void main (String [] args)
    {
        long i = 0x11111;

        System.out.printf ("Original: %,d%n", i);
        System.out.println ();

        System.out.println ("## Forward ##");

        byte[] bs_fwd = encodeForward (i);

        System.out.printf ("Encoded:  %s%n", DatatypeConverter.printHexBinary (bs_fwd));

        long i_fwd = parseForward (bs_fwd, 0);

        System.out.printf ("Parsed:   %,d%n", i_fwd);
        System.out.println ();

        System.out.println ("## Backward ##");

        byte[] bs_bck = encodeBackward (i);

        System.out.printf ("Encoded:  %s%n", DatatypeConverter.printHexBinary (bs_bck));

        long i_bck = parseBackward (bs_bck, bs_bck.length - 1);

        System.out.printf ("Parsed:   %,d%n", i_bck);
    }

    public static byte[] encodeForward (long value)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream ();

        bos.write ((byte)(value & 0x7f | 0x80));
        value >>>= 7;
        do
        {
            bos.write ((byte)(value & 0x7f));
            value >>>= 7;
        }
        while (0 != value);

        byte[] arr = bos.toByteArray ();
        reverse (arr);
        return arr;
    }

    public static byte[] encodeBackward (long value)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream ();

        do
        {
            bos.write ((byte)(value & 0x7f));
            value >>>= 7;
        }
        while (0x7f < value);
        bos.write ((byte)(value & 0x7f | 0x80));

        byte[] arr = bos.toByteArray ();
        reverse (arr);
        return arr;
    }

    private static final void reverse (byte[] bytes)
    {
        for (int i = 0, j = bytes.length - 1; i < j; ++i, --j)
        {
            bytes[i] ^= bytes[j];
            bytes[j] ^= bytes[i];
            bytes[i] ^= bytes[j];
        }
    }

    public static long parseForward (ByteBuffer buffer)
    {
        long result = 0;
        byte b;
        do
        {
            b = buffer.get ();

            result <<= 7;
            result |= b & 0x7f;
        }
        while (0x80 != (b & 0x80));

        return result;
    }

    public static long parseForward (byte[] bytes, int offset)
    {
        return parseForward (ByteBuffer.wrap (bytes, offset, bytes.length - offset));
    }

    public static long parseBackward (byte[] bytes, int offset)
    {
        long result = 0;
        byte b;
        int length = 0;
        do
        {
            b = bytes [offset--];

            result |= (b & 0x7f) << (length++ * 7);
        }
        while (0x80 != (b & 0x80));

        return result;
    }

    private VariableWidthInteger () { }
}
