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

@Counted(counter=0)
public class MobiDocHeader {

    public static final String MOBI = "MOBI";

    @Constant
    @FixedLength(length=4)
    public String     identifier;

    @Counter(counter=0)
    public int              length;
    public int              type;
    public int              encoding;
    public int              uniqueID;
    public int              version;
    public int              orthographicIndex;
    public int              inflexionIndex;
    public int              indexNames;
    public int              indexKeys;
    public int              indexExtra1;
    public int              indexExtra2;
    public int              indexExtra3;
    public int              indexExtra4;
    public int              indexExtra5;
    public int              indexExtra6;
    public int              firstNonBookRecord;
    public int              fullNameOffset;
    public int              fullNameLength;
    public int              locale;
    public int              languageInput;
    public int              languageOutput;
    public int              minimumReaderVersion;
    public int              firstImageRecord;
    public int              firstHuffmanRecord;
    public int              huffmanRecordCount;
    public int              huffmanTableOffset;
    public int              huffmanTableLength;
    public int              exthFlags;
    public final byte[]     unknownBytes1 = new byte[32];
    public int              unknownInteger1;
    public int              drmOffset;
    public int              drmCount;
    public int              drmSize;
    public int              drmFlags;
    public final byte[]     unknownBytes2 = new byte[8];
    public short            firstContentRecord;
    public short            lastContentRecord;
    public int              fdstRecord;
    public int              fcisRecord;
    public int              fcisCount;
    public int              flisRecord;
    public int              flisCount;
    public final byte[]     unknownBytes3 = new byte[8];
    public int              srcsRecord;
    public int              srcsCount;
    public int              unknownInteger5;
    public int              unknownInteger6;
    public int              extraRecordFlags;
    public int              indxRecord;
    public int              unknownInteger7;
    public int              fragmentRecord;
    public int              unknownInteger9;
    public int              skeletonRecord;
    public int              datpRecord;
    public int              guideRecord;

    public boolean hasExth() { return 0x40 == (exthFlags & 0x40); }
}