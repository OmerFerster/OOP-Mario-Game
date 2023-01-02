package pepse.util;

public class Utils {

    public static int round(int number, int factor) {
        return (number >= 0 ? number - (number % factor) : number - (factor + (number % factor)));
    }
}
