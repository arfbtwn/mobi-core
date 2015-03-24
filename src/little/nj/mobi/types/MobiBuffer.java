package little.nj.mobi.types;

import little.nj.data.Annotations;
import little.nj.data.Buffer;

public class MobiBuffer extends Buffer
{
    static class MobiSerializer extends Annotations
    {
        public MobiSerializer ()
        {
            useDefault ();

            get ( little.nj.mobi.format.PdbHeader.class );
            get ( little.nj.mobi.format.PdbRecord.class );

            get ( little.nj.mobi.format.IndxHeader.class );
            get ( little.nj.mobi.format.TagxHeader.class );

            use ( little.nj.mobi.format.RecordZero.class, new RecordZero ( this ) );
            use ( little.nj.mobi.format.NcxRecord .class, new NcxRecord  () );
        }
    }

    public final static MobiBuffer Instance = new MobiBuffer ();

    public MobiBuffer()
    {
        super ( new MobiSerializer () );
    }
}
