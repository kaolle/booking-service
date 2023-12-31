package pb.se.bookingservice.port.security;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Random;

public class JwtUtilsTest {

    @Test
    public void givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect() {
        byte[] array = new byte[64]; // length is bounded by 64
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        System.out.println(generatedString);
    }
    @Test
    public void givenUsingApache_whenGeneratingRandomAlphabeticString_thenCorrect() {
        String generatedString = RandomStringUtils.randomAlphabetic(64);

        System.out.println(generatedString);
    }
    @Test
    public void givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect() {
        int length = 64;
        boolean useLetters = true;
        boolean useNumbers = true;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        System.out.println(generatedString);
    }
}
