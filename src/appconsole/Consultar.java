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
import java.util.function.Predicate;
import com.db4o.ObjectContainer;
import util.Util;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;

public class Consultar {

	public static void main(String[] args) {
		iniciar(Consultar::executar);
	}

	public static void executar(ObjectContainer db) {
		boolean continuar = true;
		while (continuar && !encerrada()) {
			System.out.println("\n=== Consultar ===\n"
					+ "1 Listar tutores\n"
					+ "2 Listar pets\n"
					+ "3 Listar serviços\n"
					+ "4 Tutor por nome\n"
					+ "5 Pet por nome/raça/tutor\n"
					+ "6 Serviço por pet/data/intervalo/valor mínimo\n"
					+ "0 Voltar");
			String opcao = ler("Opção");
			switch (opcao) {
			case "1":
				listarTutores(db);
				break;
			case "2":
				listarPets(db);
				break;
			case "3":
				listarServicos(db);
				break;
			case "4":
				consultarTutor(db);
				break;
			case "5":
				consultaPet(db);
				break;
			case "6":
				consultaServico(db);
				break;
			case "0":
				continuar = false;
				break;
			default:
				System.out.println("Opção inválida.");
				break;
			}
		}
	}

	public static void listarTutores(ObjectContainer db) {
		System.out.println("------------ TUTORES ------------");
		List<Tutor> tutores = db.query(Tutor.class);
		if (tutores.isEmpty()) {
			System.out.println("Nenhum tutor cadastrado.");
		} else {
			for (Tutor t : tutores) {
				System.out.println(t);
			}
		}
	}

	public static void listarPets(ObjectContainer db) {
		System.out.println("\n------------ PETS ------------");
		List<Pet> pets = db.query(Pet.class);
		if (pets.isEmpty()) {
			System.out.println("Nenhum pet cadastrado.");
		} else {
			for (Pet p : pets) {
				System.out.println(p);
			}
		}
	}

	public static void listarServicos(ObjectContainer db) {
		System.out.println("\n------------ SERVICOS ------------");
		List<Servico> servicos = db.query(Servico.class);
		if (servicos.isEmpty()) {
			System.out.println("Nenhum serviço cadastrado.");
		} else {
			for (Servico s : servicos) {
				System.out.println(s);
			}
		}
	}

	public static Tutor buscarTutorPorId(ObjectContainer db, int id) {
		List<Tutor> encontrados = filtrar(db, Tutor.class, t -> t.getId() == id);
		return encontrados.isEmpty() ? null : encontrados.get(0);
	}

	public static Pet buscarPetPorId(ObjectContainer db, int id) {
		List<Pet> encontrados = filtrar(db, Pet.class, p -> p.getId() == id);
		return encontrados.isEmpty() ? null : encontrados.get(0);
	}

	public static Servico buscarServicoPorId(ObjectContainer db, int id) {
		List<Servico> encontrados = filtrar(db, Servico.class, s -> s.getId() == id);
		return encontrados.isEmpty() ? null : encontrados.get(0);
	}

	public static Tutor escolherTutor(ObjectContainer db) {
		listarTutores(db);
		if (db.query(Tutor.class).isEmpty()) {
			return null;
		}
		Integer id = inteiro("ID do tutor");
		while (id != null) {
			Tutor tutor = buscarTutorPorId(db, id);
			if (tutor != null) {
				return tutor;
			}
			System.out.println("Tutor não encontrado.");
			id = inteiro("ID do tutor");
		}
		return null;
	}

	public static Pet escolherPet(ObjectContainer db) {
		listarPets(db);
		if (db.query(Pet.class).isEmpty()) {
			return null;
		}
		Integer id = inteiro("ID do pet");
		while (id != null) {
			Pet pet = buscarPetPorId(db, id);
			if (pet != null) {
				return pet;
			}
			System.out.println("Pet não encontrado.");
			id = inteiro("ID do pet");
		}
		return null;
	}

	public static Servico escolherServico(ObjectContainer db) {
		listarServicos(db);
		if (db.query(Servico.class).isEmpty()) {
			return null;
		}
		Integer id = inteiro("ID do serviço");
		while (id != null) {
			Servico servico = buscarServicoPorId(db, id);
			if (servico != null) {
				return servico;
			}
			System.out.println("Serviço não encontrado.");
			id = inteiro("ID do serviço");
		}
		return null;
	}

