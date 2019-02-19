package edu.acc.j2ee.hubbub;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.Sha2Crypt;

/**
 * HashTool provides a password validation utility backed by Apache Commons Codec
 * SHA-256 Hash functionality. A password is stored as a SHA-256 hash with salt.
 * The salt begins with $5$ and an alphanumeric sequence terminated by $. This
 * utility can produce such a hash, perhaps at user sign-up, and validates a
 * plain-text value against a hash of the value.
 * 
 * @author sdgilkey
 */
public class HashTool {
    
    /**
        The salt for libc crypt is $5$ together with an alphanumeric sequence terminated
        by $.
    */
    public static final Pattern SALT_PATT = Pattern.compile("^(\\$\\d\\$.+)\\$");   
    
    /**
     * Produce a salted SHA-256 hash of the provided text.
     * @param text to hash
     * @return a libc crypt-compatible salted SHA-256 hash string
     */
    public static String hash(String text) {
        return Sha2Crypt.sha256Crypt(text.getBytes());
    }
    
    /**
     * Test a plaintext String against an existing hash.
     * @param plainText the plain-text to hash and compare
     * @param hash the pre-hashed value to compare against the plain text candidate
     * @return true if the hashes match
     */
    public static boolean compare(String plainText, String hash) {
        Matcher m = SALT_PATT.matcher(hash);
        boolean found = m.find();
        if (found) {
            String salt = m.group(1);
            String saltedCandidate = Sha2Crypt.sha256Crypt(plainText.getBytes(), salt);
            return saltedCandidate.equals(hash);
        }
        else throw new RuntimeException("Saved password hash does not contain a salt");
    }

}
