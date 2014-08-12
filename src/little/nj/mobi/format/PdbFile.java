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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import little.nj.data.MarshalBuilder;
import little.nj.data.MarshalRoot;
import little.nj.util.FileUtil;
import little.nj.util.StreamUtil.OutputAction;

public class PdbFile {

    public PdbHeader header;
    public byte[][] records;

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

    public ByteBuffer getRecordBuffer(int record)
    {
        return ByteBuffer.wrap(records [record]);
    }

    public void writeTo (File file)
    {
        FileUtil util = new FileUtil ();

        final byte[] data = new byte [length ()];

        ByteBuffer buffer = ByteBuffer.wrap (data);

        MarshalRoot.write (buffer, header);

        for (byte[] i : records)
        {
            buffer.put (i);
        }

        util.write (file, new OutputAction () {

            @Override
            public void act (OutputStream stream) throws IOException
            {
                stream.write (data);
            }
        });
    }

    public int length ()
    {
        int length = header.length ();

        for (byte[] i : records)
        {
            length += i.length;
        }

        return length;
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
