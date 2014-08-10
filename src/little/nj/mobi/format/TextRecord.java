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

import java.util.Arrays;

public class TextRecord
{
    public final byte[] recordBytes;
    public final byte[] trailingEntries;

    TextRecord (int flags, byte[] input)
    {
        int textEnd = input.length - 1;

        for (int i = Integer.highestOneBit (flags); 1 < i; i >>= 1)
        {
            if (i == (flags & i))
                textEnd -= VariableWidthInteger.parseBackward (input, textEnd);
        }

        if (0x1 == (flags & 0x1))
        {
            textEnd -= (input [textEnd] & 0x3);
        }

        recordBytes = Arrays.copyOf (input, textEnd);
        trailingEntries = Arrays.copyOfRange (input, textEnd, input.length);
    }
}
