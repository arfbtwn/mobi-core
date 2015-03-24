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
package little.nj.mobi.model;

import little.nj.mobi.codecs.Codec;
import little.nj.mobi.codecs.HuffCdicCodec;
import little.nj.mobi.codecs.PalmDocCodec;
import little.nj.mobi.codecs.RawCodec;
import little.nj.mobi.format.Enumerations.Compression;
import little.nj.mobi.format.Enumerations.Encoding;
import little.nj.mobi.format.Enumerations.MobiType;
import little.nj.mobi.format.*;
import little.nj.mobi.types.MobiBuffer;
import little.nj.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import static little.nj.mobi.util.ExthUtil.*;

public class MobiBuilder
{
    MobiDocument  document;

    Encoding      encoding = Encoding.UTF8;

    Compression   compression = Compression.PALMDOC;

    MobiType      type = MobiType.MOBI_BOOK;

    int minimumReaderVersion = 6,
        mobiHeaderVersion = 6,
        textRecordSize = 4096;

    boolean exth, index, fxis, srcs;

    public MobiBuilder document (MobiDocument document)
    {
        this.document = document;
        return this;
    }

    public MobiBuilder encoding (Encoding encoding)
    {
        this.encoding = encoding;
        return this;
    }

    public MobiBuilder compression (Compression compression)
    {
        this.compression = compression;
        return this;
    }

    public MobiBuilder type (MobiType type)
    {
        this.type = type;
        return this;
    }

    public MobiBuilder textRecordSize (short size)
    {
        this.textRecordSize = size;
        return this;
    }

    public MobiBuilder minimumReaderVersion (int version)
    {
        this.minimumReaderVersion = version;
        return this;
    }

    public MobiBuilder mobiHeaderVersion (int version)
    {
        this.mobiHeaderVersion = version;
        return this;
    }

    public MobiBuilder exth () { return exth ( true ); }

    public MobiBuilder exth ( boolean present )
    {
        exth = present;
        return this;
    }

    public MobiBuilder index ()
    {
        index = true;
        return this;
    }

    public MobiBuilder fxis ()
    {
        fxis = true;
        return this;
    }

    public MobiBuilder srcs ()
    {
        srcs = true;
        return this;
    }

    protected Codec getCodec (Compression compression)
    {
        switch (compression)
        {
            default:
            case NONE:
                return new RawCodec ();
            case PALMDOC:
                return new PalmDocCodec ();
            case HUFF_CDIC:
                return new HuffCdicCodec ();
        }
    }

