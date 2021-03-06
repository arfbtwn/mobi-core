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

import little.nj.mobi.util.IndexUtil;

import java.nio.charset.Charset;
import java.util.*;

public class IndexRecord
{
    public IndxHeader indxHead;
    public TagxHeader tagxHead;

    public final Map < Integer, IdxtEntry > idxt = new TreeMap <> ();

    public static class IdxtEntry {

        public final String ident;
        public final byte[] bytes;

        public IdxtEntry (String ident, byte[] bytes)
        {
            this.ident = ident;
            this.bytes = bytes;
        }

        public NavigableMap < TagxTag, Long > decode (int controlBytes, TagxTag[] tags)
        {
            return IndexUtil.decode ( this, controlBytes, tags );
        }

        int length ()
        {
            return 1 + ident.getBytes (Charset.forName ("US-ASCII")).length + bytes.length;
        }
    }
}
