package appconsole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import com.db4o.ObjectContainer;
import com.db4o.query.Query;
import util.Util;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;

public class Consultar {

	public static void main(String[] args) {
		iniciar(Consultar::executar);
	}

	public static void executar(ObjectContainer db) {
		menu(db, "Consultar", """
				1 Listar tutores
				2 Listar pets
				3 Listar serviços
				4 Tutor por nome
				5 Pet por nome/raça/tutor
				6 Serviço por pet/data/intervalo/valor mínimo""",
				Consultar::listarTutores, Consultar::listarPets, Consultar::listarServicos,
				Consultar::consultarTutor, Consultar::consultarPet, Consultar::consultarServico);
	}

	public static void listarTutores(ObjectContainer db) {
		listar(db, Tutor.class, "TUTORES", "tutor");
	}

	public static void listarPets(ObjectContainer db) {
		listar(db, Pet.class, "PETS", "pet");
	}

	public static void listarServicos(ObjectContainer db) {
		listar(db, Servico.class, "SERVICOS", "serviço");
	}

	public static Tutor escolherTutor(ObjectContainer db) {
		return escolher(db, Tutor.class, "TUTORES", "tutor");
	}

	public static Pet escolherPet(ObjectContainer db) {
		return escolher(db, Pet.class, "PETS", "pet");
	}

	public static Servico escolherServico(ObjectContainer db) {
		return escolher(db, Servico.class, "SERVICOS", "serviço");
	}

	public static List<Pet> escolherPets(ObjectContainer db) {
		if (listar(db, Pet.class, "PETS", "pet").isEmpty()) {
			System.out.println("Cadastre um pet antes de vincular serviços.");
			return null;
		}
		while (true) {
			String entrada = ler("IDs dos pets (separados por vírgula)");
			if (cancelado(entrada)) {
				return null;
			}
			List<Pet> pets = new ArrayList<>();
			boolean invalido = false;
			for (String parte : entrada.split(",")) {
				String valor = parte.trim();
				if (valor.isEmpty()) {
					continue;
				}
				Pet pet = valor.matches("\\d{1,9}") ? buscarPorId(db, Pet.class, Integer.parseInt(valor)) : null;
				if (pet == null) {
					System.out.println("Pet não encontrado: " + valor);
					invalido = true;
				} else if (!pets.contains(pet)) {
					pets.add(pet);
				}
			}
			if (invalido) {
				System.out.println("Corrija os IDs informados.");
			} else if (pets.isEmpty()) {
				System.out.println("Informe ao menos um pet.");
			} else {
				return pets;
			}
		}
	}

	private static void consultarTutor(ObjectContainer db) {
		String trecho = texto("Nome ou trecho");
		if (trecho != null) {
			mostrar(db, Tutor.class, t -> contem(t.getNome(), trecho));
		}
	}

	private static void consultarPet(ObjectContainer db) {
		System.out.println("1 Nome | 2 Raça | 3 Tutor (trecho do nome)");
		Function<Pet, String> campo = switch (ler("Critério")) {
		case "1" -> Pet::getNome;
		case "2" -> Pet::getRaca;
		case "3" -> p -> p.getTutor() == null ? null : p.getTutor().getNome();
		default -> null;
		};
		if (campo == null) {
			System.out.println("Opção inválida.");
			return;
		}
		String valor = texto("Valor");
		if (valor != null) {
			mostrar(db, Pet.class, p -> contem(campo.apply(p), valor));
		}
	}

	private static void consultarServico(ObjectContainer db) {
		System.out.println("1 Pet (trecho do nome) | 2 Data | 3 Intervalo de datas | 4 Valor mínimo");
		switch (ler("Critério")) {
		case "1" -> {
			String nomePet = texto("Nome do pet");
			if (nomePet != null) {
				mostrar(db, Servico.class, s -> s.getListaPets().stream().anyMatch(p -> contem(p.getNome(), nomePet)));
			}
		}
		case "2" -> {
			String data = lerData("Data", null);
			if (data != null) {
				mostrar(db, Servico.class, s -> s.getData().equals(data));
			}
		}
		case "3" -> {
			LocalDate inicio = data(lerData("Data inicial", null));
			LocalDate fim = data(lerData("Data final", null));
			if (inicio != null && fim != null && !fim.isBefore(inicio)) {
				mostrar(db, Servico.class, s -> !data(s.getData()).isBefore(inicio) && !data(s.getData()).isAfter(fim));
			} else {
				System.out.println("Intervalo inválido.");
			}
		}
		case "4" -> {
			BigDecimal minimo = lerValor("Valor mínimo", null);
			if (minimo != null) {
				mostrar(db, Servico.class, s -> s.getValor().compareTo(minimo) >= 0);
			}
		}
		default -> System.out.println("Opção inválida.");
		}
	}

	// --- acesso generico ao banco (Tutor, Pet e Servico tem o campo "id") ---
	private static <T> List<T> listar(ObjectContainer db, Class<T> tipo, String titulo, String nome) {
		System.out.println("\n------------ " + titulo + " ------------");
		List<T> itens = db.query(tipo);
		if (itens.isEmpty()) {
			System.out.println("Nenhum " + nome + " cadastrado.");
		}
		itens.forEach(System.out::println);
		return itens;
	}

