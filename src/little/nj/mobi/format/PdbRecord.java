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

public class PdbRecord implements Comparable<PdbRecord> {

    public int offset;
    public byte flags;
    public final byte[] id = new byte[3];

    public int getId() {
        return (id[0] << 16 | id[1] << 8 | id[2]) & 0xffffff;
    }

    public void setId(int i) {
        if (i >>> 24 != 0)
            throw new IllegalArgumentException("ID Out of Range: " + i);

        id[0] = (byte) ((i & 0xFF0000) >>> 16);
        id[1] = (byte) ((i & 0xFF00) >>> 8);
        id[2] = (byte) (i & 0xFF);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PdbRecord &&
                0 == compareTo((PdbRecord)obj);
    }

    @Override
    public int compareTo(PdbRecord o) {
        return offset - o.offset;
    }
}