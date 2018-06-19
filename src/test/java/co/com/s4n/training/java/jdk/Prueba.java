package co.com.s4n.training.java.jdk;
import io.vavr.control.Option;
<<<<<<< HEAD
import io.vavr.control.Try;

import static io.vavr.API.None;

public class Prueba {
    public static Try<Integer> multiplicar (int a, int b)
    {
        System.out.println("Multiplicando"+ a + "*" + b + "="+a*b);
        return Try.of(() -> a*b);

    }
    public static Try<Integer> comparar (int a, int b)
=======

public class Prueba {
    public static Option<Integer> multiplicar (int a, int b)
    {
        System.out.println("Multiplicando"+ a + "*" + b + "="+a*b);
        return Option.of(a*b);
    }
    public static Option<Integer> comparar (int a, int b)
>>>>>>> feature/PruebaSuite
    {
        if (a>b)
        {
            System.out.println("Comparar"+ a + ">" + b);
<<<<<<< HEAD
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
=======
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
>>>>>>> feature/PruebaSuite
    {
        if (a>0)
        {
            System.out.println("Numero positivo "+ a);
<<<<<<< HEAD
            return Try.of(()->a);
=======
            return Option.of(a);
>>>>>>> feature/PruebaSuite
        }
        else
        {
            if (a<0)
            {
                System.out.println("Numero negativo "+ a);
<<<<<<< HEAD
                return Try.of(()->a);
=======
                return Option.of(a);
>>>>>>> feature/PruebaSuite
            }
            else
            {
                System.out.println("Numero igual a "+ 0);
<<<<<<< HEAD
                return Try.of(()->0);
            }
        }
    }
=======
                return Option.of(0);
            }
        }
    }


>>>>>>> feature/PruebaSuite
}