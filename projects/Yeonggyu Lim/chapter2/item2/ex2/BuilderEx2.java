package chapter2.item2.ex2;

public class BuilderEx2 {
    public static void main(String[] args) {
        NyPizza nyPizza = new NyPizza.Builder(NyPizza.Size.LARGE)
                .addTopping(Pizza.Topping.SAUSAGE)
                .addTopping(Pizza.Topping.ONION)
                .build();

        Calzone calzone = new Calzone.Builder()
                .addTopping(Pizza.Topping.HAM)
                .sauceInside()
                .build();

        System.out.println(nyPizza.toString() + " " + nyPizza.getSize());
        System.out.println(calzone.toString() + " " + calzone.isSauceInside());
    }
}
