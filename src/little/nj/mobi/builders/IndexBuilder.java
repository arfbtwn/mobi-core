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
package little.nj.mobi.builders;

import little.nj.mobi.format.IndexRecord;
import little.nj.mobi.format.IndexRecord.IdxtEntry;
import little.nj.mobi.format.IndxHeader;
import little.nj.mobi.format.NcxRecord;
import little.nj.mobi.format.TagxHeader;
import little.nj.mobi.format.TagxTag;
import little.nj.mobi.format.VariableWidthInteger;
import little.nj.mobi.model.Index;
import little.nj.mobi.model.Index.IndexEntry;

public class IndexBuilder
{
    Index index;

    TagxTag[] tags;
    IndexRecord mainIndex;
    IndexRecord[] records;
    NcxRecord   ncxRecord;

    public IndexBuilder index (Index index)
    {
        this.index = index;
        return this;
    }

    public IndexBuilder build ()
    {
        int tags_required = 0;
        int tags_length = 1;

        for (IndexEntry i : index)
        {
            int mask = 0x1;

            while (0 == (i.offset & mask))
            {
                mask <<= 1;
            }

            int tag = Integer.numberOfTrailingZeros (mask);
            int len = (int) Math.ceil (tag / 8.0);

            if (tags_required < tag)
            {
                tags_required = tag;
            }

            if (tags_length < len)
            {
                tags_length = len;
            }
        }

        tags = new TagxTag[tags_required + 1];
        tags [tags_required] = TagxTag.end ();
        for (int i = 0; i < tags_required; ++i)
        {
            byte mask = (byte) (0x1 << i - 1);
            tags [i] = new TagxTag ((byte) i, (byte) tags_length, mask);
        }

        IndexEntry last = index.last ();

        mainIndex = new IndexRecord ();
        mainIndex.indxHead = new IndxHeader ();
        mainIndex.indxHead.firstEntryOffset = IndxHeader.LENGTH;
        mainIndex.indxHead.indxType = 0;
        mainIndex.indxHead.indexCount = 1;
        mainIndex.indxHead.cncxCount = 1;
        mainIndex.indxHead.totalEntryCount = index.size ();
        mainIndex.tagxHead = new TagxHeader ();
        mainIndex.tagxHead.length = 12 + 4 * tags.length;
        mainIndex.tagxHead.controlBytes = tags_length;
        mainIndex.indxHead.idxtOffset = IndxHeader.LENGTH + mainIndex.tagxHead.length;

        int offset = mainIndex.indxHead.idxtOffset;

        mainIndex.idxt.put (offset -= 2, new IdxtEntry(last.ident, new byte[] { 0, 0 }));

        ncxRecord = new NcxRecord ();
        IndexRecord aux = new IndexRecord ();
        aux.indxHead.indexCount = 1;
        aux.indxHead.totalEntryCount = index.size ();
        for (IndexEntry i : index)
        {
            ncxRecord.add (i.text);
            byte[] encoded = VariableWidthInteger.encodeForward (i.offset);
            aux.idxt.put (offset -= encoded.length, new IdxtEntry(i.ident, encoded));
        }
        records = new IndexRecord [] { aux };

        return this;
    }

}
