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

import static java.lang.System.out;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.xml.bind.DatatypeConverter;

import little.nj.data.MarshalBuilder;
import little.nj.mobi.format.ExthHeader;
import little.nj.mobi.format.ExthRecord;
import little.nj.mobi.format.ExthUtil;
import little.nj.mobi.format.IndxHeader;
import little.nj.mobi.format.InvalidHeaderException;
import little.nj.mobi.format.MobiDocHeader;
import little.nj.mobi.format.PalmDocHeader;
import little.nj.mobi.format.PdbHeader;
import little.nj.mobi.format.TagxHeader;
import little.nj.util.FileUtil;
import little.nj.util.StreamUtil.InputAction;

public class Inspect {

    public static void main(String [] args) throws InvalidHeaderException
    {
        File book;
        //book = new File(args[0]);
        book = new File("/home/nicholas/Documents/EBooks/Horus Heresy Books (1-15)/Abnett, Dan/Horus Rising/Horus Rising - Dan Abnett.mobi");
        //book = new File("/home/nicholas/Documents/EBooks/Horus Heresy/WH40K - The Horus Heresy 20 - Christian Dunn - The Primarchs.mobi");

        FileUtil util = new FileUtil();
        final byte[] data = new byte[(int)book.length()];

        util.read(book, new InputAction() {
            @Override
            public void act(InputStream stream) throws IOException {
                stream.read(data);
            }
        });

        ByteBuffer buffer = ByteBuffer.wrap(data);

        MarshalBuilder mb = new MarshalBuilder();

        PdbHeader bean = mb.read(buffer, PdbHeader.class);

        buffer.position(bean.records[0].offset);

        PalmDocHeader palmHead = null;
        MobiDocHeader mobiHead = null;
        ExthHeader    exthHead = null;
        IndxHeader    indxHead = null;
        TagxHeader    tagxHead = null;

        try
        {
            palmHead = mb.read(buffer, PalmDocHeader.class);
            mobiHead = mb.read(buffer, MobiDocHeader.class);

            if (mobiHead.hasExth())
            {
                exthHead = mb.read(buffer, ExthHeader.class);
            }

            if (0 < mobiHead.indxRecord)
            {
                int idx = mobiHead.indxRecord;
                int indxStart = bean.records[idx].offset;
                buffer.position(indxStart);

                indxHead = mb.read(buffer, IndxHeader.class);
                tagxHead = mb.read(buffer, TagxHeader.class);
            }
        }
        catch (Exception e) { e.printStackTrace(); }

        out.println("## PDB ##");
        out.println("Name:    " + bean.name);
        out.println("Type:    " + new String(bean.type));
        out.println("Creator: " + new String(bean.creator));
        out.println("Records: " + bean.recordCount);
        out.println();

        out.println("## Palm ##");
        out.println("Text Size:         " + palmHead.textRecordSize);
        out.println("Text Count:        " + palmHead.textRecordCount);
        out.println("Compression:       " + palmHead.compression);
        out.println("Uncompressed Size: " + palmHead.uncompressedTextLength);
        out.println("Current Position:  " + palmHead.currentPosition);
        out.println();

        out.println("## Mobi ##");
        out.println("Minimum Reader: " + mobiHead.minimumReaderVersion);
        out.println("Encoding:       " + mobiHead.encoding);
        out.println("Locale:         " + mobiHead.locale);
        out.println("Language in:    " + mobiHead.languageInput);
        out.println("Language out:   " + mobiHead.languageOutput);
        out.println("First Content:  " + mobiHead.firstContentRecord);
        out.println("First Non Book: " + mobiHead.firstNonBookRecord);
        out.println("First Huffman:  " + mobiHead.firstHuffmanRecord);
        out.println("First Image:    " + mobiHead.firstImageRecord);
        out.println("Last Content:   " + mobiHead.lastContentRecord);
        out.println("FDST:           " + mobiHead.fdstRecord);
        out.println("FLIS:           " + mobiHead.flisRecord);
        out.println("FCIS:           " + mobiHead.fcisRecord);
        out.println("SRCS:           " + mobiHead.srcsRecord);
        out.println("SRCS Count:     " + mobiHead.srcsCount);
        out.println("INDX:           " + mobiHead.indxRecord);
        out.println("Fragment:       " + mobiHead.fragmentRecord);
        out.println("Skeleton:       " + mobiHead.skeletonRecord);
        out.println("DATP:           " + mobiHead.datpRecord);
        out.println("Guide:          " + mobiHead.guideRecord);
        out.println();

        if (mobiHead.hasExth())
        {
            out.println("## EXTH ##");
            out.println("Length:  " + exthHead.length);
            out.println("Records: " + exthHead.count);
            out.println();

            ExthUtil exthUtil = new ExthUtil();

            for(ExthRecord i : exthHead.records)
            {
                String name = exthUtil.nameOf(i);
                Object value = exthUtil.decode(i);
                out.printf("%d => %s: %s%n", i.id, name, value.toString());
            }

            if (0 < exthHead.records.length)
            {
                out.println();
            }
        }

        if (null != indxHead)
        {
            out.println("## INDX ##");
            out.println("First Entry:       " + indxHead.firstEntryOffset);
            out.println("Type:              " + indxHead.indxType);
            out.println("IDXT Offset:       " + indxHead.idxtOffset);
            out.println("Index Entry Count: " + indxHead.indexEntryCount);
            out.println("Index Encoding:    " + indxHead.indexEncoding);
            out.println("Index Language:    " + indxHead.indexLanguage);
            out.println("Total Entry Count: " + indxHead.totalEntryCount);
            out.println("ORDT Offset:       " + indxHead.ordtOffset);
            out.println("LIGT Offset:       " + indxHead.ligtOffset);
            out.println("LIGT Count:        " + indxHead.ligtCount);
            out.println("CNCX Count:        " + indxHead.cncxCount);
            out.println("ORDT Count:        " + indxHead.ordtCount);
            out.println("ORDT Entries:      " + indxHead.ordtEntries);
            out.println("TAGX Offset:       " + indxHead.tagxOffset);
            out.println();

            out.println("## TAGX ##");
            out.println("Length:        " + tagxHead.length);
            out.println("Control Bytes: " + tagxHead.controlBytes);
            out.println("Tags:          " + tagxHead.tags.length + " => " + DatatypeConverter.printHexBinary(tagxHead.tags));
            out.println();
        }
    }
}
