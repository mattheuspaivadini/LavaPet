package appconsole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.db4o.ObjectContainer;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;

public class Alterar {

	public static void main(String[] args) {
		Consultar.iniciar(Alterar::executar);
	}

	public static void executar(ObjectContainer db) {
		boolean continuar = true;
		while (continuar && !Consultar.encerrada()) {
			System.out.println("\n=== Alterar ===\n"
					+ "1 Tutor\n"
					+ "2 Pet\n"
					+ "3 Serviço\n"
					+ "4 Remover vínculo Pet-Serviço\n"
					+ "0 Voltar");
			String opcao = Consultar.ler("Opção");
			switch (opcao) {
			case "1":
				alterarTutor(db);
				break;
			case "2":
				alterarPet(db);
				break;
			case "3":
				alterarServico(db);
				break;
			case "4":
				removerVinculoPetServico(db);
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

	public static void removerVinculoPetServico(ObjectContainer db) {
		Servico servico = Consultar.escolherServico(db);
		if (servico == null) {
			return;
		}
		if (servico.getListaPets().isEmpty()) {
			System.out.println("Este serviço não tem pets vinculados.");
			return;
		}
		System.out.println("Pets do serviço:");
		for (Pet p : servico.getListaPets()) {
			System.out.println("  #" + p.getId() + " " + p.getNome());
		}
		Integer id = Consultar.inteiro("Id do pet a desvincular");
		if (id == null) {
			return;
		}
		Pet pet = null;
		for (Pet p : servico.getListaPets()) {
			if (p.getId() == id) {
				pet = p;
			}
		}
		if (pet == null) {
			System.out.println("Pet não está vinculado a este serviço.");
			return;
		}

		servico.removerPet(pet);
		db.store(servico);
		db.store(pet);
		db.commit();
		System.out.println("Vínculo removido: " + pet.getNome() + " <-> serviço #" + servico.getId());
	}

	public static void alterarTutor(ObjectContainer db) {
		Tutor tutor = Consultar.escolherTutor(db);
		if (tutor == null) {
			return;
		}

		String nome = textoOpcional("Novo nome", tutor.getNome());
		if (nome == null || Consultar.encerrada()) {
			return;
		}

		tutor.setNome(nome);
		db.store(tutor);
		db.commit();
		System.out.println("Tutor alterado: " + tutor);
	}

	public static void alterarPet(ObjectContainer db) {
		Pet pet = Consultar.escolherPet(db);
		if (pet == null) {
			return;
		}

		String nome = textoOpcional("Novo nome", pet.getNome());
		if (nome == null) {
			return;
		}
		String raca = textoOpcional("Nova raça", pet.getRaca());
		if (raca == null) {
			return;
		}

		Tutor novoTutor = pet.getTutor();
		if (Apagar.confirmar("Deseja alterar o tutor")) {
			novoTutor = Consultar.escolherTutor(db);
			if (novoTutor == null) {
				return;
			}
		}
		if (Consultar.encerrada()) {
			return;
		}

		Tutor antigo = pet.getTutor();
		pet.setNome(nome);
		pet.setRaca(raca);
		pet.setTutor(novoTutor);
		db.store(antigo);
		db.store(pet);
		db.commit();
		System.out.println("Pet alterado: " + pet);
	}

	public static void alterarServico(ObjectContainer db) {
		Servico servico = Consultar.escolherServico(db);
		if (servico == null) {
			return;
		}

		String data = dataOpcional("Nova data", servico.getData());
		if (data == null) {
			return;
		}
		BigDecimal valor = valorOpcional("Novo valor", servico.getValor());
		if (valor == null) {
			return;
		}

		List<Pet> pets = null;
		if (Apagar.confirmar("Deseja alterar os pets do serviço")) {
			pets = Consultar.escolherPets(db);
			if (pets == null) {
				return;
			}
		}
		if (Consultar.encerrada()) {
			return;
		}

		servico.setData(data);
		servico.setValor(valor);
		db.activate(servico, Integer.MAX_VALUE); // ver Apagar.deletarServico
		List<Pet> antigos = new ArrayList<>(servico.getListaPets());
		if (pets != null) {
			for (Pet pet : antigos) {
				servico.removerPet(pet);
			}
			for (Pet pet : pets) {
				servico.adicionarPet(pet);
			}
		}
		db.store(servico);
		for (Pet pet : antigos) { // pets desvinculados nao estao mais no cascade do servico
			db.store(pet);
		}
		db.commit();
		System.out.println("Serviço alterado: " + servico);
	}

	//validacao de entrada da alteracao
	static String textoOpcional(String prompt, String atual) {
		String s = Consultar.ler(prompt + " (Enter mantém " + atual + ")");
		if (Consultar.cancelado(s)) {
			return null;
		}
		return s.isEmpty() ? atual : s;
	}

	static String dataOpcional(String prompt, String atual) {
		while (true) {
			String s = Consultar.ler(prompt + " (dd/MM/aaaa, Enter mantém " + atual + ")");
			if (Consultar.cancelado(s)) {
				return null;
			}
			if (s.isEmpty()) {
				return atual;
			}
			LocalDate d = Consultar.data(s);
			if (d != null) {
				return d.format(Consultar.DATA);
			}
			System.out.println("Data inválida; use dd/MM/aaaa e tente novamente.");
		}
	}

	static BigDecimal valorOpcional(String prompt, BigDecimal atual) {
		while (true) {
			String s = Consultar.ler(prompt + " (Enter mantém " + atual.toPlainString() + ")");
			if (Consultar.cancelado(s)) {
				return null;
			}
			if (s.isEmpty()) {
				return atual;
			}
			BigDecimal v = Cadastrar.dinheiro(s);
			if (v != null) {
				return v;
			}
			System.out.println("Valor inválido; tente novamente.");
		}
	}
}
