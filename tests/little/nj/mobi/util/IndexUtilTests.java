/**
 * Copyright (C) 2015
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
package little.nj.mobi.util;

import little.nj.mobi.format.IndexRecord;
import little.nj.mobi.format.NcxRecord;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class IndexUtilTests
{
    private static byte[] indxRecord1;
    private static byte[] indxRecord2;
    private static byte[] ncxRecord;

    @BeforeClass
    public static void setUpBeforeClass () throws Exception
    {
        try (FileInputStream stream = new FileInputStream ( new File("179 - ffffb3.bin") ) )
        {
            indxRecord1 = new byte[stream.available ()];
            stream.read (indxRecord1);
        }

        try (FileInputStream stream = new FileInputStream ( new File("180 - ffffb4.bin") ) )
        {
            indxRecord2 = new byte[stream.available ()];
            stream.read (indxRecord2);
        }

        try (FileInputStream stream = new FileInputStream ( new File("181 - ffffb5.bin") ) )
        {
            ncxRecord = new byte[ stream.available () ];
            stream.read ( ncxRecord );
        }
    }

    @Test
    public void test ()
    {
        try
        {
            IndexRecord index1 = IndexUtil.parseIndexRecord ( ByteBuffer.wrap ( indxRecord1 ) ),
                        index2 = IndexUtil.parseIndexRecord ( ByteBuffer.wrap ( indxRecord2 ) );

            NcxRecord ncx = NcxRecord.parse ( ByteBuffer.wrap ( ncxRecord ) );

            assertNotNull ( index1 );
            assertNotNull ( index2 );
            assertNotNull ( ncx );
        }
        catch (Exception e)
        {
            fail ( e.getMessage () );
        }
    }

}
