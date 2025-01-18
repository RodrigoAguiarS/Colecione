package com.rodrigo.colecione.util;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
public class CurrencyUtil {

    // Instância do NumberFormat para moeda brasileira
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    /**
     * Formata um valor monetário em BRL (Real brasileiro).
     *
     * @param value o valor a ser formatado
     * @return uma string com o valor formatado
     */
    public static String formatToBRL(double value) {
        return CURRENCY_FORMAT.format(value);
    }

    public static String formatToBRL(BigDecimal value) {
        return CURRENCY_FORMAT.format(value);
    }

    /**
     * Remove a formatação de moeda e retorna o valor como número.
     *
     * @param formattedValue o valor formatado (ex: "R$ 1.234,56")
     * @return o valor como número (double)
     * @throws NumberFormatException se o valor formatado for inválido
     */
    public static double parseFromBRL(String formattedValue) throws NumberFormatException {
        try {
            String numericValue = formattedValue
                    .replace("R$", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .trim();
            return Double.parseDouble(numericValue);
        } catch (Exception e) {
            throw new NumberFormatException("Valor formatado inválido: " + formattedValue);
        }
    }
}
