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

import java.nio.ByteBuffer;

import little.nj.data.MarshalBuilder;

public class MobiFile {

    PdbFile       pdbFile;

    PalmDocHeader palmHeader;
    MobiDocHeader mobiHeader;
    ExthHeader    exthHeader;
    IndxHeader    indxHeader;

    MobiFile() { }

    public PalmDocHeader getPalmHeader()
    {
        return palmHeader;
    }

    public MobiDocHeader getMobiHeader()
    {
        return mobiHeader;
    }

    public ExthHeader getExthHeader()
    {
        return exthHeader;
    }

    public IndxHeader getIndxHeader()
    {
        return indxHeader;
    }

    public static MobiFile parse(PdbFile pdbFile) throws InvalidHeaderException
    {
        if (!"BOOK".equals(pdbFile.header.type) ||
            !"MOBI".equals(pdbFile.header.creator))
        {
            throw new InvalidHeaderException();
        }

        MobiFile file = new MobiFile();

        MarshalBuilder mb = new MarshalBuilder();
        ByteBuffer buffer = ByteBuffer.wrap(pdbFile.getRecordData(0));

        file.palmHeader = mb.read(buffer, PalmDocHeader.class);
        file.mobiHeader = mb.read(buffer, MobiDocHeader.class);

        if (!MobiDocHeader.MOBI.equals(file.mobiHeader.identifier))
        {
            throw new InvalidHeaderException();
        }

        if (file.mobiHeader.hasExth())
        {
            file.exthHeader = mb.read(buffer, ExthHeader.class);
        }

        if (0 < file.mobiHeader.indxRecord)
        {
            buffer = ByteBuffer.wrap(pdbFile.getRecordData(file.mobiHeader.indxRecord));
            file.indxHeader = mb.read(buffer, IndxHeader.class);
        }

        file.pdbFile = pdbFile;

        return file;
    }
}
