public class JumpGame {
    public static boolean recCanWin(int[] arr, int num) {
        if (num >= arr.length - 1) {
            return true;
        }

        int jump = arr[num];
        for (int i = 1; i <= jump; i++) {
            int step = num + i;
            if (recCanWin(arr, step)) {
                return true;
            }
        }
        return false;
    }

    public static boolean canWin(int[] array) {

        return recCanWin(array, 0);
    }
}
