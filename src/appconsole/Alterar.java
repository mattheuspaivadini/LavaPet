package appconsole;

import java.math.BigDecimal;
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
		Consultar.menu(db, "Alterar", "1 Tutor\n2 Pet\n3 Serviço\n4 Remover vínculo Pet-Serviço",
				Alterar::alterarTutor, Alterar::alterarPet, Alterar::alterarServico, Alterar::removerVinculoPetServico);
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
		servico.getListaPets().forEach(p -> System.out.println("  #" + p.getId() + " " + p.getNome()));
		Integer id = Consultar.inteiro("Id do pet a desvincular");
		if (id == null) {
			return;
		}
		Pet pet = servico.getListaPets().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
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
		String nome = tutor == null ? null : Consultar.lerComo("Novo nome", tutor.getNome(), s -> s, null);
		if (nome == null) {
			return;
		}
		tutor.setNome(nome);
		db.store(tutor);
		db.commit();
		System.out.println("Tutor alterado: " + tutor);
	}

	public static void alterarPet(ObjectContainer db) {
		Pet pet = Consultar.escolherPet(db);
		String nome = pet == null ? null : Consultar.lerComo("Novo nome", pet.getNome(), s -> s, null);
		String raca = nome == null ? null : Consultar.lerComo("Nova raça", pet.getRaca(), s -> s, null);
		if (raca == null) {
			return;
		}
		Tutor antigo = pet.getTutor();
		Tutor novo = Consultar.confirmar("Deseja alterar o tutor") ? Consultar.escolherTutor(db) : antigo;
		if (novo == null || Consultar.encerrada()) {
			return;
		}
		pet.setNome(nome);
		pet.setRaca(raca);
		pet.setTutor(novo);
		db.store(antigo);
		db.store(pet);
		db.commit();
		System.out.println("Pet alterado: " + pet);
	}

	public static void alterarServico(ObjectContainer db) {
		Servico servico = Consultar.escolherServico(db);
		String data = servico == null ? null : Consultar.lerData("Nova data", servico.getData());
		BigDecimal valor = data == null ? null : Consultar.lerValor("Novo valor", servico.getValor());
		if (valor == null) {
			return;
		}
		List<Pet> pets = null;
		if (Consultar.confirmar("Deseja alterar os pets do serviço")) {
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
			antigos.forEach(servico::removerPet);
			pets.forEach(servico::adicionarPet);
		}
		db.store(servico);
		antigos.forEach(db::store); // pets desvinculados nao estao mais no cascade do servico
		db.commit();
		System.out.println("Serviço alterado: " + servico);
	}
}