    public MobiFile build ()
    {
        Charset set = encoding.getCharset ();

        byte[] textBytes = document.text.getBytes (set);

        Codec codec = getCodec (compression);

        PdbBuilder pdbBuilder = new PdbBuilder ()
            .name    (document.title)
            .type    ("BOOK")
            .creator ("MOBI");

        // Compress and split text

        TextRecord[] textRecords = new TextRecordBuilder ()
            .charset (set)
            .codec   (codec)
            .flags   (0)
            .size    (textRecordSize)
            .text    (document.getText ())
            .build   ();

        short countText = (short) textRecords.length;

        // Gather images

        BufferedImage[] images = document.images.toArray (new BufferedImage[0]);
        short imageCover = (short) Arrays.asList (images).indexOf (document.cover);
        short imageThumb = (short) Arrays.asList (images).indexOf (document.thumb);

        short countImage = (short) images.length;

        // Create index

        short countIndex = 0;

        IndexRecord mainIndex = null;
        IndexRecord[] indices = null;
        NcxRecord   ncxRecord = null;

        if (index)
        {
            IndexBuilder indexBuilder = new IndexBuilder ()
                .index (document.getIndex ())
                .build ();

            mainIndex = indexBuilder.mainIndex;
            indices = indexBuilder.records;
            ncxRecord = indexBuilder.ncxRecord;

            countIndex = (short) (2 + indices.length);
        }

        // TODO: Build fcis/flis/srcs records

        // Build record zero

        PalmDocHeader palmHead          = new PalmDocHeader ();
        palmHead.textRecordCount        = (short) countText;
        palmHead.textRecordSize         = (short) textRecordSize;
        palmHead.compression            = compression.getValue ();
        palmHead.uncompressedTextLength = textBytes.length;

        MobiDocHeader mobiHead = new MobiDocHeader ();
        ExthHeader    exthHead = null;

        mobiHead.identifier = MobiDocHeader.MOBI;

        // FIXME: Stepped according to Mobi header version?
        mobiHead.length = 232;
        mobiHead.type = type.getValue ();
        mobiHead.version = mobiHeaderVersion;

        mobiHead.orthographicIndex = -1;
        mobiHead.inflexionIndex = -1;
        mobiHead.indexKeys = -1;
        mobiHead.indexNames = -1;
        mobiHead.indexExtra1 = -1;
        mobiHead.indexExtra2 = -1;
        mobiHead.indexExtra3 = -1;
        mobiHead.indexExtra4 = -1;
        mobiHead.indexExtra5 = -1;
        mobiHead.indexExtra6 = -1;
        mobiHead.unknownInteger1 = -1;
        mobiHead.drmOffset = -1;
        mobiHead.drmCount = -1;

        mobiHead.encoding = encoding.getValue ();
        mobiHead.language = document.language.getValue ();
        mobiHead.dialect = document.dialect.getValue ();
        mobiHead.minimumReaderVersion = minimumReaderVersion;
        mobiHead.fullNameLength = document.title.getBytes (set).length;
        mobiHead.fullNameOffset = PalmDocHeader.LENGTH + mobiHead.length;

        if (exth)
        {
            ExthBuilder exthBuilder = new ExthBuilder (set)
                .put (AUTHOR, document.author)
                .put (TITLE, document.title)
                .put (ISBN, document.isbn)
                .put (PUBLISHER, "MobiBuilder")
                .put (PUBLISHING_DATE, document.published)
                .put (BLURB, document.blurb)
                .put (COVER, imageCover + 1)
                .put (THUMB, imageThumb + 1)
                .put (HASFAKECOVER, document.inlineCover ? 1 : 0);

            for (String i : document.subjects)
            {
                exthBuilder.add (SUBJECT, i);
            }

            exthHead = exthBuilder.build ();
            mobiHead.fullNameOffset += exthHead.length;
            mobiHead.setExth (true);
        }

        short idx = 1;

        mobiHead.firstContentRecord = idx;
        mobiHead.firstNonBookRecord = idx += countText;

        mobiHead.indxRecord         = 0 < countIndex ? idx : -1;
        idx += countIndex;

        mobiHead.firstImageRecord   = 0 < countImage ? idx : -1;
        idx += countImage;

        mobiHead.lastContentRecord  = (short) (idx - 1);

        if (fxis)
        {
            mobiHead.flisCount = 1;
            mobiHead.flisRecord = idx++;

            mobiHead.fcisCount = 1;
            mobiHead.fcisRecord = idx++;
        }
        else
        {
            mobiHead.flisCount = 0;
            mobiHead.flisRecord = -1;

            mobiHead.fcisCount = 0;
            mobiHead.fcisRecord = -1;
        }

        if (srcs)
        {
            mobiHead.srcsCount = 1;
            mobiHead.srcsRecord = idx++;
        }
        else
        {
            mobiHead.srcsCount = 0;
            mobiHead.srcsRecord = -1;
        }

        RecordZero recordZero = new RecordZero ();
        recordZero.palmHead = palmHead;
        recordZero.mobiHead = mobiHead;
        recordZero.exthHead = exthHead;
        recordZero.bookName = document.title;

        byte[] record = new byte[recordZero.size ()];
        ByteBuffer buffer = ByteBuffer.wrap ( record );

        try
        {
            MobiBuffer.Instance.write ( recordZero, buffer );
        }
        catch ( IOException ex )
        {
            throw new RuntimeException ( ex );
        }

        pdbBuilder.record ( record );

        for ( TextRecord i : textRecords )
        {
            pdbBuilder.record ( i.toByteArray () );
        }

        for (BufferedImage i : images)
        {
            pdbBuilder.record ( ImageUtil.writeImage (i, "jpg") );
        }

        if (index)
        {
            try
            {
                MobiBuffer.Instance.write ( mainIndex, buffer );
            }
            catch (IOException ex)
            {
                throw new RuntimeException ( ex );
            }

            pdbBuilder.record ( record );

            for ( IndexRecord i : indices )
            {
                try
                {
                    MobiBuffer.Instance.write ( i, buffer );
                }
                catch ( IOException ex )
                {
                    throw new RuntimeException ( ex );
                }

                pdbBuilder.record ( record );
            }

            pdbBuilder.record ( NcxRecord.toByteArray ( ncxRecord ) );
        }

        PdbFile pdbFile       = pdbBuilder.build ();
        MobiFile mobiFile     = new MobiFile ();
        mobiFile.pdbFile      = pdbFile;
        mobiFile.recordZero   = recordZero;
        mobiFile.exthHeader   = recordZero.exthHead;
        mobiFile.palmHeader   = recordZero.palmHead;
        mobiFile.mobiHeader   = recordZero.mobiHead;
        mobiFile.bookName     = recordZero.bookName;

        mobiFile.mainIndex    = mainIndex;
        mobiFile.indexRecords = indices;
        mobiFile.ncxRecord    = new NcxRecord [] { ncxRecord };

        mobiFile.textRecords = textRecords;

        return mobiFile;
    }
}
