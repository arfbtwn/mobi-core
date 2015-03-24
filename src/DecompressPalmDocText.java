import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import little.nj.mobi.codecs.PalmDocCodec;
import little.nj.util.FileUtil;
import little.nj.util.StreamUtil.InputAction;

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

public class DecompressPalmDocText
{
    public static void main (String[] args)
    {
        File file = new File(args[0]);

        file = new File("/home/nicholas/Source/Java/Personal/MobiCore/book/181 - ffffb5.bin");

        if (!file.canRead ())
        {
            return;
        }

        final byte[] data = new byte[(int) file.length()];

        out.printf ("Reading %d bytes…%n", data.length);

        FileUtil util = new FileUtil ();

        util.read (file, new InputAction()
        {
            @Override
            public void act (InputStream stream) throws IOException
            {
                stream.read (data);
            }
        });

        PalmDocCodec codec = new PalmDocCodec ();

        out.println ("Decompressing…");
        byte[] result = codec.decompress (data);

        out.println ("Result:");
        out.println (new String(result));
    }

}
