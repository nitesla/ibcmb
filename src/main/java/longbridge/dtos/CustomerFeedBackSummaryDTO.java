package longbridge.dtos;

import java.math.BigDecimal;

public class CustomerFeedBackSummaryDTO {
        private String tranType;
        private BigDecimal retailRating;
        private BigDecimal corporateRating;
        private BigDecimal bothRating;
        private int retailRatingReviews;
        private int corporateRatingReviews;
        private int bothRatingReviews;

    public int getCorporateRatingReviews() {
        return corporateRatingReviews;
    }

    public void setCorporateRatingReviews(int corporateRatingReviews) {
        this.corporateRatingReviews = corporateRatingReviews;
    }

    public int getBothRatingReviews() {
        return bothRatingReviews;
    }

    public void setBothRatingReviews(int bothRatingReviews) {
        this.bothRatingReviews = bothRatingReviews;
    }



    public int getRetailRatingReviews() {
        return retailRatingReviews;
    }

    public void setRetailRatingReviews(int retailRatingReviews) {
        this.retailRatingReviews = retailRatingReviews;
    }


    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public BigDecimal getRetailRating() {
        return retailRating;
    }

    public void setRetailRating(BigDecimal retailRating) {
        this.retailRating = retailRating;
    }

    public BigDecimal getCorporateRating() {
        return corporateRating;
    }

    public void setCorporateRating(BigDecimal corporateRating) {
        this.corporateRating = corporateRating;
    }

    public BigDecimal getBothRating() {
        return bothRating;
    }

    public void setBothRating(BigDecimal bothRating) {
        this.bothRating = bothRating;
    }
}
