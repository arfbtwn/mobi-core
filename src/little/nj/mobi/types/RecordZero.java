package little.nj.mobi.types;

import little.nj.data.Annotations;
import little.nj.data.Serializer;
import little.nj.data.Type;
import little.nj.mobi.format.Enumerations;
import little.nj.mobi.format.ExthHeader;
import little.nj.mobi.format.MobiDocHeader;
import little.nj.mobi.format.PalmDocHeader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class RecordZero implements Type< little.nj.mobi.format.RecordZero >
{
    final static Serializer SERIALIZER = new Annotations ().useDefault ();

    final Type < PalmDocHeader > palm;
    final Type < MobiDocHeader > mobi;
    final Type < ExthHeader >    exth;

    public RecordZero ()
    {
        this ( SERIALIZER );
    }

    public RecordZero ( Serializer serializer )
    {
        palm = serializer.get ( PalmDocHeader.class );
        mobi = serializer.get ( MobiDocHeader.class );
        exth = serializer.get ( ExthHeader.class );
    }

    public final little.nj.mobi.format.RecordZero read ( ByteBuffer stream ) throws IOException
    {
        little.nj.mobi.format.RecordZero struct = new little.nj.mobi.format.RecordZero ();

        struct.palmHead = palm.read ( stream );
        struct.mobiHead = mobi.read ( stream );

        if (!MobiDocHeader.MOBI.equals(struct.mobiHead.identifier))
        {
            throw new RuntimeException ();
        }

        if (struct.mobiHead.hasExth ())
        {
            struct.exthHead = exth.read ( stream );
        }

        byte[] name = new byte [struct.mobiHead.fullNameLength];
        stream.position ( struct.mobiHead.fullNameOffset );
        stream.get ( name );

        Charset set = Enumerations.convert ( struct.mobiHead.encoding, Enumerations.Encoding.class )
                                  .getCharset ();

        struct.bookName = new String ( name, set );

        return struct;
    }

    public final void write ( little.nj.mobi.format.RecordZero struct, ByteBuffer stream ) throws IOException
    {
        int start = stream.position ();

        palm.write ( struct.palmHead, stream );
        mobi.write ( struct.mobiHead, stream );

        if (struct.mobiHead.hasExth ())
        {
            exth.write ( struct.exthHead, stream );
        }

        Charset set = Enumerations.convert ( struct.mobiHead.encoding, Enumerations.Encoding.class )
                                  .getCharset ();

        byte[] name = struct.bookName.getBytes ( set );

        stream.position ( struct.mobiHead.fullNameOffset );
        stream.put ( name );
        stream.putShort ( (short) 0 );

        int pad = (stream.position () - start) % 4;

        if ( 0 != pad )
        {
            stream.put ( new byte [ 4 - pad ] );
        }
    }
}
