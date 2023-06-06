/**
 * Created by IntelliJ IDEA. Project ${PROJECT_NAME}. For the normal community guys.
 * Author: ${USER}
 * Date: ${DATE}
 * Time: ${TIME}
 * Modify:
 * ...
 */
public class Main {

    public static final String KEY_HEX = "\"KEY\"=hex:";

    public static void main( String[] args ) throws Exception {
        System.out.println( "Arma-1 licence key to hexadecimal string converter, see \"https://github.com/ValveSoftware/Proton/issues/767\"!" );
        String keyArma = "60Z1-XB663-SKRWL-CA7DC-J44HH";
        byte[] keyByte = getByteKey( keyArma );
        String keyHex = getHexKey( keyByte );
        System.out.printf( "+++ Arma key: %s%n", keyArma );
        System.out.printf( "+++ Reg. key: %s%n", keyHex );
        String ser = bytesToSerial( keyByte );
        System.out.printf( "+++ Arma key: %s restored from registry%n", ser );
        String reg = getKeyAsReg(  keyByte );
        System.out.printf( "+++ %s%n", reg );
        keyArma = registryToSerial( reg );
        System.out.printf( "+++ Key restored from registry: %s%n", keyArma );
        keyArma = registryToSerial( "\"KEY\"=hex:1d,0c,70,e4,22,1d,3f,a9,6c,0a,13,20,c9,e2,0f" );
        System.out.printf( "+++ My Arma-1 rey restored from registry: %s%n", keyArma );
    }

    private static final String szTemplate = "0123456789ABCDEFGHJKLMNPRSTVWXYZ";

    public static String getKeyAsHex( String arma2key ) throws Exception {
        byte[] bResult = getByteKey( arma2key );
        return javax.xml.bind.DatatypeConverter.printHexBinary( bResult );
    }

    public static String getHexKey( byte[] armaKey ) throws Exception {
        return javax.xml.bind.DatatypeConverter.printHexBinary( armaKey );
    }

// "KEY"=hex:1d,0c,70,e4,22,1d,3f,a9,6c,0a,13,20,c9,e2,0f
    public static String getKeyAsReg( byte[] armaKey ) throws Exception {
        StringBuilder sb = new StringBuilder(64 );
        sb.append( KEY_HEX );
        for(byte bt: armaKey)
            sb.append(String.format("%02x,", bt));
        return sb.substring( 0, sb.length() - 1 );
    }
    public static byte[] getByteKey( String arma2key ) throws Exception {
        String upperkey = arma2key.trim().toUpperCase().replace( 'O', '0' ).replace( 'I', '1' ).replace( "-", "" );
        byte[] bResult = new byte[15];
        if ( upperkey.length() != 24 )
            throw new Exception( "Invalid key length" );

        for ( int i = 0; i < 3; ++i ) {
            long qwResult = 0;
            // Converts char to its index from sample szTemplate buffer
            // Put 40 bits to the long var (8 packs of 5 bit for each char) in the start order:
            // first char in first 5 bits etc
            for ( int j = 0; j < 8; ++j ) {
                char cChar = upperkey.charAt( i * 8 + j );
                long szPos = szTemplate.indexOf( cChar ); // Convert 8 char bit (0.255) to the index 5 bits (0..31)
                qwResult |= szPos << ( j * 5 );
                System.out.print( "" );
            }
            // put 5 bytes containing 8 indexes each of 5 bits length (8 * 5 = 40)
            // in start order (1st char in high 5 bits of zero offset array byte)
            for ( int j = 0; j < 5; ++j ) {
                bResult[i * 5 + 5 - 1 - j] = ( byte ) ( qwResult & 0xFF );
                qwResult >>>= 8;
            }
        }
        return bResult;
    }

    /**
     *  Convert string in format "\"KEY\"=hex:31,97,d0,fc,06,53,29,cc,4f,23,8c,48,49,31,a7" to the
     *  "60Z1-XB663-SKRWL-CA7DC-J44HH"
     * @param registry string in format "\"KEY\"=hex:31,97,d0,fc,06,53,29,cc,4f,23,8c,48,49,31,a7" to the
     * @return string in format "60Z1-XB663-SKRWL-CA7DC-J44HH"
     */
    public static String registryToSerial( String registry ) {
        int pos = registry.indexOf( KEY_HEX );
        if (pos < 0) {
            System.err.printf("--- Expected start sequence \"%s\" not detected", KEY_HEX );
            return null;
        }
        byte[] bytes =  new byte[15];
        final int len = KEY_HEX.length();
        String[] vals = registry.substring( len ).split( "," );
        if (vals.length != 15) {
            System.err.printf("--- Expected number of values must be 15, found %d", len );
            return null;
        }
        for ( int i = 0; i < 15; i++ )
            bytes[i] = (byte)(Integer.parseInt( vals[i].toUpperCase(), 16 ) & 0xFF);
        return bytesToSerial( bytes );
    }

    public static String bytesToSerial( byte[] bytes ) {
        if ( bytes == null )
            throw new NullPointerException( "registry" );
        if ( bytes.length != 15 )
            throw new IllegalArgumentException( "Input array is not a valid registry value. (Should contain 15 bytes)" );

        char[] cResult = new char[24];
        int pos = 0;

        for ( int i = 0; i < 3; ++i ) {
            long qwResult = 0;
            for ( int j = 0; j < 5; ++j ) {
                qwResult <<= 8;
                qwResult |= bytes[i * 5 + j] & 0xFF;
            }

            for ( int j = 0; j < 8; ++j ) {
                long szPos = ( qwResult >>> ( j * 5 ) ) & 0x1F;
                char cChar = szTemplate.charAt( ( int ) szPos );
                cResult[pos++] = cChar;
            }
        }
        return String.format( "%s-%s-%s-%s-%s",
                              new String( cResult, 0, 4 ),
                              new String( cResult, 4, 5 ),
                              new String( cResult, 9, 5 ),
                              new String( cResult, 14, 5 ),
                              new String( cResult, 19, 5 )
                            );
    }
}