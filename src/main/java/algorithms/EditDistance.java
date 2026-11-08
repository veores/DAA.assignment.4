package algorithms;

public interface EditDistance {

    int distance(String a, String b);

    long operations();

    long memoryCells();

    String name();
}
