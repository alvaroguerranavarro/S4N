package co.com.s4n.training.java.jdk;
import io.vavr.control.Option;
import io.vavr.control.Try;


public class Prueba {

    public static Try<Integer> multiplicarTry (int a, int b)
    {
        System.out.println("Multiplicando"+ a + "*" + b + "="+a*b);
        return Try.of(() -> a*b);

    }

    public static Try<Integer> compararTry (int a, int b)
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

    public static Try<Integer> divisionTry (int a, int b)
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

    public static Try<Integer> postivoTry (int a)
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

    public static Option<Integer> multiplicarOption (int a, int b)
    {
        System.out.println("Multiplicando"+ a + "*" + b + "="+a*b);
        return Option.of(a*b);
    }

    public static Option<Integer> compararOption (int a, int b)
    {
        if (a>b)
        {
            System.out.println("Comparar"+ a + ">" + b);
            return Option.of(a);
        }
        else
        {
            System.out.println("Comparar"+ b + ">" + a);
            return Option.of(b);
        }
    }

    public static Option<Integer> divisionOption (int a, int b)
    {
        //System.out.println("Dividiendo"+ a + "/" + b + "= "+a/b);
        if (b!=0)
        {
            System.out.println("Dividiendo"+ a + "/" + b + "= "+a/b);
            return Option.of(a/b);
        }
        else
        {
            return Option.of(-1);
        }

    }

    public static Option<Integer> postivoOption (int a)
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