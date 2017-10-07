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

import little.nj.mobi.format.PdbFile;
import little.nj.mobi.format.PdbRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static java.lang.System.out;

public class Unpack {

    public static void main( String [] args ) throws IOException
    {
        File book = null;

        if ( 0 < args.length )
        {
            book = new File ( args[0] );
        }

        if (null == book )
        {
            out.println ( "Usage: unpack <file>" );
            return;
        }

        File dir = new File ( "book" );

        if ( !book.exists () || ( !dir.exists () && !dir.mkdirs () ) ) {
            return;
        }

        final byte[] data = new byte[(int) book.length ()];

        try ( FileInputStream stream = new FileInputStream ( book ))
        {
            stream.read ( data );
        }

        ByteBuffer buffer = ByteBuffer.wrap ( data );

        PdbFile pdbFile = null;

        try
        {
            out.println ( "Reading PDB..." );
            pdbFile = PdbFile.parse ( buffer );
        }
        catch ( Exception e )
        {
            e.printStackTrace ();
            return;
        }

        out.printf( "Extracting %d Records…%n", pdbFile.size () );
        for ( int i = 0, end = pdbFile.size (); i < end; ++i )
        {
            PdbRecord record = pdbFile.getRecord ( i );

            int id = record.getId ();
            final byte[] recordData = pdbFile.getRecordData( i );

            out.printf(
                "Extracting Record %03d - %06x (%,d bytes)%n",
                i, id, recordData.length
            );

            File file = new File( dir, String.format( "%03d - %06x.bin", i, id ) );

            try ( FileOutputStream stream = new FileOutputStream ( file ) )
            {
                stream.write ( recordData );
            }
        }
    }

}
