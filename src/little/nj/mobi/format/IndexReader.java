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
import java.util.Arrays;
import little.nj.data.MarshalBuilder;
import little.nj.data.MarshalRoot;

public class IndexReader
{
    public IndexReader () { }

    public IndexRecord parseIndexRecord (ByteBuffer buffer)
    {
        MarshalBuilder mb = MarshalRoot.getInstance ();

        IndexRecord indexRecord = new IndexRecord ();

        IndxHeader recHead = mb.read (buffer, IndxHeader.class);
        indexRecord.indxHead = recHead;

        if (0 < recHead.tagxOffset)
        {
            buffer.position (recHead.tagxOffset);
            indexRecord.tagxHead = mb.read (buffer, TagxHeader.class);
        }

        if (0 < recHead.idxtOffset)
        {
            buffer.position (recHead.idxtOffset + 4);

            int[] idxtStarts = new int [recHead.indexEntryCount + 1];
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

                indexRecord.add (ident, data);
            }
        }

        return indexRecord;
    }
}
