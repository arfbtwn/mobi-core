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

import little.nj.data.MarshalBuilder.FixedLength;

public class IndxHeader {

    public static final String INDX = "INDX";

    @FixedLength(length=4)
    public final String identifier = INDX;
    public int          firstEntryOffset;

    public int          unknownInteger1;

    public int          indxType;

    public int          unknownInteger2;

    public int          idxtOffset;
    public int          indexCount;
    public int          indexEncoding;
    public short        indexDialect;
    public short        indexLanguage;
    public int          totalEntryCount;
    public int          ordtOffset;
    public int          ligtOffset;
    public int          ligtCount;
    public int          cncxCount;

    public final byte[] unknownBytes2 = new byte[108];

    public int          ordtCount;
    public int          ordtEntries;
    public int          ordt1Offset;
    public int          ordt2Offset;
    public int          tagxOffset;

    public final byte[] unknownBytes3 = new byte[8];
}
