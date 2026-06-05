package utils;


import book.PairOfUidAndBookId;
import com.bit.utils.FileUtils;
import constants.Constants;

// 这里面的是后期 MySQL 中的 dao 层
public class AnalyzingBorrowedBook {


    public PairOfUidAndBookId[] loadObject(String filename) {
        String fileContent = FileUtils.readFile(filename);



        String[] uidAndBookIdArrays = fileContent.split(Constants.OBJECT_SEPARATOR);
        PairOfUidAndBookId[] userIdAndBookIdList = new PairOfUidAndBookId[uidAndBookIdArrays.length];
        int index = 0;
        for (String uidAndBookIdStr : uidAndBookIdArrays) {
            PairOfUidAndBookId uidAndBookId = PairOfUidAndBookId.parse(uidAndBookIdStr);
            if (uidAndBookId != null) {
                userIdAndBookIdList[index++] = uidAndBookId;
            }
        }

        return userIdAndBookIdList;
    }

    public void storeObject(PairOfUidAndBookId[] userIdAndBookIdList, String fileName) {
        if (userIdAndBookIdList == null || fileName == null || fileName.isEmpty()) {
            return;
        }


        StringBuilder userIdAndBookIdJson = new StringBuilder();
        for (PairOfUidAndBookId uidAndBookId : userIdAndBookIdList) {
            if (uidAndBookId == null) break;
            userIdAndBookIdJson.append(uidAndBookId.toJson()).append(Constants.OBJECT_SEPARATOR);
        }

        FileUtils.writeFile(userIdAndBookIdJson.toString(), fileName);
    }

    public static void main(String[] args) {
//        storeTest();
        loadTest();
    }

    private static void loadTest() {
        PairOfUidAndBookId[] pairOfUidAndBookIds = new AnalyzingBorrowedBook().loadObject(Constants.BOOKS_USERS_FILE_NAME);
        for (PairOfUidAndBookId pairOfUidAndBookId : pairOfUidAndBookIds) {
            System.out.println(pairOfUidAndBookId);
        }

    }

    private static void storeTest() {
        PairOfUidAndBookId[] list = new PairOfUidAndBookId[]{
                new PairOfUidAndBookId(1, 1),
                new PairOfUidAndBookId(2, 2),
                new PairOfUidAndBookId(3, 1),
        };
        new AnalyzingBorrowedBook().storeObject(list, Constants.BOOKS_USERS_FILE_NAME);
    }

}
