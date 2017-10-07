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
import little.nj.mobi.util.ConsoleUtil;
import little.nj.mobi.util.IndexUtil;

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

        ConsoleUtil.printPdbHead  ( pdbHead );
        ConsoleUtil.printPalmHead ( palmHead );
        ConsoleUtil.printMobiHead ( mobiHead );

        if (mobiHead.hasExth())
        {
            ConsoleUtil.printExthHead ( exthHead );
        }

        out.println ( "Book Name: " + mobiFile.bookName );
        out.println ();

        ConsoleUtil.printIndx ( indxHead );
        ConsoleUtil.printTagx ( tagxHead );

        if ( null != indxHead && null != tagxHead )
        {
            TagxTag[] tags = IndexUtil.getTags (tagxHead);

            ConsoleUtil.printIdxt ( mobiFile.mainIndex.idxt, tagxHead.controlBytes, tags );

            for ( IndexRecord index : mobiFile.indexRecords )
            {
                ConsoleUtil.printIndx ( index.indxHead );
                ConsoleUtil.printTagx ( index.tagxHead );

                ConsoleUtil.printIdxt ( index.idxt, tagxHead.controlBytes, tags );
            }

            out.println ();
        }

        ConsoleUtil.printNcx ( mobiFile.ncxRecord );
    }
}
