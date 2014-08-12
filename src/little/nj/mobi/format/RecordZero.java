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
import java.nio.charset.Charset;

import little.nj.data.MarshalBuilder.TypeMarshal;
import little.nj.data.MarshalRoot;
import little.nj.mobi.format.Enumerations.Encoding;

public class RecordZero
{
    public PalmDocHeader palmHead;
    public MobiDocHeader mobiHead;
    public ExthHeader    exthHead;

    public String        bookName;

    public int size ()
    {
        int offset = mobiHead.fullNameOffset + mobiHead.fullNameLength + 2,
            pad    = offset % 4;

        if (0 != pad)
        {
            offset += 4 - pad;
        }

        return offset;
    }

    public static final TypeMarshal MARSHAL = new TypeMarshal ()
    {
        @Override
        public Object read (ByteBuffer buffer)
        {
            RecordZero zero = new RecordZero ();

            zero.palmHead = MarshalRoot.read (buffer, PalmDocHeader.class);
            zero.mobiHead = MarshalRoot.read (buffer, MobiDocHeader.class);

            if (!MobiDocHeader.MOBI.equals(zero.mobiHead.identifier))
            {
                // TODO: Replace
                throw new RuntimeException ();
            }

            if (zero.mobiHead.hasExth ())
            {
                zero.exthHead = MarshalRoot.read (buffer, ExthHeader.class);
            }

            byte[] name = new byte [zero.mobiHead.fullNameLength];
            buffer.position (zero.mobiHead.fullNameOffset);
            buffer.get (name);

            Charset set = Enumerations.parse (zero.mobiHead.encoding, Encoding.class)
                .getCharset ();

            zero.bookName = new String (name, set);

            return zero;
        }

        @Override
        public void write (ByteBuffer buffer, Object struct)
        {
            RecordZero zero = (RecordZero) struct;

            int start = buffer.position ();

            MarshalRoot.write (buffer, zero.palmHead);
            MarshalRoot.write (buffer, zero.mobiHead);

            if (zero.mobiHead.hasExth ())
            {
                MarshalRoot.write (buffer, zero.exthHead);
            }

            Charset set = Enumerations.parse (zero.mobiHead.encoding, Encoding.class)
                .getCharset ();

            byte[] name = zero.bookName.getBytes (set);

            MarshalRoot.write (buffer, name);

            buffer.putShort ((short) 0);

            int pad = (buffer.position () - start) % 4;

            if (0 != pad)
            {
                byte[] padding = new byte [4 - pad];
                buffer.put (padding);
            }
        }

    };
}
