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

import little.nj.mobi.format.InvalidHeaderException;
import little.nj.mobi.format.MobiFile;
import little.nj.mobi.format.PdbFile;
import little.nj.mobi.model.MobiDocument;
import little.nj.util.FileUtil;
import little.nj.util.StringUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static java.lang.System.out;

public class Harness
{

    public static void main (String[] args)
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

        util.read(book, stream -> stream.read ( data ));

        ByteBuffer buffer = ByteBuffer.wrap(data);

        try
        {
            PdbFile pdbFile   = PdbFile.parse (buffer);
            MobiFile mobiFile = MobiFile.parse (pdbFile);

            MobiDocument doc  = MobiDocument.fromMobiFile (mobiFile);

            out.printf ("Title:     %s%n", doc.getTitle ());
            out.printf ("Author:    %s%n", doc.getAuthor ());
            out.printf ("ISBN:      %s%n", doc.getIsbn ());
            out.printf ("Publisher: %s%n", doc.getPublisher ());
            out.printf ("Published: %s%n", doc.getPublished ());
            out.printf ("Subjects:  %s%n", StringUtil.valueOf (doc.getSubjects ()));
            out.printf ("Length:    %,d%n", doc.getText ().length ());
            out.printf ("Index:     %s%n", StringUtil.valueOf (doc.getIndex ()));

            out.println ("Extracting Content...");

            final byte[] text = doc.getText ().getBytes ();

            try ( FileOutputStream stream = new FileOutputStream ( new File ( "text.txt" ) ) )
            {
                stream.write ( text );
            }

            BufferedImage[] images = doc.getImages ().toArray (new BufferedImage[0]);
            for (int i = 0, end = images.length; i < end; ++i)
            {
                String name = String.format ("image_%02d", i + 1);
                ImageIO.write (images [i], "png", new File(name));
            }
            out.println ();

            out.println ("Rewriting Content...");

            mobiFile = MobiDocument.toMobiFile ( doc );

            mobiFile.pdbFile.writeTo (new File ("output.mobi"));

            out.println ();

            Inspect.main (new String [] { "output.mobi"});
        }
        catch (InvalidHeaderException ex) { ex.printStackTrace (); }
        catch (IOException ex) { ex.printStackTrace (); }

    }

}
