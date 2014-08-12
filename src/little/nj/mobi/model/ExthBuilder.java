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

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import little.nj.mobi.format.ExthHeader;
import little.nj.mobi.format.ExthRecord;
import little.nj.mobi.format.ExthUtil;

public class ExthBuilder extends ExthUtil
{
    final Map<Integer, Object> entries = new LinkedHashMap<Integer, Object> ();

    public ExthBuilder () { }

    public ExthBuilder (Charset charset) { super (charset); }

    public ExthBuilder put (int id, Object value)
    {
        entries.put (id, value);
        return this;
    }

    public ExthHeader build ()
    {
        ExthHeader header = new ExthHeader ();
        header.count = entries.size ();
        header.length = 12;
        header.records = new ExthRecord [header.count];

        int count = 0;
        for (Entry<Integer, Object> i : entries.entrySet ())
        {
            ExthRecord record = encode (i.getKey (), i.getValue ());
            header.length += record.length;
            header.records [count++] = record;
        }

        return header;
    }
}
