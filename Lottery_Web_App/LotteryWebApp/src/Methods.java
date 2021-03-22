import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.Arrays;

/**
 * This class contains all the methods are called and used in my java servlets.
 * @author Travis Higgins
 */
public class Methods {
    /**
     * Returns an array of the byte arrays (each subarray of the array is one encrypted set of user numbers submission).
     * @param source Byte array of all the encrypted draws that will be split up.
     * @return A 2-dimensional byte array where each element in the array is one hashed set of draws.
     */
    private byte[][] byteArraySplitter(byte[] source) {
        int totalBytes = source.length;
        //Each encryption is 256 bytes long so the source is split into an array where each element is 256 bytes
        int amountOfSets = totalBytes/256;
        byte[][] encryptedSetOfBytes = new byte[amountOfSets][256];
        // Creates a new 2 dimensional array where each element has a size 256 bytes
        for (int i = 0; i < amountOfSets; i++) {
            encryptedSetOfBytes[i] = Arrays.copyOfRange(source, i*256 , ((i+1)*256));
        }
        return encryptedSetOfBytes;
    }

    /**
     * Reads encrypted bytes from the filePath specified as a byte array
     * @param filePath This is the location of where the encrypted draws are stored
     * @return This returns an array of all of the bytes in the encrypted array
     */
    private byte[] bytesFileReader(String filePath) {
        try {
            return Files.readAllBytes(Paths.get(filePath));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Used to decrypt one encrypted set of bytes (one draw).
     * @param data Bytes for one draw.
     * @param cipher Cipher used to decrypt the bytes.
     * @param pair Keypair used to decrypt the bytes.
     * @return String of decrypted draw.
     */
    private String decryptData(byte[] data, Cipher cipher, KeyPair pair) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());
            byte[] decipheredText = cipher.doFinal(data);
            String decipheredTextString = new String(decipheredText, StandardCharsets.UTF_8);
            return decipheredTextString;
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This will decrypt all the data in a text file and output a string array.
     * @param filepath This is the filepath of where the encrypted draws are stored.
     * @param pair Keypair used to decrypt the bytes (this is the same for every draw).
     * @param cipher Cipher used to decrypt the bytes.
     * @return A string array of all of the users draws decrypted where each element is a string of one of the users.
     * draws
     */
    public String[] decryptAllUserNum(String filepath, KeyPair pair, Cipher cipher) {
        String decryptedString = "";
        // Reads file in as byte array
        byte[] bytes = bytesFileReader(filepath);
        // Array is split into a 2 dimensional byte array where each element is a set of encrypted draw numbers
        byte[][] arrayOfEncryptedDraws = byteArraySplitter(bytes);
        String[] arrayOfDecryptedData = new String[arrayOfEncryptedDraws.length];
        // Iterates through and decrypts each element (set of draws) and then assigns data to a string array
        for (int i = 0; i < arrayOfEncryptedDraws.length; i++) {
            decryptedString = decryptData(arrayOfEncryptedDraws[i], cipher, pair);
            arrayOfDecryptedData[i] = decryptedString;
        }
        return arrayOfDecryptedData;
    }

    /**
     * Creates a hash of the users password using the SHA-512 hashing algorithm.
     * @param password The users password (before being hashed).
     * @return The users hashed password.
     */
    public String hashCreator(String password) {
        try {
            // Declare which hashing algorithm to use
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(password.getBytes());
            // Create a byte array of hashed bytes
            byte[] byteArray = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            // Appends each hashed byte to a string builder object as a hexadecimal
            for (int i = 0; i < byteArray.length; i++) {
                sb.append(String.format("%02x", byteArray[i]));
            }
            String hashedString = sb.toString();
            return hashedString;
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * This checks if the user has a winning match in there draws that matches the predefined set of winning numbers.
     * @param winningNum This is an integer array of the winning draw.
     * @param arrayOfUserNum This is a string array where each element is one of the users draws.
     * @return If there is a match true is returned, if not false is returned.
     */
    public boolean checkIfUserWon(int[] winningNum, String[] arrayOfUserNum){
        for (int i = 0; i < arrayOfUserNum.length ; i++) {
            // Converts string to array of strings
            String[] integerStrings = arrayOfUserNum[i].split(",");
            // Converts array of strings to array of integers
            int[] intArrayOfOneSetNum = Arrays.stream(integerStrings).mapToInt(Integer::parseInt).toArray();
            // Sort both int arrays into ascending order so order of integers does not affect if the user wins or not
            Arrays.sort(intArrayOfOneSetNum);
            Arrays.sort(winningNum);
            // Return true if winning numbers and the user draw matches
            if (Arrays.equals(winningNum, intArrayOfOneSetNum)) {
                return true;
            }
        }
        // Return false if no matches have been found
        return false;
    }

    /**
     * This writes the cipher text of a users draw to their respective text file.
     * @param filepath This is the filepath of where the encrypted draw will be stored.
     * @param data This is a byte array of the cipher-text.
     */
    public void writeCipherTextToFile(String filepath, byte[] data){
        try {
            File dir = new File("EncryptedNumbers");
            // Creates directory called EncryptedNumbers if it doesnt already exist
            if(!dir.exists()) {
                dir.mkdir();
            }
            File myFile = new File(dir, filepath+".txt");
            // Note append is true so if the file already exists with data inside it then the data is just added to the
            // existing data in the file.
            OutputStream os = new FileOutputStream(myFile, true);
            os.write(data);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This encrypts one of the users draws using the keypair provided.
     * @param draw This is the draw which will get encrypted.
     * @param pair This is the keypair which is used to encrypt the draw.
     * @return A byte array of the encrypted draw is returned.
     */
    public byte[] encryptDraw(String draw, KeyPair pair) {
        try{
            PublicKey publicKey = pair.getPublic();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipher.update(draw.getBytes());
            return cipher.doFinal();
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException ex){
            ex.printStackTrace();
        }
        return null;
    }

}
