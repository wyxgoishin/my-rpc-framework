public class Test implements X, Y{
    public static void main(String[] args) {
        X ct = new ChildTest();
        System.out.println(ct.getClass().getCanonicalName());
    }
}

interface X{}
interface Y{}

class ChildTest extends Test{}
