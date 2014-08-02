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

import little.nj.data.MarshalBuilder.*;

public class PdbHeader {

    public static final int LENGTH_NAME = 32;

    @Encoding
    @FixedLength(length = LENGTH_NAME)
    public String       name;
    public short        attributes;
    public short        version;
    public int          creationTime;
    public int          modificationTime;
    public int          backedUpTime;
    public int          modificationNumber;
    public int          appInfoID;
    public int          sortInfoID;

    @FixedLength(length = 4)
    public String       type;
    @FixedLength(length = 4)
    public String       creator;

    public int          uniqueSeedID;
    public int          nextRecordListID;

    @Counter(counter = 1)
    public short        recordCount;

    @Counted(counter = 1)
    public PdbRecord[] records;
}