	public static List<Pet> escolherPets(ObjectContainer db) {
		listarPets(db);
		if (db.query(Pet.class).isEmpty()) {
			System.out.println("Cadastre um pet antes de vincular serviços.");
			return null;
		}
		while (true) {
			String entrada = ler("IDs dos pets (separados por vírgula)");
			if (cancelado(entrada)) {
				return null;
			}
			if (entrada.isEmpty()) {
				System.out.println("Informe ao menos um pet.");
				continue;
			}
			List<Pet> pets = new ArrayList<>();
			boolean invalido = false;
			for (String parte : entrada.split(",")) {
				String valor = parte.trim();
				if (valor.isEmpty()) {
					continue;
				}
				Pet pet = null;
				try {
					pet = buscarPetPorId(db, Integer.parseInt(valor));
				} catch (NumberFormatException e) {
					pet = null;
				}
				if (pet == null) {
					System.out.println("Pet não encontrado: " + valor);
					invalido = true;
				} else if (!pets.contains(pet)) {
					pets.add(pet);
				}
			}
			if (invalido) {
				System.out.println("Corrija os IDs informados.");
				continue;
			}
			if (pets.isEmpty()) {
				System.out.println("Informe ao menos um pet.");
				continue;
			}
			return pets;
		}
	}

	private static void consultarTutor(ObjectContainer db) {
		String trecho = texto("Nome ou trecho");
		if (trecho == null) {
			return;
		}

		List<Tutor> encontrados = filtrar(db, Tutor.class, t -> contem(t.getNome(), trecho));
		resultados(encontrados);
	}

	private static void consultaPet(ObjectContainer db) {
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

	private static void consultarPetPorNome(ObjectContainer db, String nome) {
		List<Pet> encontrados = filtrar(db, Pet.class, p -> contem(p.getNome(), nome));
		resultados(encontrados);
	}

	private static void consultarPetPorRaca(ObjectContainer db, String raca) {
		List<Pet> encontrados = filtrar(db, Pet.class, p -> contem(p.getRaca(), raca));
		resultados(encontrados);
	}

	private static void consultarPetPorTutor(ObjectContainer db, String nomeTutor) {
		List<Pet> encontrados = filtrar(db, Pet.class,
				p -> p.getTutor() != null && contem(p.getTutor().getNome(), nomeTutor));
		resultados(encontrados);
	}

	private static void consultaServico(ObjectContainer db) {
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

	private static void consultarServicoPorPet(ObjectContainer db) {
		String nomePet = texto("Nome do pet");
		if (nomePet == null) {
			return;
		}

		List<Servico> encontrados = filtrar(db, Servico.class, s -> {
			for (Pet p : s.getListaPets()) {
				if (contem(p.getNome(), nomePet)) {
					return true;
				}
			}
			return false;
		});
		resultados(encontrados);
	}

	private static void consultarServicoPorData(ObjectContainer db) {
		String data = Cadastrar.dataPrompt("Data");
		if (data == null) {
			return;
		}

		List<Servico> encontrados = filtrar(db, Servico.class, s -> s.getData().equals(data));
		resultados(encontrados);
	}

	private static void consultarServicoPorIntervalo(ObjectContainer db) {
		LocalDate dataInicial = data(Cadastrar.dataPrompt("Data inicial"));
		LocalDate dataFinal = data(Cadastrar.dataPrompt("Data final"));
		if (dataInicial != null && dataFinal != null && !dataFinal.isBefore(dataInicial)) {
			List<Servico> encontrados = filtrar(db, Servico.class,
					s -> !data(s.getData()).isBefore(dataInicial) && !data(s.getData()).isAfter(dataFinal));
			resultados(encontrados);
		} else {
			System.out.println("Intervalo inválido.");
		}
	}

	private static void consultarServicoPorValorMinimo(ObjectContainer db) {
		BigDecimal valorMinimo = Cadastrar.valorPrompt("Valor mínimo");
		if (valorMinimo == null) {
			return;
		}

		List<Servico> encontrados = filtrar(db, Servico.class, s -> s.getValor().compareTo(valorMinimo) >= 0);
		resultados(encontrados);
	}

	private static <T> List<T> filtrar(ObjectContainer db, Class<T> tipo, Predicate<T> filtro) {
		List<T> resultado = new ArrayList<>();
		for (T item : db.query(tipo)) {
			if (filtro.test(item)) {
				resultado.add(item);
			}
		}
		return resultado;
	}

	private static boolean contem(String s, String trecho) {
		return s != null && s.toLowerCase().contains(trecho.toLowerCase());
	}

	private static void mostrar(List<?> itens) {
		for (Object o : itens) {
			System.out.println(o);
		}
	}

	// --- leitura do teclado: valida a entrada antes de qualquer acesso ao banco ---
	private static final Scanner IN = new Scanner(System.in);
	static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);
	private static final String CANCELAR = ":cancelar";
	private static boolean entradaEncerrada;

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

	static LocalDate data(String s) {
		try {
			return LocalDate.parse(s, DATA);
		} catch (DateTimeParseException | NullPointerException e) {
			return null;
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
