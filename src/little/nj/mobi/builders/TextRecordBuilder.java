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
package little.nj.mobi.builders;

import little.nj.mobi.codecs.Codec;
import little.nj.mobi.codecs.RawCodec;
import little.nj.mobi.format.Enumerations.Encoding;
import little.nj.mobi.format.TextRecord;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TextRecordBuilder
{
    int size = 4096;
    int flags = 0;
    Codec codec = new RawCodec ();
    Charset charset = Encoding.UTF8.getCharset ();
    String text;

    public TextRecordBuilder size (int size)
    {
        this.size = size;
        return this;
    }

    public TextRecordBuilder flags (int flags)
    {
        this.flags = flags;
        return this;
    }

    public TextRecordBuilder text (String text)
    {
        this.text = text;
        return this;
    }

    public TextRecordBuilder codec (Codec codec)
    {
        this.codec = codec;
        return this;
    }

    public TextRecordBuilder charset (Charset charset)
    {
        this.charset = charset;
        return this;
    }

    public TextRecord[] build ()
    {
        List<TextRecord> records = new ArrayList<TextRecord> ();

        ByteBuffer buffer = ByteBuffer.wrap (text.getBytes (charset));

        while (buffer.hasRemaining ())
        {
            int length = buffer.remaining () < size
                ? buffer.remaining ()
                : size;

            byte[] record = new byte [length];

            buffer.get (record);

            records.add (new TextRecord (codec.compress (record)));
        }

        return records.toArray (new TextRecord [records.size ()]);
    }
}
