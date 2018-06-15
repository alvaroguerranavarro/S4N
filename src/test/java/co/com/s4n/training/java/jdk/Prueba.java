package co.com.s4n.training.java.jdk;
import io.vavr.control.Option;

public class Prueba {
    public static Option<Integer> multiplicar (int a, int b)
    {
        System.out.println("Multiplicando"+ a + "*" + b + "="+a*b);
        return Option.of(a*b);
    }
    public static Option<Integer> comparar (int a, int b)
    {
        if (a>b)
        {
            System.out.println("Comparar"+ a + ">" + b);
            return Option.of(a);
        }
        else
        {
            if (b>a)
            {
                System.out.println("Comparar"+ b + ">" + a);
                return Option.of(b);
            }
            else
            {
                System.out.println("Comparar"+ a + "=" + b);
                return Option.of(a=b);
            }
        }
    }
    public static Option<Integer> division (int a, int b)
    {
        System.out.println("Dividiendo"+ a + "/" + b + "= "+a/b);
        return Option.of(a/b);
    }

    public static Option<Integer> postivo (int a)
    {
        if (a>0)
        {
            System.out.println("Numero positivo "+ a);
            return Option.of(a);
        }
        else
        {
            if (a<0)
            {
                System.out.println("Numero negativo "+ a);
                return Option.of(a);
            }
            else
            {
                System.out.println("Numero igual a "+ 0);
                return Option.of(0);
            }
        }
    }


}