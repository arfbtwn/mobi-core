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

public class RecordZero
{
    public PalmDocHeader palmHead;
    public MobiDocHeader mobiHead;
    public ExthHeader    exthHead;

    public String        bookName;

    public int size ()
    {
        int len = mobiHead.fullNameOffset + mobiHead.fullNameLength + 2,
            pad = len % 4;

        if (0 != pad)
        {
            len += 4 - pad;
        }

        return len;
    }
}
