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
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.xml.bind.DatatypeConverter;

import little.nj.data.MarshalBuilder;
import little.nj.mobi.format.ExthHeader;
import little.nj.mobi.format.ExthRecord;
import little.nj.mobi.format.ExthUtil;
import little.nj.mobi.format.IndxHeader;
import little.nj.mobi.format.MobiDocHeader;
import little.nj.mobi.format.PalmDocHeader;
import little.nj.mobi.format.PdbHeader;
import little.nj.mobi.format.TagxHeader;
import little.nj.util.FileUtil;
import little.nj.util.StreamUtil.InputAction;
import little.nj.util.StreamUtil.OutputAction;

public class Inspect {

    public static void main(String [] args)
    {
        if (0 == args.length)
        {
            return;
        }
        
        File book = new File(args[0]);
        
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
                buffer.position(indxStart + indxHead.tagxOffset);
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
            out.println("Index Count:       " + indxHead.entryCount);
            out.println("Index Encoding:    " + indxHead.indexEncoding);
            out.println("Index Language:    " + indxHead.indexLanguage);
            out.println("Index Entry Count: " + indxHead.totalEntryCount);
            out.println("ORDT Offset:       " + indxHead.ordtOffset);
            out.println("LIGT Offset:       " + indxHead.ligtOffset);
            out.println("LIGT Count:        " + indxHead.ligtCount);
            out.println("CNCX Count:        " + indxHead.cncxCount);
            out.println("O Entries:         " + indxHead.oentries);
            out.println("O Count:           " + indxHead.ocount);
            out.println("TAGX Offset:       " + indxHead.tagxOffset);
            out.println();
            
            out.println("## TAGX ##");
            out.println("Length:        " + tagxHead.length);
            out.println("Control Bytes: " + tagxHead.controlBytes);
            out.println("Tags:          " + tagxHead.tags.length + " => " + DatatypeConverter.printHexBinary(tagxHead.tags));
            out.println();
            
            
            int idxStart = bean.records[mobiHead.indxRecord].offset;
            int idxEnd   = bean.records[mobiHead.indxRecord + 1].offset;
            
            buffer.position(idxStart);
            final byte[] indx = new byte[idxEnd - idxStart];
            buffer.get(indx);
            util.write(new File("indx.bin"), new OutputAction() {
                @Override
                public void act(OutputStream stream) throws IOException {
                    stream.write(indx);
                } });
        }
        
        if (0 < mobiHead.srcsRecord)
        {
            out.println("Extracting SRCS Record");
            
            int srcsStart = bean.records[mobiHead.srcsRecord].offset;
            int srcsEnd   = bean.records[mobiHead.srcsRecord + 1].offset;
            
            final byte[] srcs   = new byte[srcsEnd - srcsStart];
            buffer.position(srcsStart);
            buffer.get(srcs);
            
            util.write(new File("srcs.zip"), new OutputAction() {

                @Override
                public void act(OutputStream stream) throws IOException {
                    stream.write(srcs);
                } });
        }
    }
}
