/**
 * Created by IntelliJ IDEA. Project ${PROJECT_NAME}. For Grigorov
 * Author: ${USER}
 * Date: ${DATE}
 * Time: ${TIME}
 * Modify:
 * ...
 */
public class Main {
    public static void main( String[] args ) throws Exception {
        System.out.println( "Arma-1 licence key to hexadecimal string converter, see \"https://github.com/ValveSoftware/Proton/issues/767\"!" );
        String keyArma = "60Z1-XB663-SKRWL-CA7DC-J44HH";
        byte[] keyByte  = getByteKey( keyArma );
        String keyHex  = getHexKey( keyByte );
        System.out.printf("+++ Arma key: %s%n", keyArma);
        System.out.printf("+++ Reg. key: %s%n", keyHex);
        String reg = registryToSerial( keyByte );
        System.out.printf("+++ Arma key: %s restored from registry%n", reg);
    }
    private static final String szTemplate = "0123456789ABCDEFGHJKLMNPRSTVWXYZ";
    public static String getHexKey(String arma2key) throws Exception {
        byte[] bResult = getByteKey(arma2key);
        return javax.xml.bind.DatatypeConverter.printHexBinary( bResult );
    }

    public static String getHexKey(byte[] armaKey) throws Exception {
        return javax.xml.bind.DatatypeConverter.printHexBinary( armaKey );
    }

    public static byte[] getByteKey(String arma2key) throws Exception {
        String upperkey = arma2key.trim().toUpperCase().replace( 'O', '0' ).replace( 'I', '1' ).replace( "-", "" );
        byte[] bResult = new byte[15];
        if ( upperkey.length() != 24 )
            throw new Exception( "Invalid key length" );

        for ( int i = 0; i < 3; ++i ) {
            long qwResult = 0;
            for ( int j = 0; j < 8; ++j ) {
                char cChar = upperkey.charAt( i * 8 + j);
                long szPos = szTemplate.indexOf( cChar );
                qwResult |= szPos << ( j * 5 );
                System.out.println("");
            }
            for ( int j = 0; j < 5; ++j ) {
                bResult[i * 5 + 5 - 1 - j] = ( byte ) ( qwResult & 0xFF );
                qwResult >>= 8;
            }
        }
        return bResult;
    }


    public static String registryToSerial( byte[] registry )
    {
        if (registry == null)
            throw new NullPointerException("registry");
        if (registry.length != 15)
            throw new IllegalArgumentException("Input array is not a valid registry value. (Should contain 15 bytes)");

        String bResult = "";

        for (int i = 0; i < 3; ++i)
        {
            long qwResult = 0;
            for (int j = 0; j < 5; ++j)
            {
                qwResult <<= 8;
                qwResult |= registry[i * 5 + j];
            }

            for (int j = 0; j < 8; ++j)
            {
                long szPos = (qwResult >> (j * 5)) & 0x1F;
                char cChar = szTemplate.charAt( (int)szPos);
                bResult += cChar;
            }
        }

        return bResult;
    }
}