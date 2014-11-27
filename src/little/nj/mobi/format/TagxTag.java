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


public class TagxTag implements Comparable<TagxTag>
{
    public final byte id;
    public final byte numValues;
    public final byte bitMask;
    public final byte eof;

    TagxTag(byte[] bytes, int offset)
    {
        id        = bytes [offset];
        numValues = bytes [offset + 1];
        bitMask   = bytes [offset + 2];
        eof       = bytes [offset + 3];
    }

    public TagxTag (byte id, byte numValues, byte bitMask)
    {
        this.id        = id;
        this.numValues = numValues;
        this.bitMask   = bitMask;
        this.eof       = 0;
    }

    public TagxTag ()
    {
        id        = 0;
        numValues = 0;
        bitMask   = 0;
        eof       = 1;
    }

    @Override
    public String toString ()
    {
        return String.format (
            "{ ID = %d, Values = %d, Mask = %d, Eof = %d }",
            id, numValues, bitMask, eof
        );
    }

    @Override
    public int compareTo (TagxTag o)
    {
        return id - o.id;
    }
}
