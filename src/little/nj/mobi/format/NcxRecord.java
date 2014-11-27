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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import little.nj.util.StringUtil;

public class NcxRecord
{
    final List<String> entries;

    public NcxRecord()
    {
        this.entries = new ArrayList<String> ();
    }

    public void addEntry (String entry)
    {
        entries.add (entry);
    }

    public List<String> getEntries ()
    {
        return Collections.unmodifiableList (entries);
    }

    public void parse (ByteBuffer record)
    {
        while (record.hasRemaining ())
        {
            long length = VariableWidthInteger.parseForward (record);
            byte[] entry = new byte [(int) length];
            record.get (entry);
            entries.add (new String (entry));
        }
    }

    public byte[] toByteArray ()
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream ();

        try
        {
            for (String i : entries)
            {
                byte[] bytes = i.getBytes ();
                bos.write (VariableWidthInteger.encodeForward (bytes.length));
                bos.write (bytes);
            }
        }
        catch (IOException e) { }

        return bos.toByteArray ();
    }

    @Override
    public String toString ()
    {
        return StringUtil.valueOf (entries);
    }
}
