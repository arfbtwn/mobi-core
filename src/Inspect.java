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

import little.nj.mobi.format.*;
import little.nj.mobi.format.Enumerations.*;
import little.nj.mobi.format.IndexRecord.IdxtEntry;
import little.nj.mobi.util.ExthUtil;
import little.nj.mobi.util.IndexUtil;
import little.nj.util.StringUtil;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static java.lang.System.out;

public class Inspect {

    public static void main( String [] args ) throws IOException, InvalidHeaderException
    {
        File book;

        //book = new File("/home/nicholas/Documents/EBooks/Horus Heresy Books (1-15)/Abnett, Dan/Horus Rising/Horus Rising - Dan Abnett.mobi");
        //book = new File("/home/nicholas/Documents/EBooks/Horus Heresy/WH40K - The Horus Heresy 20 - Christian Dunn - The Primarchs.mobi");
        book = new File("/home/nicholas/Documents/EBooks/Kindle/Card, Orson Scott - Ender 01 - Ender's Game.mobi");
        //book = new File ("output.mobi");

        if (0 < args.length)
        {
            book = new File (args[0]);
        }

        if (!book.canRead ())
        {
            return;
        }

        final byte[] data = new byte[(int)book.length()];

        try ( FileInputStream stream = new FileInputStream ( book ) )
        {
            stream.read ( data );
        }

        ByteBuffer buffer = ByteBuffer.wrap ( data );

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
            pdbFile  = PdbFile.parse ( buffer );
            mobiFile = MobiFile.parse ( pdbFile );

            pdbHead  = pdbFile.header;
            palmHead = mobiFile.palmHeader;
            mobiHead = mobiFile.mobiHeader;
            exthHead = mobiFile.exthHeader;

            if (mobiFile.hasIndex ())
            {
                indxHead = mobiFile.mainIndex.indxHead;
                tagxHead = mobiFile.mainIndex.tagxHead;
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
        out.println("Header Length:  " + mobiHead.length);
        out.println("Header Version: " + mobiHead.version);
        out.println("Minimum Reader: " + mobiHead.minimumReaderVersion);
        out.println("Unique ID:      " + mobiHead.uniqueID);
        out.println();
        out.println("Orth Index:     " + mobiHead.orthographicIndex);
        out.println("Infl'n Index:   " + mobiHead.inflexionIndex);
        out.println("Index Names:    " + mobiHead.indexNames);
        out.println("Index Keys:     " + mobiHead.indexKeys);
        out.println("Index Extra 1:  " + mobiHead.indexExtra1);
        out.println("Index Extra 2:  " + mobiHead.indexExtra2);
        out.println("Index Extra 3:  " + mobiHead.indexExtra3);
        out.println("Index Extra 4:  " + mobiHead.indexExtra4);
        out.println("Index Extra 5:  " + mobiHead.indexExtra5);
        out.println("Index Extra 6:  " + mobiHead.indexExtra6);
        out.println();
        out.println("Document Type:  " + Enumerations.tryParse (mobiHead.type, MobiType.class));
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

        out.println ( "Book Name: " + mobiFile.bookName );
        out.println ();

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

            TagxTag[] tags = IndexUtil.getTags (tagxHead);

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

        if ( null != mobiFile.ncxRecord )
        {
            out.println ( "## NCX ##" );

            for ( NcxRecord ncxRecord : mobiFile.ncxRecord )
            {
                for ( String entry : ncxRecord )
                {
                    out.println ( entry );
                }
            }
            out.println ();
        }
    }
}
