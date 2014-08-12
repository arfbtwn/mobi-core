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
package little.nj.mobi.model;

import java.util.ArrayList;
import java.util.List;

import little.nj.mobi.format.PdbFile;
import little.nj.mobi.format.PdbHeader;
import little.nj.mobi.format.PdbRecord;

public class PdbBuilder
{
    String name;
    String type;
    String creator;

    final List<Integer> record_ids = new ArrayList<Integer> ();
    final List<byte[]> records = new ArrayList<byte[]> ();

    public PdbBuilder name (String name)
    {
        this.name = name;
        return this;
    }

    public PdbBuilder type (String type)
    {
        this.type = type;
        return this;
    }

    public PdbBuilder creator (String creator)
    {
        this.creator = creator;
        return this;
    }

    public PdbBuilder record (int id, byte[] record)
    {
        records.add (record);
        record_ids.add (id);
        return this;
    }

    public PdbBuilder record (byte[] record)
    {
        return record (records.size (), record);
    }

    public PdbFile build ()
    {
        PdbFile file = new PdbFile ();

        file.header = new PdbHeader ();
        file.header.name = name;
        file.header.type = type;
        file.header.creator = creator;
        file.header.recordCount = (short) records.size ();
        file.header.records = new PdbRecord [file.header.recordCount];
        file.records = records.toArray (new byte[file.header.recordCount][]);

        int start = PdbHeader.LENGTH + PdbRecord.LENGTH * records.size ();
        for (int i = 0, end = records.size (); i < end; ++i)
        {
            PdbRecord record = new PdbRecord ();
            record.offset = start;
            record.setId (record_ids.get (i));

            file.header.records [i] = record;

            start += file.records [i].length;
        }

        return file;
    }
}
