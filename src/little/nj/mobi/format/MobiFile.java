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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import little.nj.data.MarshalBuilder;
import little.nj.data.MarshalRoot;
import little.nj.mobi.format.Enumerations.Compression;
import little.nj.mobi.format.Enumerations.Encoding;

public class MobiFile {

    public PdbFile       pdbFile;

    public PalmDocHeader palmHeader;
    public MobiDocHeader mobiHeader;
    public ExthHeader    exthHeader;

    public String        bookName;

    public IndexRecord   mainIndex;
    public IndexRecord[] indexRecords;

    public NcxRecord     ncxRecord;

    public TextRecord[]  textRecords;

    public Encoding getEncoding ()
    {
        return Enumerations.parse (mobiHeader.encoding, Encoding.class);
    }

    public Compression getCompression ()
    {
        return Enumerations.parse (palmHeader.compression, Compression.class);
    }

    public boolean hasExth ()
    {
        return exthHeader != null && mobiHeader.hasExth ();
    }

    public boolean hasIndex ()
    {
        return null != mainIndex && 0 < mobiHeader.indxRecord;
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
        ByteBuffer buffer = pdbFile.getRecordBuffer (0);

        file.palmHeader = mb.read(buffer, PalmDocHeader.class);
        file.mobiHeader = mb.read(buffer, MobiDocHeader.class);

        if (!MobiDocHeader.MOBI.equals(file.mobiHeader.identifier))
        {
            throw new InvalidHeaderException();
        }

        Charset charset = Enumerations.parse (file.mobiHeader.encoding, Encoding.class)
            .getCharset ();

        if (file.mobiHeader.hasExth())
        {
            file.exthHeader = mb.read(buffer, ExthHeader.class);
        }

        byte[] name = new byte [file.mobiHeader.fullNameLength];
        buffer.position (file.mobiHeader.fullNameOffset);
        buffer.get (name);

        file.bookName = new String (name, charset);

        if (0 < file.mobiHeader.indxRecord)
        {
            int idx = file.mobiHeader.indxRecord;
            IndexReader reader = new IndexReader ();
            List<IndexRecord> records = new ArrayList<IndexRecord>();

            buffer = pdbFile.getRecordBuffer (idx);

            IndexRecord mainIndex = reader.parseIndexRecord (buffer);

            ++idx;
            for (int end = idx + mainIndex.indxHead.indexCount; idx < end; ++idx)
            {
                buffer = pdbFile.getRecordBuffer (idx);
                records.add (reader.parseIndexRecord (buffer));
            }

            file.mainIndex = mainIndex;
            file.indexRecords = records.toArray (new IndexRecord[records.size ()]);

            for (int end = idx + mainIndex.indxHead.cncxCount; idx < end; ++idx)
            {
                file.ncxRecord = new NcxRecord ();
                file.ncxRecord.parse (pdbFile.getRecordBuffer (idx));
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
