package appconsole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class Entrada {
	private static final Scanner IN = new Scanner(System.in);
	private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);
	private static final String CANCELAR = ":cancelar";
	private static boolean entradaEncerrada;

	private Entrada() {
	}

	public static void reiniciar() {
		entradaEncerrada = false;
	}

	public static boolean encerrada() {
		return entradaEncerrada;
	}

	public static boolean cancelado(String valor) {
		return CANCELAR.equals(valor);
	}

	public static String ler(String prompt) {
		System.out.print(prompt + ": ");
		if (IN.hasNextLine()) {
			return IN.nextLine().trim();
		}
		entradaEncerrada = true;
		System.out.println("\nFim da entrada.");
		return CANCELAR;
	}

	public static String texto(String prompt) {
		while (true) {
			String s = ler(prompt);
			if (cancelado(s)) {
				return null;
			}
			if (!s.isEmpty()) {
				return s;
			}
			System.out.println("O campo não pode ficar vazio. Tente novamente ou digite :cancelar.");
		}
	}

	public static String textoOpcional(String prompt, String atual) {
		String s = ler(prompt + " (Enter mantém " + atual + ")");
		if (cancelado(s)) {
			return null;
		}
		return s.isEmpty() ? atual : s;
	}

	public static Integer inteiro(String prompt) {
		while (true) {
			String s = texto(prompt);
			if (s == null) {
				return null;
			}
			try {
				return Integer.valueOf(s);
			} catch (NumberFormatException e) {
				System.out.println("Número inválido; tente novamente.");
			}
		}
	}

	public static LocalDate dataPrompt(String prompt) {
		while (true) {
			String s = texto(prompt + " (dd/MM/aaaa)");
			if (s == null) {
				return null;
			}
			LocalDate d = data(s);
			if (d != null) {
				return d;
			}
			System.out.println("Data inválida; use dd/MM/aaaa e tente novamente.");
		}
	}

	public static LocalDate dataOpcional(String prompt, LocalDate atual) {
		while (true) {
			String s = ler(prompt + " (dd/MM/aaaa, Enter mantém " + atual.format(DATA) + ")");
			if (cancelado(s)) {
				return null;
			}
			if (s.isEmpty()) {
				return atual;
			}
			LocalDate d = data(s);
			if (d != null) {
				return d;
			}
			System.out.println("Data inválida; use dd/MM/aaaa e tente novamente.");
		}
	}

	public static LocalDate data(String s) {
		try {
			return LocalDate.parse(s, DATA);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	public static BigDecimal valorPrompt(String prompt) {
		while (true) {
			String s = texto(prompt);
			if (s == null) {
				return null;
			}
			BigDecimal v = dinheiro(s);
			if (v != null) {
				return v;
			}
			System.out.println("Valor inválido; tente novamente.");
		}
	}

	public static BigDecimal valorOpcional(String prompt, BigDecimal atual) {
		while (true) {
			String s = ler(prompt + " (Enter mantém " + atual.toPlainString() + ")");
			if (cancelado(s)) {
				return null;
			}
			if (s.isEmpty()) {
				return atual;
			}
			BigDecimal v = dinheiro(s);
			if (v != null) {
				return v;
			}
			System.out.println("Valor inválido; tente novamente.");
		}
	}

	public static BigDecimal dinheiro(String s) {
		try {
			String n = s.trim();
			if (n.contains(",") && n.contains(".")) {
				return null;
			}
			return new BigDecimal(n.replace(',', '.'));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static boolean confirmar(String prompt) {
		while (true) {
			String s = ler(prompt + " (s/n)");
			if (cancelado(s)) {
				return false;
			}
			if (s.equalsIgnoreCase("s") || s.equalsIgnoreCase("sim")) {
				return true;
			}
			if (s.equalsIgnoreCase("n") || s.equalsIgnoreCase("nao") || s.equalsIgnoreCase("não")) {
				return false;
			}
			System.out.println("Responda com s ou n.");
		}
	}
}
