import java.io.*;
import java.security.*;


public class md5
{

    public static void main(String[] args)
    {
        {
            String data = "hola";
            String checksum = "4d186321c1a7f0f354b297e8914ab240";

            MessageDigest messageDigest;
            try
            {
                messageDigest = MessageDigest.getInstance("MD5");

                messageDigest.update(data.getBytes("UTF-8"));
                byte[] messageDigestMD5 = messageDigest.digest();

                System.out.println(checksum);
                System.out.println(stringBuffer);

                System.out.println(checksum.equals(receivedChecksum));
            }
            catch (NoSuchAlgorithmException exception)
            {
                exception.printStackTrace();
            }
            catch(UnsupportedEncodingException exception){
            	exception.printStackTrace();
            }

        }
    }
}
