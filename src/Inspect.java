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

import little.nj.mobi.format.Enumerations;
import little.nj.mobi.format.Enumerations.Compression;
import little.nj.mobi.format.Enumerations.Dialect;
import little.nj.mobi.format.Enumerations.Encoding;
import little.nj.mobi.format.Enumerations.Language;
import little.nj.mobi.format.Enumerations.MobiType;
import little.nj.mobi.format.ExthHeader;
import little.nj.mobi.format.ExthRecord;
import little.nj.mobi.format.ExthUtil;
import little.nj.mobi.format.IndexRecord;
import little.nj.mobi.format.IndexRecord.IdxtEntry;
import little.nj.mobi.format.IndxHeader;
import little.nj.mobi.format.InvalidHeaderException;
import little.nj.mobi.format.MobiDocHeader;
import little.nj.mobi.format.MobiFile;
import little.nj.mobi.format.PalmDocHeader;
import little.nj.mobi.format.PdbFile;
import little.nj.mobi.format.PdbHeader;
import little.nj.mobi.format.TagxHeader;
import little.nj.mobi.format.TagxTag;
import little.nj.util.FileUtil;
import little.nj.util.StreamUtil.InputAction;
import little.nj.util.StringUtil;

public class Inspect {

    public static void main(String [] args) throws InvalidHeaderException
    {
        File book;

        //book = new File("/home/nicholas/Documents/EBooks/Horus Heresy Books (1-15)/Abnett, Dan/Horus Rising/Horus Rising - Dan Abnett.mobi");
        //book = new File("/home/nicholas/Documents/EBooks/Horus Heresy/WH40K - The Horus Heresy 20 - Christian Dunn - The Primarchs.mobi");
        book = new File("/home/nicholas/Documents/EBooks/Kindle/Card, Orson Scott - Ender 01 - Ender's Game.mobi");

        if (0 < args.length)
        {
            book = new File (args[0]);
        }

        if (!book.canRead ())
        {
            return;
        }

        FileUtil util = new FileUtil();
        final byte[] data = new byte[(int)book.length()];

        util.read(book, new InputAction() {
            @Override
            public void act(InputStream stream) throws IOException {
                stream.read(data);
            }
        });

        ByteBuffer buffer = ByteBuffer.wrap(data);

        PdbFile       pdbFile = null;
        PdbHeader     pdbHead = null;

        MobiFile      mobiFile = null;
        PalmDocHeader palmHead = null;
        MobiDocHeader mobiHead = null;
        ExthHeader    exthHead = null;
        IndxHeader    indxHead = null;
        TagxHeader    tagxHead = null;

        try
        {
            pdbFile  = PdbFile.parse(buffer);
            mobiFile = MobiFile.parse (pdbFile);

            pdbHead  = pdbFile.header;
            palmHead = mobiFile.palmHeader;
            mobiHead = mobiFile.mobiHeader;
            exthHead = mobiFile.exthHeader;

            if (mobiFile.hasIndex ())
            {
                indxHead = mobiFile.indexRecords [0].indxHead;
                tagxHead = mobiFile.indexRecords [0].tagxHead;
            }
        }
        catch (Exception e) { e.printStackTrace(); return; }

        out.println("## PDB ##");
        out.println("Name:    " + pdbHead.name);
        out.println("Type:    " + new String(pdbHead.type));
        out.println("Creator: " + new String(pdbHead.creator));
        out.println("Records: " + pdbHead.recordCount);
        out.println();

        out.println("## Palm ##");
        out.println("Text Size:         " + palmHead.textRecordSize);
        out.println("Text Count:        " + palmHead.textRecordCount);
        out.println("Compression:       " + Enumerations.tryParse (palmHead.compression, Compression.class));
        out.println("Uncompressed Size: " + palmHead.uncompressedTextLength);
        out.println("Current Position:  " + palmHead.currentPosition);
        out.println();

        out.println("## Mobi ##");
        out.println("Document Type:  " + Enumerations.tryParse (mobiHead.type, MobiType.class));
        out.println("Minimum Reader: " + mobiHead.minimumReaderVersion);
        out.println("Encoding:       " + Enumerations.tryParse (mobiHead.encoding, Encoding.class));
        out.println("Language:       " + Enumerations.tryParse (mobiHead.language, Language.class));
        out.println("Dialect:        " + Enumerations.tryParse (mobiHead.dialect, Dialect.class));
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
            out.println("Index Count:       " + indxHead.indexCount);
            out.println("Index Encoding:    " + Enumerations.tryParse (indxHead.indexEncoding, Encoding.class));
            out.println("Index Language:    " + Enumerations.tryParse (indxHead.indexLanguage, Language.class));
            out.println("Index Dialect:     " + Enumerations.tryParse (indxHead.indexDialect, Dialect.class));
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

            TagxTag[] tags = tagxHead.getTags ();

            for (IndexRecord index : mobiFile.indexRecords)
            {
                for (IdxtEntry idxt : index.idxtEntries)
                {
                    String decoded = StringUtil.valueOf (idxt.decode (tagxHead.controlBytes, tags));
                    out.println (idxt.ident + ": " + decoded);
                }
            }

            out.println ();
        }

        if (null != mobiFile.ncxRecord)
        {
            out.println ("## NCX ##");

            for (String entry : mobiFile.ncxRecord.getEntries ())
            {
                out.println (entry);
            }

            out.println ();
        }
    }
}
