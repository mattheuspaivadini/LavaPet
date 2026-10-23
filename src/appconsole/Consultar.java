package appconsole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;
import util.BancoMemoria;
import util.Util;

public class Consultar {

	public static void executar(BancoMemoria db) {
		boolean continuar = true;
		while (continuar && !Util.encerrada()) {
			System.out.println("\n=== Consultar ===\n"
					+ "1 Listar tutores\n"
					+ "2 Listar pets\n"
					+ "3 Listar serviços\n"
					+ "4 Tutor por nome\n"
					+ "5 Pet por nome/raça/tutor\n"
					+ "6 Serviço por pet/data/intervalo/valor mínimo\n"
					+ "0 Voltar");
			String opcao = Util.ler("Opção");
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

	public static void listarTutores(BancoMemoria db) {
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

	public static void listarPets(BancoMemoria db) {
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

	public static void listarServicos(BancoMemoria db) {
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

	public static Tutor buscarTutorPorId(BancoMemoria db, int id) {
		List<Tutor> encontrados = db.query(Tutor.class, t -> t.getId() == id);
		return encontrados.isEmpty() ? null : encontrados.get(0);
	}

	public static Pet buscarPetPorId(BancoMemoria db, int id) {
		List<Pet> encontrados = db.query(Pet.class, p -> p.getId() == id);
		return encontrados.isEmpty() ? null : encontrados.get(0);
	}

	public static Servico buscarServicoPorId(BancoMemoria db, int id) {
		List<Servico> encontrados = db.query(Servico.class, s -> s.getId() == id);
		return encontrados.isEmpty() ? null : encontrados.get(0);
	}

	public static Tutor escolherTutor(BancoMemoria db) {
		listarTutores(db);
		if (db.query(Tutor.class).isEmpty()) {
			return null;
		}
		Integer id = Util.inteiro("ID do tutor");
		while (id != null) {
			Tutor tutor = buscarTutorPorId(db, id);
			if (tutor != null) {
				return tutor;
			}
			System.out.println("Tutor não encontrado.");
			id = Util.inteiro("ID do tutor");
		}
		return null;
	}

	public static Pet escolherPet(BancoMemoria db) {
		listarPets(db);
		if (db.query(Pet.class).isEmpty()) {
			return null;
		}
		Integer id = Util.inteiro("ID do pet");
		while (id != null) {
			Pet pet = buscarPetPorId(db, id);
			if (pet != null) {
				return pet;
			}
			System.out.println("Pet não encontrado.");
			id = Util.inteiro("ID do pet");
		}
		return null;
	}

	public static Servico escolherServico(BancoMemoria db) {
		listarServicos(db);
		if (db.query(Servico.class).isEmpty()) {
			return null;
		}
		Integer id = Util.inteiro("ID do serviço");
		while (id != null) {
			Servico servico = buscarServicoPorId(db, id);
			if (servico != null) {
				return servico;
			}
			System.out.println("Serviço não encontrado.");
			id = Util.inteiro("ID do serviço");
		}
		return null;
	}

	public static List<Pet> escolherPets(BancoMemoria db) {
		listarPets(db);
		if (db.query(Pet.class).isEmpty()) {
			System.out.println("Cadastre um pet antes de vincular serviços.");
			return null;
		}
		while (true) {
			String entrada = Util.ler("IDs dos pets (separados por vírgula)");
			if (Util.cancelado(entrada)) {
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

	private static void consultarTutor(BancoMemoria db) {
		String trecho = Util.texto("Nome ou trecho");
		if (trecho == null) {
			return;
		}

		List<Tutor> encontrados = db.query(Tutor.class, t -> contem(t.getNome(), trecho));
		resultados(encontrados);
	}

	private static void consultaPet(BancoMemoria db) {
		System.out.println("1 Nome | 2 Raça | 3 Tutor (trecho do nome)");
		String op = Util.ler("Critério");
		String criterio = Util.texto("Valor");
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
		List<Pet> encontrados = db.query(Pet.class,
				p -> p.getTutor() != null && contem(p.getTutor().getNome(), nomeTutor));
		resultados(encontrados);
	}

	private static void consultaServico(BancoMemoria db) {
		System.out.println("1 Pet (trecho do nome) | 2 Data | 3 Intervalo de datas | 4 Valor mínimo");
		String op = Util.ler("Critério");
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
		String nomePet = Util.texto("Nome do pet");
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
		LocalDate data = Util.dataPrompt("Data");
		if (data == null) {
			return;
		}

		List<Servico> encontrados = db.query(Servico.class, s -> s.getData().equals(data));
		resultados(encontrados);
	}

	private static void consultarServicoPorIntervalo(BancoMemoria db) {
		LocalDate dataInicial = Util.dataPrompt("Data inicial");
		LocalDate dataFinal = Util.dataPrompt("Data final");
		if (dataInicial != null && dataFinal != null && !dataFinal.isBefore(dataInicial)) {
			List<Servico> encontrados = db.query(Servico.class,
					s -> !s.getData().isBefore(dataInicial) && !s.getData().isAfter(dataFinal));
			resultados(encontrados);
		} else {
			System.out.println("Intervalo inválido.");
		}
	}

	private static void consultarServicoPorValorMinimo(BancoMemoria db) {
		BigDecimal valorMinimo = Util.valorPrompt("Valor mínimo");
		if (valorMinimo == null) {
			return;
		}

		List<Servico> encontrados = db.query(Servico.class, s -> s.getValor().compareTo(valorMinimo) >= 0);
		resultados(encontrados);
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
