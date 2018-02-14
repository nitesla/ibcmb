package longbridge.utils;

import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;



/**
 * Created by Fortune on 2/14/2018.
 */
public class TransferUtilsTest {


    private TransferUtils transferUtils;

    @Before
    public void setUp(){
        transferUtils = new TransferUtils();
    }



    @Test
    public void givenInputOf12_thenWillGenerate12DigitNumber() throws Exception {

        String refNum = transferUtils.generateReferenceNumber(12);
        System.out.println("The reference number is "+refNum);
        assertEquals("12",""+refNum.length());

}

    @Test
    public void givenInputOf10_thenWillGenerate10DigitNumber() throws Exception {

        String refNum = transferUtils.generateReferenceNumber(10);
        System.out.println("The reference number is "+refNum);
        assertEquals("10",""+refNum.length());

    }


    @Test
    public void givenInputOf6_thenWillGenerate6DigitNumber() throws Exception {

        String refNum = transferUtils.generateReferenceNumber(6);
        System.out.println("The reference number is "+refNum);
        assertEquals("6",""+refNum.length());

}


    @Test
    public void givenInputOf3_thenWillGenerate3DigitNumber() throws Exception {

        String refNum = transferUtils.generateReferenceNumber(3);
        System.out.println("The reference number is "+refNum);
        assertEquals("3",""+refNum.length());

}

    @Test
    public void givenInputOf1_thenWillGenerate1DigitNumber() throws Exception {

        String refNum = transferUtils.generateReferenceNumber(1);
        System.out.println("The reference number is "+refNum);
        assertEquals("1",""+refNum.length());

}

    @Test(expected = IllegalArgumentException.class)
    public void givenAnyNumber_whenEqualToZero_thenThrowException() throws Exception {

        String refNum = transferUtils.generateReferenceNumber(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenAnyNumber_whenNegative_thenThrowException() throws Exception {

        String refNum = transferUtils.generateReferenceNumber(-4);
    }


}
