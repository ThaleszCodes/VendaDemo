package com.atos.vendademo;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
public final class Money {
    public static String format(String input) {
        String s = input.trim();
        if (!s.matches("[0-9]{1,7}([,.][0-9]{1,2})?")) throw new IllegalArgumentException("Use um valor como 7,87, sem separador de milhares.");
        BigDecimal value = new BigDecimal(s.replace(',', '.'));
        if (value.signum() <= 0) throw new IllegalArgumentException("Insira um valor maior que zero.");
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(value).replace('\u00a0', ' ');
    }
    private Money() {}
}
