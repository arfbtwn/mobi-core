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
package little.nj.mobi.util;

import little.nj.mobi.format.*;
import little.nj.mobi.format.IndexRecord.IdxtEntry;
import little.nj.mobi.types.MobiBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.NavigableMap;
import java.util.TreeMap;

public class IndexUtil
{
    private IndexUtil () { }

    public static IndexRecord parseIndexRecord ( ByteBuffer buffer ) throws IOException
    {
        MobiBuffer mb = MobiBuffer.Instance;

        IndexRecord indexRecord = new IndexRecord ();

        IndxHeader recHead = mb.read ( IndxHeader.class, buffer );
        indexRecord.indxHead = recHead;

        if (0 < recHead.tagxOffset)
        {
            buffer.position (recHead.tagxOffset);
            indexRecord.tagxHead = mb.read ( TagxHeader.class, buffer );
        }

        if (0 < recHead.idxtOffset)
        {
            buffer.position (recHead.idxtOffset + 4);

            int[] idxtStarts = new int [recHead.indexCount + 1];
            idxtStarts [idxtStarts.length - 1] = recHead.idxtOffset;

            for (int i = 0, end = idxtStarts.length - 1; i < end; ++i)
            {
                idxtStarts [i] = buffer.getShort ();
            }

            for (int i = 0, end = idxtStarts.length - 1; i < end; ++i)
            {
                byte[] entry = new byte [idxtStarts[i + 1] - idxtStarts[i]];
                buffer.position (idxtStarts [i]);
                buffer.get(entry);

                String ident = new String(entry, 1, entry[0]);

                byte[] data = Arrays.copyOfRange (entry, 1 + entry [0], entry.length);

                indexRecord.idxt.put (idxtStarts[i], new IdxtEntry(ident, data));
            }
        }

        return indexRecord;
    }

    public static TagxTag[] getTags( TagxHeader tagx )
    {
        TagxTag[] _tags = new TagxTag [tagx.tags.length / 4];

        for (int i = 0, end = tagx.tags.length; i < end; i += 4)
        {
            _tags [i / 4] = new TagxTag ( tagx.tags, i );
        }

        return _tags;
    }


    public static NavigableMap < TagxTag, Long > decode ( IdxtEntry entry, int controlBytes, TagxTag[] tags )
    {
        NavigableMap < TagxTag, Long > result = new TreeMap <> ();

        byte[] cba = Arrays.copyOf (entry.bytes, controlBytes);

        for ( TagxTag tag : tags )
        {
            if ( tag.isEof () )
            {
                break;
            }

            int value = cba [0] & tag.bitMask;

            if ( 0 == value )
            {
                continue;
            }
            else if ( tag.bitMask != value )
            {
                byte mask = tag.bitMask;

                while (0 == (mask & 0x1))
                {
                    value >>= 1;
                    mask  >>= 1;
                }

                throw new UnsupportedOperationException ();
            }

            ByteBuffer buffer = ByteBuffer.wrap (entry.bytes, controlBytes, entry.bytes.length - controlBytes);
            result.put (tag, VariableWidthInteger.parseForward (buffer));
        }

        return result;
    }
}
