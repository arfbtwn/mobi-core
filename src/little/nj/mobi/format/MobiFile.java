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

import little.nj.mobi.format.Enumerations.Compression;
import little.nj.mobi.format.Enumerations.Encoding;
import little.nj.mobi.util.IndexUtil;
import little.nj.mobi.types.MobiBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MobiFile {

    public PdbFile       pdbFile;

    public RecordZero    recordZero;

    public PalmDocHeader palmHeader;
    public MobiDocHeader mobiHeader;
    public ExthHeader    exthHeader;

    public String        bookName;

    public IndexRecord   mainIndex;
    public IndexRecord[] indexRecords;

    public NcxRecord[]   ncxRecord;

    public TextRecord[]  textRecords;

    public Encoding getEncoding ()
    {
        return Enumerations.convert ( mobiHeader.encoding, Encoding.class );
    }

    public Compression getCompression ()
    {
        return Enumerations.convert ( palmHeader.compression, Compression.class );
    }

    public boolean hasExth ()
    {
        return exthHeader != null && mobiHeader.hasExth ();
    }

    public boolean hasIndex ()
    {
        return null != mainIndex && 0 < mobiHeader.indxRecord;
    }

    public static MobiFile parse(PdbFile pdbFile) throws InvalidHeaderException, IOException
    {
        if (!"BOOK".equals(pdbFile.header.type) ||
            !"MOBI".equals(pdbFile.header.creator))
        {
            throw new InvalidHeaderException();
        }

        MobiFile file = new MobiFile();
        file.pdbFile = pdbFile;

        MobiBuffer marshal = MobiBuffer.Instance;

        ByteBuffer buffer = pdbFile.getRecordBuffer (0);

        file.recordZero = marshal.read ( RecordZero.class, buffer );

        file.palmHeader = file.recordZero.palmHead;
        file.mobiHeader = file.recordZero.mobiHead;

        if (file.mobiHeader.hasExth())
        {
            file.exthHeader = file.recordZero.exthHead;
        }

        file.bookName = file.recordZero.bookName;

        if (0 < file.mobiHeader.indxRecord)
        {
            int idx = file.mobiHeader.indxRecord;

            buffer = pdbFile.getRecordBuffer (idx);

            IndexRecord mainIndex = IndexUtil.parseIndexRecord (buffer);
            file.mainIndex = mainIndex;
            file.indexRecords = new IndexRecord[mainIndex.indxHead.indexCount];
            for (int count = 0, end = ++idx + mainIndex.indxHead.indexCount; idx < end; ++count, ++idx)
            {
                buffer = pdbFile.getRecordBuffer (idx);
                file.indexRecords[count] = IndexUtil.parseIndexRecord (buffer);
            }

            file.ncxRecord = new NcxRecord[mainIndex.indxHead.cncxCount];
            for (int count = 0, end = idx + mainIndex.indxHead.cncxCount; idx < end; ++count, ++idx)
            {
                file.ncxRecord[count] = NcxRecord.parse (pdbFile.getRecordBuffer (idx));
            }
        }

        file.textRecords = new TextRecord[file.palmHeader.textRecordCount];
        int flags = file.mobiHeader.extraRecordFlags;
        for (int idx = file.mobiHeader.firstContentRecord, end = idx + file.textRecords.length; idx < end; ++idx)
        {
            int record = idx - file.mobiHeader.firstContentRecord;
            byte[] data = file.pdbFile.getRecordData (idx);
            file.textRecords [record] = new TextRecord (flags, data);
        }

        return file;
    }
}
