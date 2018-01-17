package longbridge.dtos;

/**
 * Created by Showboy on 04/07/2017.
 */
public class PasswordStrengthDTO {

    private int minLength;
    private int numOfdigits;
    private int numOfSpecChar;
    private String specialChars;

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getNumOfdigits() {
        return numOfdigits;
    }

    public void setNumOfdigits(int numOfdigits) {
        this.numOfdigits = numOfdigits;
    }

    public int getNumOfSpecChar() {
        return numOfSpecChar;
    }

    public void setNumOfSpecChar(int numOfSpecChar) {
        this.numOfSpecChar = numOfSpecChar;
    }

    public String getSpecialChars() {
        return specialChars;
    }

    public void setSpecialChars(String specialChars) {
        this.specialChars = specialChars;
    }

    @Override
    public String toString() {
        return "PasswordStrengthDTO{" +
                "minLength=" + minLength +
                ", numOfdigits=" + numOfdigits +
                ", numOfSpecChar=" + numOfSpecChar +
                ", specialChars='" + specialChars + '\'' +
                '}';
    }
}
