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

import little.nj.data.MarshalBuilder;

public class PdbFile {

    PdbHeader header;
    byte[][] records;

    PdbFile() { }

    public PdbHeader getHeader()
    {
        return header;
    }

    public int size()
    {
        return records.length;
    }

    public PdbRecord getRecord(int record)
    {
        return record >= 0 && record < header.records.length
            ? header.records [record]
            : null;
    }

    public byte[] getRecordData(int record)
    {
        return record >= 0 && record < records.length
            ? records [record]
            : null;
    }

    public static PdbFile parse(ByteBuffer buffer)
    {
        PdbFile file = new PdbFile();

        file.header = new MarshalBuilder().read(buffer, PdbHeader.class);
        file.records = new byte[file.header.records.length][];

        int end = buffer.capacity();

        for (int i = file.records.length - 1; i >= 0; --i)
        {
            PdbRecord record = file.header.records[i];
            buffer.position (record.offset);

            file.records [i] = new byte[end - record.offset];
            buffer.get(file.records [i]);

            end = record.offset;
        }

        return file;
    }
}
