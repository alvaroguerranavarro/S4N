package co.com.s4n.training.java.jdk;
import io.vavr.control.Option;
import io.vavr.control.Try;

import static io.vavr.API.None;

public class Prueba {
    public static Try<Integer> multiplicar (int a, int b)
    {
        System.out.println("Multiplicando"+ a + "*" + b + "="+a*b);
        return Try.of(() -> a*b);

    }
    public static Try<Integer> comparar (int a, int b)
    {
        if (a>b)
        {
            System.out.println("Comparar"+ a + ">" + b);
            return Try.of(()->a);
        }
        else
        {
            System.out.println("Comparar"+ b + ">" + a);
            return Try.of(()->b);
        }
    }
    public static Try<Integer> division (int a, int b)
    {
        //System.out.println("Dividiendo"+ a + "/" + b + "= "+a/b);
        if (b!=0)
        {
            System.out.println("Dividiendo"+ a + "/" + b + "= "+a/b);
            return Try.of(()->a/b);
        }
        else
        {
            return Try.failure(new Exception("Error al dividir por 0"));
        }

    }

    public static Try<Integer> postivo (int a)
    {
        if (a>0)
        {
            System.out.println("Numero positivo "+ a);
            return Try.of(()->a);
        }
        else
        {
            if (a<0)
            {
                System.out.println("Numero negativo "+ a);
                return Try.of(()->a);
            }
            else
            {
                System.out.println("Numero igual a "+ 0);
                return Try.of(()->0);
            }
        }
    }
}