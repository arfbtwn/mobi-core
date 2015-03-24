package little.nj.mobi.types;

import little.nj.data.Type;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NcxRecord implements Type < little.nj.mobi.format.NcxRecord >
{
    @Override
    public little.nj.mobi.format.NcxRecord read ( ByteBuffer stream ) throws IOException
    {
        return little.nj.mobi.format.NcxRecord.parse ( stream );
    }

    @Override
    public void write ( little.nj.mobi.format.NcxRecord struct, ByteBuffer stream ) throws IOException
    {
        stream.put ( little.nj.mobi.format.NcxRecord.toByteArray ( struct ) );
    }
}
