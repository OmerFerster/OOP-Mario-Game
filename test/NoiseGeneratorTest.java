import pepse.util.NoiseGenerator;

public class NoiseGeneratorTest {

    public static void main(String[] args) {
//        double smoothness = 35;
//        NoiseGenerator noiseGenerator = new NoiseGenerator(50);
//        for (int i = 0; i < 512; i++) {
//            System.out.println(noiseGenerator.noise((float) (i / smoothness)));
//        }
//        System.out.println((-95) % 30);
//        System.out.println(40 % 30);

        // TESTING CALLBACKS
        Callback<Integer> a = num -> num + 10;
        Callback<String> b = String::toUpperCase;
        Callback<Integer> c = (num) -> {
            if (num == 0) {
                num = 1;
            } else {
                num = num + 100;
            }
            return num;
        };
        System.out.println(c.apply(0));
        Consumer<Integer> omer = num -> System.out.println(num);
        Consumer<String> liel = str -> {
            if (str.equals("fuck")) {
                System.out.println(str);
            } else {
                System.out.println("byeAh");
            }
        };
        omer.run(20);
        liel.run("a");
    }

    @FunctionalInterface
    public interface Callback<T> {
        T apply(T elem);
    }

    @FunctionalInterface
    public interface Consumer<T> {
        void run(T elem);
    }


}
