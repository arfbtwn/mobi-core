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

import little.nj.mobi.model.Index.IndexEntry;

import java.util.ArrayList;

public class Index extends ArrayList < IndexEntry >
{
    public int add (Long offset, String ident)
    {
        add (new IndexEntry (offset, ident));
        return size ();
    }

    public int add (Long offset, String ident, String text)
    {
        add (new IndexEntry (offset, ident, text));
        return size ();
    }

    public void set (int entry, String text)
    {
        get (entry).text = text;
    }

    public class IndexEntry
    {
        IndexEntry (Long offset, String ident, String text)
        {
            this.offset = offset;
            this.ident  = ident;
            this.text   = text;
        }

        IndexEntry (Long offset, String ident)
        {
            this (offset, ident, null);
        }

        public final Long   offset;
        public final String ident;
        public       String text;

        @Override
        public String toString ()
        {
            return String.format ("%d => (%s, %s)", offset, ident, text);
        }
    }

    public IndexEntry last ()
    {
        return get (size () - 1);
    }
}
