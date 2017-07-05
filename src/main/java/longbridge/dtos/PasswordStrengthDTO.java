package longbridge.dtos;

/**
 * Created by Showboy on 04/07/2017.
 */
public class PasswordStrengthDTO {

    private int minLength;
    private String digits;
    private String specialChars;

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public String getDigits() {
        return digits;
    }

    public void setDigits(String digits) {
        this.digits = digits;
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
                ", digits='" + digits + '\'' +
                ", specialChars='" + specialChars + '\'' +
                '}';
    }
}
