package book;

import constants.Constants;

import java.util.Objects;

public class PairOfUidAndBookId {
    private Integer bookId;
    private Integer userId;

    public PairOfUidAndBookId() {
    }

    public PairOfUidAndBookId(Integer bookId, Integer userId) {
        this.bookId = bookId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PairOfUidAndBookId that = (PairOfUidAndBookId) o;
        return Objects.equals(bookId, that.bookId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, userId);
    }

    @Override
    public String toString() {
        return "PairOfUidAndBookId{" +
                "bookId=" + bookId +
                ", userId=" + userId +
                '}';
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public static PairOfUidAndBookId parse(String uidAndBookIdStr) {
        String[] split = uidAndBookIdStr.split(Constants.OBJECT_MEMBER_SEPARATOR);
        for (String str : split) {
            if (str.isEmpty()) return null;
        }
        PairOfUidAndBookId ret = new PairOfUidAndBookId(
                Integer.parseInt(split[0]),
                Integer.parseInt(split[1])
        );
        return ret;
    }


    public String toJson() {
        StringBuilder ret = new StringBuilder();
        ret.append(bookId).append(Constants.OBJECT_MEMBER_SEPARATOR)
                .append(userId);
        return ret.toString();
    }
}
