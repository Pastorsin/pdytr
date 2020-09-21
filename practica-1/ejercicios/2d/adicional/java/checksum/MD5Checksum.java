package checksum;

import java.security.*;


public class MD5Checksum
{
    public static byte[] generate(byte[] message)
    {
        MessageDigest messageDigest = null;

        try
        {
            messageDigest = MessageDigest.getInstance("MD5");

            return messageDigest.digest(message);
        }
        catch (NoSuchAlgorithmException exception)
        {
            exception.printStackTrace();

            return null;
        }
    }

    private static String checksumToString(byte[] checksum)
    {
        StringBuffer stringBuffer = new StringBuffer();

        for (byte bytes : checksum)
            stringBuffer.append(String.format("%02x", bytes & 0xff));

        return new String(stringBuffer);
    }

    public static boolean isValid(byte[] checksum, byte[] message)
    {
        /* Convert checksum field in string */
        String checksumString = checksumToString(checksum);

        /* Generate checksum of the message */
        byte[] checksumMessageInBytes = MD5Checksum.generate(message);
        String checksumMessage = checksumToString(checksumMessageInBytes);

        return checksumString.equals(checksumMessage);
    }

}