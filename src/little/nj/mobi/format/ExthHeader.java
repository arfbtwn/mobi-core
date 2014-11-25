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

import little.nj.data.MarshalBuilder.*;

public class ExthHeader {

    public static final String EXTH = "EXTH";

    @Constant
    @FixedLength(length=4)
    public final String identifier = EXTH;
    public int          length;

    @Counter(id=1)
    public int          count;

    @Counted(counter=1)
    public ExthRecord[] records;

}
