package little.nj.mobi.util;

import little.nj.mobi.format.*;
import little.nj.mobi.format.IndexRecord.IdxtEntry;
import little.nj.util.StringUtil;

import javax.xml.bind.DatatypeConverter;
import java.util.Map;

import static java.lang.System.out;
import static little.nj.mobi.format.Enumerations.*;

public class ConsoleUtil
{
    private ConsoleUtil () { }

    public static void printPdbHead ( PdbHeader pdbHead )
    {
        out.println ( "## PDB ##" );
        out.println ( "Name:    " + pdbHead.name );
        out.println ( "Type:    " + new String ( pdbHead.type ) );
        out.println ( "Creator: " + new String ( pdbHead.creator ) );
        out.println ( "Records: " + pdbHead.recordCount );
        out.println ();
    }

    public static void printPalmHead ( PalmDocHeader palmHead )
    {
        out.println ( "## Palm ##" );
        out.println ( "Text Size:         " + palmHead.textRecordSize );
        out.println ( "Text Count:        " + palmHead.textRecordCount );
        out.println ( "Compression:       " + tryParse ( palmHead.compression, Compression.class ) );
        out.println ( "Uncompressed Size: " + palmHead.uncompressedTextLength );
        out.println ( "Current Position:  " + palmHead.currentPosition );
        out.println ();
    }

    public static void printMobiHead ( MobiDocHeader mobiHead )
    {
        out.println ( "## Mobi ##" );
        out.println ( "Header Length:  " + mobiHead.length );
        out.println ( "Header Version: " + mobiHead.version );
        out.println ( "Minimum Reader: " + mobiHead.minimumReaderVersion );
        out.println ( "Unique ID:      " + mobiHead.uniqueID );
        out.println ();
        out.println ( "Orth Index:     " + mobiHead.orthographicIndex );
        out.println ( "Infl'n Index:   " + mobiHead.inflexionIndex );
        out.println ( "Index Names:    " + mobiHead.indexNames );
        out.println ( "Index Keys:     " + mobiHead.indexKeys );
        out.println ( "Index Extra 1:  " + mobiHead.indexExtra1 );
        out.println ( "Index Extra 2:  " + mobiHead.indexExtra2 );
        out.println ( "Index Extra 3:  " + mobiHead.indexExtra3 );
        out.println ( "Index Extra 4:  " + mobiHead.indexExtra4 );
        out.println ( "Index Extra 5:  " + mobiHead.indexExtra5 );
        out.println ( "Index Extra 6:  " + mobiHead.indexExtra6 );
        out.println ();
        out.println ( "Document Type:  " + tryParse ( mobiHead.type, MobiType.class ) );
        out.println ( "Encoding:       " + tryParse ( mobiHead.encoding, Encoding.class ) );
        out.println ( "Language:       " + tryParse ( mobiHead.language, Language.class ) );
        out.println ( "Dialect:        " + tryParse ( mobiHead.dialect, Dialect.class ) );
        out.println ( "Language in:    " + mobiHead.languageInput );
        out.println ( "Language out:   " + mobiHead.languageOutput );
        out.println ( "First Content:  " + mobiHead.firstContentRecord );
        out.println ( "First Non Book: " + mobiHead.firstNonBookRecord );
        out.println ( "First Huffman:  " + mobiHead.firstHuffmanRecord );
        out.println ( "First Image:    " + mobiHead.firstImageRecord );
        out.println ( "Last Content:   " + mobiHead.lastContentRecord );
        out.println ( "FDST:           " + mobiHead.fdstRecord );
        out.println ( "FLIS:           " + mobiHead.flisRecord );
        out.println ( "FCIS:           " + mobiHead.fcisRecord );
        out.println ( "SRCS:           " + mobiHead.srcsRecord );
        out.println ( "SRCS Count:     " + mobiHead.srcsCount );
        out.println ( "INDX:           " + mobiHead.indxRecord );
        out.println ( "Fragment:       " + mobiHead.fragmentRecord );
        out.println ( "Skeleton:       " + mobiHead.skeletonRecord );
        out.println ( "DATP:           " + mobiHead.datpRecord );
        out.println ( "Guide:          " + mobiHead.guideRecord );
        out.println ();
    }

    public static void printExthHead ( ExthHeader exthHead )
    {
        out.println ( "## EXTH ##" );
        out.println ( "Length:  " + exthHead.length );
        out.println ( "Records: " + exthHead.count );
        out.println ();

        ExthUtil exthUtil = new ExthUtil ();

        for ( ExthRecord i : exthHead.records )
        {
            String name = exthUtil.nameOf ( i );
            Object value = exthUtil.decode ( i );
            out.printf ( "%d => %s: %s%n", i.id, name, value.toString () );
        }

        if ( 0 < exthHead.records.length )
        {
            out.println ();
        }
    }

    public static void printIndx( IndxHeader indxHead )
    {
        if ( null == indxHead ) return;

        out.println ( "## INDX ##" );
        out.println ( "First Entry:       " + indxHead.firstEntryOffset );
        out.println ( "Type:              " + indxHead.indxType );
        out.println ( "IDXT Offset:       " + indxHead.idxtOffset );
        out.println ( "Index Count:       " + indxHead.indexCount );
        out.println ( "Index Encoding:    " + tryParse ( indxHead.indexEncoding, Encoding.class ) );
        out.println ( "Index Language:    " + tryParse ( indxHead.indexLanguage, Language.class ) );
        out.println ( "Index Dialect:     " + tryParse ( indxHead.indexDialect, Dialect.class ) );
        out.println ( "Total Entry Count: " + indxHead.totalEntryCount );
        out.println ( "ORDT Offset:       " + indxHead.ordtOffset );
        out.println ( "LIGT Offset:       " + indxHead.ligtOffset );
        out.println ( "LIGT Count:        " + indxHead.ligtCount );
        out.println ( "CNCX Count:        " + indxHead.cncxCount );
        out.println ( "ORDT Count:        " + indxHead.ordtCount );
        out.println ( "ORDT Entries:      " + indxHead.ordtEntries );
        out.println ( "TAGX Offset:       " + indxHead.tagxOffset );
        out.println ();
    }

    public static void printTagx ( TagxHeader tagxHead)
    {
        if ( null == tagxHead ) return;

        String tags = DatatypeConverter.printHexBinary ( tagxHead.tags );

        out.println ( "## TAGX ##" );
        out.println ( "Length:        " + tagxHead.length );
        out.println ( "Control Bytes: " + tagxHead.controlBytes );
        out.println ( "Tags:          " + tagxHead.tags.length + " => " + tags );
        out.println ();
    }

    public static void printIdxt ( Map < Integer, IdxtEntry > map, int cb, TagxTag[] tags )
    {
        out.println ( "## IDXT ##" );
        out.println ( "Count: " + map.size () );
        out.println ();

        for ( Map.Entry< Integer, IdxtEntry > kv : map.entrySet () )
        {
            int key = kv.getKey ();
            IdxtEntry value = kv.getValue ();

            String decoded = StringUtil.valueOf ( value.decode ( cb, tags ) );
            out.println ( key + " => " + value.ident + ": " + decoded );
        }
        out.println ();
    }

    public static void printNcx ( NcxRecord[] records )
    {
        if ( null == records || 0 == records.length ) return;

        out.println ( "## NCX ##" );

        for ( NcxRecord ncxRecord : records )
        {
            for ( String entry : ncxRecord )
            {
                out.println ( entry );
            }
        }
        out.println ();
    }
}
