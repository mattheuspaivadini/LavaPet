package appconsole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;
import util.BancoMemoria;
import util.Util;

public class Demonstracao {
	private static final Scanner IN = new Scanner(System.in);
	private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);
	private static final String CANCELAR = ":cancelar";
	private static boolean entradaEncerrada;

	public static void main(String[] args) {
		Util.executar(Demonstracao::executar);
	}

	public static void executar(BancoMemoria db) {
		exibirExemplosDiretos(db);
		menu(db);
	}

	public static void exibirExemplosDiretos(BancoMemoria db) {
		System.out.println("SISTEMA LAVAPET");

		System.out.println("1. TUTORES");
		for (Tutor t : db.getTutores()) {
			System.out.println("• " + t);
			for (Pet p : t.getListaPets()) {
				System.out.println("    └─ Pet: " + p.getNome() + " | Raça: " + p.getRaca());
			}
		}

		System.out.println("\n2. PETS");
		for (Pet p : db.getPets()) {
			System.out.println("• " + p);
			for (Servico s : p.getListaServicos()) {
				System.out.printf("    └─ Serviço #%d | Data: %s | Valor: R$ %.2f%n",
						s.getId(), s.getData().format(DATA), s.getValor());
			}
		}

		System.out.println("\n3. SERVIÇOS");
		BigDecimal totalFaturado = BigDecimal.ZERO;
		for (Servico s : db.getServicos()) {
			System.out.printf("• Serviço #%d | Data: %s | Valor: R$ %.2f%n",
					s.getId(), s.getData().format(DATA), s.getValor());
			totalFaturado = totalFaturado.add(s.getValor());
			for (Pet p : s.getListaPets()) {
				String nomeTutor = (p.getTutor() != null) ? p.getTutor().getNome() : "Sem tutor";
				System.out.println("    └─ Pet atendido: " + p.getNome() + " (" + p.getRaca() + ") | Tutor: " + nomeTutor);
			}
		}

		System.out.println("\nRESUMO GERAL DO LAVAPET");
		System.out.println("Total de Tutores:  " + db.getTutores().size());
		System.out.println("Total de Pets:     " + db.getPets().size());
		System.out.println("Total de Serviços: " + db.getServicos().size());
		System.out.printf("Faturamento Total: R$ %.2f%n", totalFaturado);
	}

	public static void menu(BancoMemoria db) {
		entradaEncerrada = false;
		boolean continuar = true;
		while (continuar) {
			exibirMenuPrincipal();
			String opcao = ler("Opção");
			switch (opcao) {
			case "1":
				exibirExemplosDiretos(db);
				break;
			case "2":
				Listar.executar(db);
				break;
			case "3":
				consultar(db);
				break;
			case "0":
				continuar = false;
				break;
			default:
				System.out.println("Opção inválida. Escolha uma opção do menu.");
				break;
			}
			if (entradaEncerrada) {
				continuar = false;
			}
		}
		System.out.println("Até logo.");
	}

	private static void exibirMenuPrincipal() {
		System.out.println("=== LavaPet - Menu ===\n"
				+ "1 Exibir exemplos completos\n"
				+ "2 Listagem simples\n"
				+ "3 Consultar dados\n"
				+ "0 Sair");
	}

	private static void consultar(BancoMemoria db) {
		System.out.println("1 Tutor por nome | 2 Pet por nome/raça/tutor | 3 Serviço por pet/data/intervalo/valor mínimo | 0 Voltar");
		String opcao = ler("Consulta");
		switch (opcao) {
		case "1":
			consultarTutor(db);
			break;
		case "2":
			consultaPet(db);
			break;
		case "3":
			consultaServico(db);
			break;
		case "0":
			break;
		default:
			System.out.println("Opção inválida.");
			break;
		}
	}

	private static void consultarTutor(BancoMemoria db) {
		String trecho = texto("Nome ou trecho");
		if (trecho == null) {
			return;
		}

		List<Tutor> encontrados = db.query(Tutor.class, t -> contem(t.getNome(), trecho));
		resultados(encontrados);
	}

	private static void consultaPet(BancoMemoria db) {
		System.out.println("1 Nome | 2 Raça | 3 Tutor (trecho do nome)");
		String op = ler("Critério");
		String criterio = texto("Valor");
		if (criterio == null) {
			return;
		}

		if (op.equals("1")) {
			consultarPetPorNome(db, criterio);
		} else if (op.equals("2")) {
			consultarPetPorRaca(db, criterio);
		} else if (op.equals("3")) {
			consultarPetPorTutor(db, criterio);
		} else {
			System.out.println("Opção inválida.");
		}
	}

	private static void consultarPetPorNome(BancoMemoria db, String nome) {
		List<Pet> encontrados = db.query(Pet.class, p -> contem(p.getNome(), nome));
		resultados(encontrados);
	}

	private static void consultarPetPorRaca(BancoMemoria db, String raca) {
		List<Pet> encontrados = db.query(Pet.class, p -> contem(p.getRaca(), raca));
		resultados(encontrados);
	}

	private static void consultarPetPorTutor(BancoMemoria db, String nomeTutor) {
		List<Pet> encontrados = db.query(Pet.class, p -> p.getTutor() != null && contem(p.getTutor().getNome(), nomeTutor));
		resultados(encontrados);
	}

	private static void consultaServico(BancoMemoria db) {
		System.out.println("1 Pet (trecho do nome) | 2 Data | 3 Intervalo de datas | 4 Valor mínimo");
		String op = ler("Critério");
		if (op.equals("1")) {
			consultarServicoPorPet(db);
		} else if (op.equals("2")) {
			consultarServicoPorData(db);
		} else if (op.equals("3")) {
			consultarServicoPorIntervalo(db);
		} else if (op.equals("4")) {
			consultarServicoPorValorMinimo(db);
		} else {
			System.out.println("Opção inválida.");
		}
	}

	private static void consultarServicoPorPet(BancoMemoria db) {
		String nomePet = texto("Nome do pet");
		if (nomePet == null) {
			return;
		}

		List<Servico> encontrados = db.query(Servico.class, s -> {
			for (Pet p : s.getListaPets()) {
				if (contem(p.getNome(), nomePet)) {
					return true;
				}
			}
			return false;
		});
		resultados(encontrados);
	}

	private static void consultarServicoPorData(BancoMemoria db) {
		LocalDate data = dataPrompt("Data");
		if (data == null) {
			return;
		}

		List<Servico> encontrados = db.query(Servico.class, s -> s.getData().equals(data));
		resultados(encontrados);
	}

	private static void consultarServicoPorIntervalo(BancoMemoria db) {
		LocalDate dataInicial = dataPrompt("Data inicial");
		LocalDate dataFinal = dataPrompt("Data final");
		if (dataInicial != null && dataFinal != null && !dataFinal.isBefore(dataInicial)) {
			List<Servico> encontrados = db.query(Servico.class,
					s -> !s.getData().isBefore(dataInicial) && !s.getData().isAfter(dataFinal));
			resultados(encontrados);
		} else {
			System.out.println("Intervalo inválido.");
		}
	}

	private static void consultarServicoPorValorMinimo(BancoMemoria db) {
		BigDecimal valorMinimo = valorPrompt("Valor mínimo");
		if (valorMinimo == null) {
			return;
		}

		List<Servico> encontrados = db.query(Servico.class, s -> s.getValor().compareTo(valorMinimo) >= 0);
		resultados(encontrados);
	}

	private static String ler(String prompt) {
		System.out.print(prompt + ": ");
		if (IN.hasNextLine()) {
			return IN.nextLine().trim();
		}
		entradaEncerrada = true;
		System.out.println("\nFim da entrada.");
		return CANCELAR;
	}

	private static String texto(String prompt) {
		while (true) {
			String s = ler(prompt);
			if (s.equals(CANCELAR)) {
				return null;
			}
			if (!s.trim().isEmpty()) {
				return s.trim();
			}
			System.out.println("O campo não pode ficar vazio. Tente novamente ou digite :cancelar.");
		}
	}

	private static LocalDate dataPrompt(String prompt) {
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

	private static LocalDate data(String s) {
		try {
			return LocalDate.parse(s, DATA);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	private static BigDecimal valorPrompt(String prompt) {
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

	private static BigDecimal dinheiro(String s) {
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

	private static boolean contem(String s, String trecho) {
		return s != null && s.toLowerCase().contains(trecho.toLowerCase());
	}

	private static void mostrar(List<?> itens) {
		for (Object o : itens) {
			System.out.println(o);
		}
	}

	private static void resultados(List<?> itens) {
		if (itens.isEmpty()) {
			System.out.println("Nenhum resultado encontrado.");
		} else {
			mostrar(itens);
		}
	}
}
