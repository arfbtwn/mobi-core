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

import little.nj.exceptions.NotImplementedException;
import little.nj.mobi.codecs.Codec;
import little.nj.mobi.codecs.PalmDocCodec;
import little.nj.mobi.codecs.RawCodec;
import little.nj.mobi.format.*;
import little.nj.mobi.format.Enumerations.*;
import little.nj.mobi.format.IndexRecord.IdxtEntry;
import little.nj.mobi.util.ExthUtil;
import little.nj.mobi.util.IndexUtil;
import little.nj.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static little.nj.mobi.util.ExthUtil.*;

public class MobiDocument
{
    String author, title, blurb, text, publisher, isbn;

    Date published;

    BufferedImage cover, thumb;

    boolean inlineCover;

    Language language;

    Dialect dialect;

    final Set<String> subjects;

    final Set<BufferedImage> images;

    final Index index;

    public MobiDocument ()
    {
        images = new LinkedHashSet<BufferedImage> ();
        subjects = new HashSet<String> ();
        index  = new Index ();
    }

    public String getAuthor ()
    {
        return author;
    }

    public void setAuthor (String author)
    {
        this.author = author;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getBlurb ()
    {
        return blurb;
    }

    public void setBlurb (String blurb)
    {
        this.blurb = blurb;
    }

    public String getText ()
    {
        return text;
    }

    public void setText (String text)
    {
        this.text = text;
    }

    public Language getLanguage ()
    {
        return language;
    }

    public void setLanguage (Language language)
    {
        this.language = language;
    }

    public Dialect getDialect ()
    {
        return dialect;
    }

    public void setDialect (Dialect dialect)
    {
        this.dialect = dialect;
    }

    public String getPublisher ()
    {
        return publisher;
    }

    public void setPublisher (String publisher)
    {
        this.publisher = publisher;
    }

    public String getIsbn ()
    {
        return isbn;
    }

    public void setIsbn (String isbn)
    {
        this.isbn = isbn;
    }

    public Date getPublished ()
    {
        return published;
    }

    public void setPublished (Date published)
    {
        this.published = published;
    }

    public BufferedImage getCover ()
    {
        return cover;
    }

    public void setCover (BufferedImage cover)
    {
        this.cover = cover;
        images.add (cover);
    }

    public BufferedImage getThumb ()
    {
        return thumb;
    }

    public void setThumb (BufferedImage thumb)
    {
        this.thumb = thumb;
        images.add (thumb);
    }

    public Set<String> getSubjects ()
    {
        return subjects;
    }

    public Set<BufferedImage> getImages ()
    {
        return images;
    }

    public Index getIndex ()
    {
        return index;
    }

    public static MobiDocument fromMobiFile (MobiFile mobiFile)
    {
        MobiDocument document = new MobiDocument ();

        document.title = mobiFile.bookName;

        int cover = -1, thumb = -1;

        if (mobiFile.hasExth ())
        {
            ExthUtil exthDecoder = new ExthUtil (mobiFile.getEncoding ().getCharset ());
            for (ExthRecord i : mobiFile.exthHeader.records)
            {
                switch (i.id)
                {
                    case AUTHOR:
                        document.author = (String) exthDecoder.decode (i);
                        break;
                    case BLURB:
                        document.blurb = (String) exthDecoder.decode (i);
                        break;
                    case TITLE:
                    case UPDATED_TITLE:
                        document.title = (String) exthDecoder.decode (i);
                        break;
                    case ISBN:
                        document.isbn = (String) exthDecoder.decode (i);
                        break;
                    case PUBLISHER:
                        document.publisher = (String) exthDecoder.decode (i);
                        break;
                    case SUBJECT:
                        document.subjects.add ((String) exthDecoder.decode (i));
                        break;
                    case PUBLISHING_DATE:
                        try
                        {
                            DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                            document.published = fmt.parse ((String) exthDecoder.decode (i));
                        }
                        catch (ParseException ex)
                        {
                            ex.printStackTrace ();
                        }
                        break;
                    case COVER:
                        cover = (Integer) exthDecoder.decode (i);
                        break;
                    case THUMB:
                        thumb = (Integer) exthDecoder.decode (i);
                        break;
                    case HASFAKECOVER:
                        int fake = (Integer) exthDecoder.decode (i);
                        document.inlineCover = 1 == fake;
                        break;
                }
            }
        }

        if (mobiFile.hasIndex ())
        {
            IndexRecord main = mobiFile.mainIndex;
            TagxHeader tagx = main.tagxHead;
            TagxTag[] tags = IndexUtil.getTags (main.tagxHead);
            for (IndexRecord indx : mobiFile.indexRecords)
            {
                for (IdxtEntry idxt : indx.idxtEntries)
                {
                    NavigableMap<?, Long> map = idxt.decode (tagx.controlBytes, tags);

                    if (map.isEmpty ())
                    {
                        continue;
                    }

                    document.index.add (map.firstEntry ().getValue (), idxt.ident);
                }
            }

            if (null != mobiFile.ncxRecord)
            {
                for (NcxRecord ncxRecord : mobiFile.ncxRecord)
                {
                    List<String> entries = ncxRecord;

                    if (entries.size () != document.index.size ())
                    {
                        throw new IllegalArgumentException ();
                    }

                    for (int i = 0, end = entries.size (); i < end; ++i)
                    {
                        document.index.set (i, entries.get (i));
                    }
                }
            }
        }

        if (0 < mobiFile.palmHeader.textRecordCount)
        {
            Compression compression = mobiFile.getCompression ();

            Codec codec;
            switch (compression) {
                case HUFF_CDIC:
                    throw new NotImplementedException ();
                case PALMDOC:
                    codec = new PalmDocCodec();
                    break;
                case NONE:
                    codec = new RawCodec ();
                    break;
                default:
                    throw new RuntimeException();
            }

            Encoding encoding = mobiFile.getEncoding ();
            Charset charset = encoding.getCharset ();

            StringBuilder sb = new StringBuilder ();

            System.out.println ("Decoding Text Records...");
            for (TextRecord text : mobiFile.textRecords)
            {
                sb.append (new String (codec.decompress (text.recordBytes), charset));
            }

            document.text = sb.toString ();
        }

        if (0 < mobiFile.mobiHeader.firstImageRecord)
        {
            int idx = mobiFile.mobiHeader.firstImageRecord;

            for (int count = 1, end = mobiFile.mobiHeader.lastContentRecord; idx <= end; ++idx, ++count)
            {
                byte[] data = mobiFile.pdbFile.getRecordData (idx);
                BufferedImage image = ImageUtil.readImage (data);
                document.images.add (image);

                if (cover == count)
                {
                    document.cover = image;
                }

                if (thumb == count)
                {
                    document.thumb = image;
                }
            }
        }

        Result<Language> language = Enumerations.tryParse (mobiFile.mobiHeader.language, Language.class);
        Result<Dialect>  dialect  = Enumerations.tryParse (mobiFile.mobiHeader.dialect, Dialect.class);

        document.language = language.hasValue () ? language.getValue () : Language.NONE;

        document.dialect  = dialect.hasValue ()  ? dialect.getValue ()  : Dialect.NONE;

        return document;
    }

    public static MobiFile toMobiFile (MobiDocument mobiDocument)
    {
        return new MobiBuilder ()
            .document (mobiDocument)
            .exth  ()
            .build ();
    }
}
