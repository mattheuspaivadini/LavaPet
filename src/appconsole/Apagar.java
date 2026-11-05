package appconsole;

import java.util.ArrayList;
import java.util.List;
import com.db4o.ObjectContainer;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;

public class Apagar {

	public static void main(String[] args) {
		Consultar.iniciar(Apagar::executar);
	}

	public static void executar(ObjectContainer db) {
		Consultar.menu(db, "Apagar", "1 Tutor (com seus pets)\n2 Pet\n3 Serviço",
				Apagar::deletarTutor, Apagar::deletarPet, Apagar::deletarServico);
	}

	public static void deletarTutor(ObjectContainer db) {
		Tutor tutor = Consultar.escolherTutor(db);
		if (tutor == null || !confirmado("Excluir o tutor e todos os seus pets")) {
			return;
		}
		List<Pet> pets = new ArrayList<>(tutor.getListaPets());
		pets.forEach(pet -> excluirPet(db, pet));
		db.delete(tutor);
		db.commit();
		System.out.println("Tutor excluído: " + tutor.getNome() + " (" + pets.size() + " pet(s) removido(s))");
	}

	public static void deletarPet(ObjectContainer db) {
		Pet pet = Consultar.escolherPet(db);
		if (pet == null || !confirmado("Excluir o pet e remover seus vínculos")) {
			return;
		}
		excluirPet(db, pet);
		db.commit();
		System.out.println("Pet excluído: " + pet.getNome());
	}

	public static void deletarServico(ObjectContainer db) {
		Servico servico = Consultar.escolherServico(db);
		if (servico == null || !confirmado("Excluir o serviço e remover seus vínculos")) {
			return;
		}
		// pets alcancados pelo grafo do servico vem parcialmente ativados e nao removeriam o servico da propria lista
		db.activate(servico, Integer.MAX_VALUE);
		for (Pet pet : new ArrayList<>(servico.getListaPets())) {
			servico.removerPet(pet);
			db.store(pet);
		}
		db.delete(servico);
		db.commit();
		System.out.println("Serviço excluído: #" + servico.getId());
	}

	private static boolean confirmado(String prompt) {
		if (Consultar.confirmar(prompt)) {
			return true;
		}
		System.out.println("Operação cancelada.");
		return false;
	}

	// sem commit: quem chama confirma tudo de uma vez
	private static void excluirPet(ObjectContainer db, Pet pet) {
		// o pet pode vir parcialmente ativado (ex.: alcancado pelo grafo de um tutor/servico)
		db.activate(pet, Integer.MAX_VALUE);
		for (Servico servico : new ArrayList<>(pet.getListaServicos())) {
			servico.removerPet(pet);
			db.delete(servico);
		}
		Tutor tutor = pet.getTutor();
		tutor.removerPet(pet);
		db.store(tutor);
		db.delete(pet);
	}
}
