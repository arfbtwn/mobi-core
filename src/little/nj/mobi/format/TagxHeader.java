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

import little.nj.data.Annotations.Counted;
import little.nj.data.Annotations.Counter;
import little.nj.data.Annotations.FixedLength;

public class TagxHeader {

    public static final String TAGX = "TAGX";

    @FixedLength(length=4)
    public String identifier = TAGX;

    @Counter(id=0,adjustment=12)
    public int length;

    public int controlBytes;

    @Counted(counter=0)
    public byte[] tags;
}
