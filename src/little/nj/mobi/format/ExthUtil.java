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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExthUtil {

    public static final String UNKNOWN = "Unknown";

    /*
     * Known Strings
     */
    public static final int TITLE           = 99;
    public static final int AUTHOR          = 100;
    public static final int PUBLISHER       = 101;
    public static final int BLURB           = 103;
    public static final int ISBN            = 104;
    public static final int SUBJECT         = 105;
    public static final int PUBLISHING_DATE = 106;
    public static final int REVIEW          = 107;
    public static final int CONTRIBUTOR     = 108;
    public static final int RIGHTS          = 109;
    public static final int HASH            = 113;
    public static final int RETAIL_PRICE    = 118;
    public static final int RETAIL_CURRENCY = 119;
    public static final int KF8_COVER_URI   = 129;
    public static final int CDE_TYPE        = 501;
    public static final int UPDATED_DATE    = 502;
    public static final int UPDATED_TITLE   = 503;
    public static final int CREATOR_EXTRA   = 535;

    /*
     * Known Integers
     */
    public static final int START_READING   = 116;
    public static final int KF8_OFFSET      = 121;
    public static final int RESOURCE_COUNT  = 125;
    public static final int COVER           = 201;
    public static final int THUMB           = 202;
    public static final int HASFAKECOVER    = 203;
    public static final int CREATOR_ID      = 204;
    public static final int CREATOR_MAJOR   = 205;
    public static final int CREATOR_MINOR   = 206;
    public static final int CREATOR_BUILD   = 207;
    public static final int FONT_SIGNATURE  = 300;

    final Charset charset;

    Map<Integer, String> all = new HashMap<Integer, String>();
    Set<Integer> integers = new HashSet<Integer>();

    public ExthUtil(Charset charset)
    {
        this.charset = charset;
        registerDefaults();
    }

    public ExthUtil()
    {
        this(Charset.defaultCharset());
    }

    protected void registerDefaults()
    {
        register(AUTHOR, "Author");
        register(PUBLISHER, "Publisher");
        register(BLURB, "Blurb");
        register(ISBN, "ISBN");
        register(SUBJECT, "Subject");
        register(REVIEW, "Review");
        register(PUBLISHING_DATE, "Publishing Date");
        register(RIGHTS, "Rights");
        register(CONTRIBUTOR, "Contributor");
        register(HASH, "Hash");
        register(RETAIL_PRICE, "Retail Price");
        register(RETAIL_CURRENCY, "Retail Currency");
        register(KF8_COVER_URI, "KF8 Cover Uri");
        register(CDE_TYPE, "CDE Type");
        register(TITLE, "Title");
        register(UPDATED_TITLE, "Updated Title");

        registerInt(START_READING, "Start Reading");
        registerInt(KF8_OFFSET, "KF8 Offset");
        registerInt(RESOURCE_COUNT, "Resource Count");
        registerInt(COVER, "Cover");
        registerInt(THUMB, "Thumbnail");
        registerInt(HASFAKECOVER, "Has Fake Cover");
        registerInt(CREATOR_ID, "Creator ID");
        registerInt(CREATOR_MAJOR, "Creator Major");
        registerInt(CREATOR_MINOR, "Creator Minor");
        registerInt(CREATOR_BUILD, "Creator Build");
        registerInt(FONT_SIGNATURE, "Font Signature");

        registerInt(131);
    }

    public void register(int id, String name)
    {
        all.put(id, name);
    }

    public void register(int id)
    {
        register(id, null);
    }

    public void registerInt(int id, String name)
    {
        all.put(id, name);
        integers.add(id);
    }

    public void registerInt(int id)
    {
        registerInt(id, null);
    }

    public String nameOf(ExthRecord record)
    {
        String name = all.get(record.id);
        return null == name ? UNKNOWN : name;
    }

    public Object decode(ExthRecord record)
    {
        if (integers.contains(record.id))
        {
            return ByteBuffer.wrap(record.data).getInt();
        }

        return new String(record.data, charset);
    }

    public void encode(ExthRecord record, Object value)
    {
        if (integers.contains(record.id))
        {
            ByteBuffer.wrap(record.data).putInt((Integer) value);
            return;
        }

        record.data = value.toString().getBytes(charset);
        record.length = record.data.length - 8;
    }
}
