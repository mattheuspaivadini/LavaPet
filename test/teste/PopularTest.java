package teste;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.db4o.ObjectContainer;

import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;
import util.Util;

public class PopularTest {
	static final String[] TUTORES = { "Ana", "Bruno", "Carla", "Diego", "Elisa" };
	static final String[][] PETS = { { "Bob", "Beagle", "2" }, { "Thor", "Labrador", "1" },
			{ "Rex", "Vira-lata", "0" }, { "Max", "Pug", "4" }, { "Luna", "Siames", "1" },
			{ "Mel", "Poodle", "0" }, { "Nina", "Persa", "3" } };
	static final int[][] SERVICOS = { { 0, 1, 2 }, { 3, 4 }, { 5, 6 }, { 0, 3, 5 }, { 1, 2 }, { 4, 6, 0 }, { 2, 5 },
			{ 1, 3, 6 }, { 0 }, { 4 } };

	public static void main(String[] args) {
		Util.conectar();
		ObjectContainer db = Util.getManager();
		try {
			for (Object o : db.query(Object.class))
				db.delete(o);
			db.commit();

			Tutor[] tutores = new Tutor[TUTORES.length];
			for (int i = 0; i < TUTORES.length; i++) {
				tutores[i] = new Tutor(TUTORES[i]);
				db.store(tutores[i]);
			}
			for (String[] p : PETS)
				db.store(new Pet(p[0], p[1], tutores[Integer.parseInt(p[2])]));
			db.commit();

			List<Pet> todos = new java.util.ArrayList<>(db.query(Pet.class));
			todos.sort((x, y) -> x.getId() - y.getId());
			for (int i = 0; i < SERVICOS.length; i++) {
				Servico servico = new Servico(String.format("%02d/%02d/2026", i % 28 + 1, i / 28 + 1),
						new BigDecimal(40 + i % 2 * 5));
				for (int idx : SERVICOS[i])
					servico.adicionarPet(todos.get(idx));
				db.store(servico);
			}
			db.commit();
		} finally {
			Util.desconectar();
		}

		Util.conectar();
		db = Util.getManager();
		try {
			List<Tutor> tutores = db.query(Tutor.class);
			List<Pet> pets = db.query(Pet.class);
			List<Servico> servicos = db.query(Servico.class);
			verdade(tutores.size() == 5, "5 tutores, veio " + tutores.size());
			verdade(pets.size() == 7, "7 pets, veio " + pets.size());
			verdade(servicos.size() == SERVICOS.length, "10 servicos, veio " + servicos.size());

			idsSequenciais(tutores.stream().map(Tutor::getId).toList(), "Tutor");
			idsSequenciais(pets.stream().map(Pet::getId).toList(), "Pet");
			idsSequenciais(servicos.stream().map(Servico::getId).toList(), "Servico");

			for (Pet p : pets) {
				String[] esperado = PETS[p.getId() - 1];
				Tutor dono = p.getTutor();
				verdade(p.getNome().equals(esperado[0]) && dono.getId() == Integer.parseInt(esperado[2]) + 1,
						"Pet id " + p.getId() + " (" + p.getNome() + ", tutor id " + dono.getId() + ") esperado "
								+ esperado[0] + " do tutor id " + (Integer.parseInt(esperado[2]) + 1));
			}

			for (Servico sv : servicos) {
				int esperado = SERVICOS[sv.getId() - 1].length;
				verdade(sv.getListaPets().size() == esperado,
						"Servico id " + sv.getId() + " tem " + sv.getListaPets().size() + " pets, esperado " + esperado);
			}
			for (Pet p : pets)
				verdade(!p.getListaServicos().isEmpty(), "Pet " + p.getNome() + " sem servicos");
		} finally {
			Util.desconectar();
		}
		System.out.println("OK   PopularTest: 5 tutores, 7 pets, 10 servicos (2-3 pets cada, 2 com 1 pet) em lavapet.db4o");
	}

	static void idsSequenciais(List<Integer> ids, String classe) {
		Set<Integer> esperado = new HashSet<>();
		for (int i = 1; i <= ids.size(); i++)
			esperado.add(i);
		verdade(new HashSet<>(ids).equals(esperado), classe + " ids nao sao 1.." + ids.size() + ": " + ids);
	}

	static void verdade(boolean condicao, String mensagem) {
		if (!condicao) {
			System.out.println("FAIL " + mensagem);
			System.exit(1);
		}
	}
}
