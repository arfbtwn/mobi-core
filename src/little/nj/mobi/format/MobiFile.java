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
import java.util.List;

import little.nj.data.MarshalBuilder;
import little.nj.data.MarshalRoot;

public class MobiFile {

    public PdbFile       pdbFile;

    public PalmDocHeader palmHeader;
    public MobiDocHeader mobiHeader;
    public ExthHeader    exthHeader;

    public IndexRecord[] indexRecords;

    public NcxRecord     ncxRecord;

    public boolean hasIndex ()
    {
        return null != indexRecords && 0 < indexRecords.length;
    }

    public static MobiFile parse(PdbFile pdbFile) throws InvalidHeaderException
    {
        if (!"BOOK".equals(pdbFile.header.type) ||
            !"MOBI".equals(pdbFile.header.creator))
        {
            throw new InvalidHeaderException();
        }

        MobiFile file = new MobiFile();
        file.pdbFile = pdbFile;

        MarshalBuilder mb = MarshalRoot.getInstance ();
        ByteBuffer buffer = ByteBuffer.wrap(pdbFile.getRecordData(0));

        file.palmHeader = mb.read(buffer, PalmDocHeader.class);
        file.mobiHeader = mb.read(buffer, MobiDocHeader.class);

        if (!MobiDocHeader.MOBI.equals(file.mobiHeader.identifier))
        {
            throw new InvalidHeaderException();
        }

        if (file.mobiHeader.hasExth())
        {
            file.exthHeader = mb.read(buffer, ExthHeader.class);
        }

        if (0 < file.mobiHeader.indxRecord)
        {
            int idx = file.mobiHeader.indxRecord;
            IndexReader reader = new IndexReader ();

            buffer = pdbFile.getRecordBuffer (idx);

            List<IndexRecord> records = new ArrayList<IndexRecord>();
            IndexRecord mainIndex = reader.parseIndexRecord (buffer);
            records.add (mainIndex);

            for (int i = idx + 1, end = idx + 1 + mainIndex.indxHead.indexCount;
                 i < end; ++i)
            {
                buffer = pdbFile.getRecordBuffer (i);
                records.add (reader.parseIndexRecord (buffer));
            }

            file.indexRecords = records.toArray (new IndexRecord[records.size ()]);

            if (0 < mainIndex.indxHead.cncxCount)
            {
                idx += mainIndex.indxHead.indexCount + mainIndex.indxHead.cncxCount;
                file.ncxRecord = new NcxRecord ();
                file.ncxRecord.parse (pdbFile.getRecordBuffer (idx));
            }

        }

        return file;
    }
}
