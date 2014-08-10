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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class IndexRecord
{
    public IndxHeader indxHead;
    public TagxHeader tagxHead;

    public List<IdxtEntry> idxtEntries = new ArrayList<IdxtEntry>();

    public void add (String ident, byte[] data)
    {
        idxtEntries.add (new IdxtEntry (ident, data));
    }

    public class IdxtEntry {

        public final String ident;
        public final byte[] bytes;

        IdxtEntry (String ident, byte[] bytes)
        {
            this.ident = ident;
            this.bytes = bytes;
        }

        public NavigableMap<TagxTag, Long> decode (int controlBytes, TagxTag[] tags)
        {
            NavigableMap<TagxTag, Long> result = new TreeMap<TagxTag, Long>();

            byte[] cba = Arrays.copyOf (bytes, controlBytes);

            for (TagxTag tag : tags)
            {
                if (1 == tag.eof)
                {
                    continue;
                }

                int value = cba [0] & tag.bitMask;

                if (0 == value)
                {
                    continue;
                }
                else if (tag.bitMask != value)
                {
                    byte mask = tag.bitMask;

                    while (0 == (mask & 0x1))
                    {
                        value >>= 1;
                        mask  >>= 1;
                    }

                    throw new UnsupportedOperationException ();
                }

                ByteBuffer buffer = ByteBuffer.wrap (bytes, controlBytes, bytes.length - controlBytes);
                result.put (tag, VariableWidthInteger.parseForward (buffer));
            }

            return result;
        }
    }
}
