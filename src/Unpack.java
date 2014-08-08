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

import little.nj.data.FieldMarshals.FieldMarshalException;
import little.nj.mobi.format.PdbFile;
import little.nj.mobi.format.PdbRecord;
import little.nj.util.FileUtil;
import little.nj.util.StreamUtil.InputAction;
import little.nj.util.StreamUtil.OutputAction;

public class Unpack {

    public static void main(String[] args)
    {
        File book;

        //book = new File("/home/nicholas/Documents/EBooks/Horus Heresy Books (1-15)/Abnett, Dan/Horus Rising/Horus Rising - Dan Abnett.mobi");
        //book = new File("/home/nicholas/Documents/EBooks/Horus Heresy/WH40K - The Horus Heresy 20 - Christian Dunn - The Primarchs.mobi");
        book = new File("/home/nicholas/Documents/EBooks/Kindle/Card, Orson Scott - Ender 01 - Ender's Game.mobi");

        if (0 < args.length)
        {
            book = new File(args[0]);
        }

        File dir = new File("book");

        if (!book.exists() || (!dir.exists() && !dir.mkdirs())) {
            return;
        }

        final byte[] data = new byte[(int) book.length()];

        FileUtil util = new FileUtil();

        util.read(book, new InputAction() {

            @Override
            public void act(InputStream stream) throws IOException {
                stream.read(data);
            } });

        ByteBuffer buffer = ByteBuffer.wrap(data);

        PdbFile pdbFile = null;

        try
        {
            out.println("Reading PDB...");
            pdbFile = PdbFile.parse(buffer);
        }
        catch (FieldMarshalException e)
        {
            e.printStackTrace();
            return;
        }

        out.printf("Extracting %d Records…%n", pdbFile.size());
        for (int i = 0, end = pdbFile.size(); i < end; ++i)
        {
            PdbRecord record = pdbFile.getRecord(i);

            int id = record.getId();
            final byte[] recordData = pdbFile.getRecordData(i);

            out.printf(
                "Extracting Record %03d - %06x (%,d bytes)%n",
                i, id, recordData.length
            );

            util.write(
                new File(dir, String.format("%03d - %06x.bin", i, id)),
                new OutputAction()
                {
                    @Override
                    public void act(OutputStream stream) throws IOException
                    {
                        stream.write(recordData);
                    }
                });
        }
    }

}