	private static <T> T escolher(ObjectContainer db, Class<T> tipo, String titulo, String nome) {
		if (listar(db, tipo, titulo, nome).isEmpty()) {
			return null;
		}
		for (Integer id = inteiro("ID do " + nome); id != null; id = inteiro("ID do " + nome)) {
			T item = buscarPorId(db, tipo, id);
			if (item != null) {
				return item;
			}
			System.out.println("ID " + id + " não encontrado.");
		}
		return null;
	}

	static <T> T buscarPorId(ObjectContainer db, Class<T> tipo, int id) {
		Query q = db.query();
		q.constrain(tipo);
		q.descend("id").constrain(id);
		List<T> encontrados = q.execute();
		return encontrados.isEmpty() ? null : encontrados.get(0);
	}

	private static <T> void mostrar(ObjectContainer db, Class<T> tipo, Predicate<T> filtro) {
		List<T> encontrados = db.query(tipo).stream().filter(filtro).toList();
		if (encontrados.isEmpty()) {
			System.out.println("Nenhum resultado encontrado.");
		}
		encontrados.forEach(System.out::println);
	}

	private static boolean contem(String s, String trecho) {
		return s != null && s.toLowerCase().contains(trecho.toLowerCase());
	}

	// --- ciclo de vida e menus ---
	static void iniciar(Consumer<ObjectContainer> operacao) {
		Util.conectar();
		try {
			operacao.accept(Util.getManager());
		} catch (Exception e) {
			System.out.println("ERRO: " + e.getMessage());
			e.printStackTrace(System.out);
		} finally {
			Util.desconectar();
		}
	}

	// opcoes: texto exibido, numerado de 1 em diante na mesma ordem de acoes
	@SafeVarargs
	static void menu(ObjectContainer db, String titulo, String opcoes, Consumer<ObjectContainer>... acoes) {
		while (!encerrada()) {
			System.out.println("\n=== " + titulo + " ===\n" + opcoes + "\n0 Voltar");
			String op = ler("Opção");
			if (op.equals("0")) {
				return;
			}
			int i = op.matches("[1-9]") ? op.charAt(0) - '1' : -1;
			if (i >= 0 && i < acoes.length) {
				acoes[i].accept(db);
			} else {
				System.out.println("Opção inválida.");
			}
		}
	}

	// --- leitura do teclado: valida a entrada antes de qualquer acesso ao banco ---
	private static final Scanner IN = new Scanner(System.in);
	private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);
	private static final String CANCELAR = ":cancelar";
	private static boolean entradaEncerrada;

	static boolean encerrada() {
		return entradaEncerrada;
	}

	static boolean cancelado(String valor) {
		return CANCELAR.equals(valor);
	}

	static String ler(String prompt) {
		System.out.print(prompt + ": ");
		if (IN.hasNextLine()) {
			return IN.nextLine().trim();
		}
		entradaEncerrada = true;
		System.out.println("\nFim da entrada.");
		return CANCELAR;
	}

	static String texto(String prompt) {
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

	static Integer inteiro(String prompt) {
		return lerComo(prompt, null, s -> s.matches("-?\\d{1,9}") ? Integer.valueOf(s) : null,
				"Número inválido; tente novamente.");
	}

	// atual == null: campo obrigatorio; senao Enter mantem o atual. null = cancelado
	static <T> T lerComo(String prompt, T atual, Function<String, T> conversor, String erro) {
		while (true) {
			String s = atual == null ? texto(prompt) : ler(prompt + " (Enter mantém " + atual + ")");
			if (s == null || cancelado(s)) {
				return null;
			}
			if (s.isEmpty()) {
				return atual;
			}
			T valor = conversor.apply(s);
			if (valor != null) {
				return valor;
			}
			System.out.println(erro);
		}
	}

	static String lerData(String prompt, String atual) {
		return lerComo(prompt + " (dd/MM/aaaa)", atual, s -> {
			LocalDate d = data(s);
			return d == null ? null : d.format(DATA);
		}, "Data inválida; use dd/MM/aaaa e tente novamente.");
	}

	static BigDecimal lerValor(String prompt, BigDecimal atual) {
		return lerComo(prompt, atual, Consultar::dinheiro, "Valor inválido; tente novamente.");
	}

	// confirmacao antes de qualquer remocao/alteracao destrutiva
	static boolean confirmar(String prompt) {
		while (true) {
			String s = ler(prompt + " (s/n)").toLowerCase();
			if (cancelado(s) || s.equals("n") || s.equals("nao") || s.equals("não")) {
				return false;
			}
			if (s.equals("s") || s.equals("sim")) {
				return true;
			}
			System.out.println("Responda com s ou n.");
		}
	}

	static LocalDate data(String s) {
		try {
			return LocalDate.parse(s, DATA);
		} catch (DateTimeParseException | NullPointerException e) {
			return null;
		}
	}

	static BigDecimal dinheiro(String s) {
		if (s.contains(",") && s.contains(".")) {
			return null;
		}
		try {
			return new BigDecimal(s.trim().replace(',', '.'));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
