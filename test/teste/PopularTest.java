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

// Popula o lavapet.db4o: 5 tutores, 7 pets, 14 servicos por tutor (70 no total).
// ATENCAO: apaga todo o conteudo atual do lavapet.db4o antes de popular.
public class PopularTest {
	static final String[] TUTORES = { "Ana", "Bruno", "Carla", "Diego", "Elisa" };
	// pet -> indice do tutor, fora de ordem de proposito: o id do pet nao segue o do tutor
	// (Ana, tutor id 1, fica com os pets id 3 e 6; Bruno, id 2, com os pets 2 e 5)
	static final String[][] PETS = { { "Bob", "Beagle", "2" }, { "Thor", "Labrador", "1" },
			{ "Rex", "Vira-lata", "0" }, { "Max", "Pug", "4" }, { "Luna", "Siames", "1" },
			{ "Mel", "Poodle", "0" }, { "Nina", "Persa", "3" } };
	// ordem em que os tutores recebem servicos: Diego (id 4) fica com os servicos 1..14, Ana com 15..28...
	static final int[] ORDEM_SERVICOS = { 3, 0, 4, 1, 2 };
	static final int SERVICOS_POR_TUTOR = 14;

	public static void main(String[] args) {
		Util.conectar();
		ObjectContainer db = Util.getManager();
		try {
			for (Object o : db.query(Object.class)) // inclui RegistroID: ids recomecam em 1
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

			int dia = 0;
			for (int indice : ORDEM_SERVICOS) {
				List<Pet> pets = tutores[indice].getListaPets();
				for (int s = 0; s < SERVICOS_POR_TUTOR; s++) {
					Servico servico = new Servico(String.format("%02d/%02d/2026", dia % 28 + 1, dia / 28 + 1),
							new BigDecimal(40 + s * 5));
					servico.adicionarPet(pets.get(s % pets.size())); // reveza entre os pets do tutor
					db.store(servico);
					dia++;
				}
			}
			db.commit();
		} finally {
			Util.desconectar();
		}

		// releitura em conexao nova: o que se verifica veio do disco
		Util.conectar();
		db = Util.getManager();
		try {
			List<Tutor> tutores = db.query(Tutor.class);
			List<Pet> pets = db.query(Pet.class);
			List<Servico> servicos = db.query(Servico.class);
			verdade(tutores.size() == 5, "5 tutores, veio " + tutores.size());
			verdade(pets.size() == 7, "7 pets, veio " + pets.size());
			verdade(servicos.size() == 5 * SERVICOS_POR_TUTOR, "70 servicos, veio " + servicos.size());

			idsSequenciais(tutores.stream().map(Tutor::getId).toList(), "Tutor");
			idsSequenciais(pets.stream().map(Pet::getId).toList(), "Pet");
			idsSequenciais(servicos.stream().map(Servico::getId).toList(), "Servico");

			// ids sao de cada classe: o pet id N e o N-esimo criado, seja qual for o id do seu tutor
			for (Pet p : pets) {
				String[] esperado = PETS[p.getId() - 1];
				Tutor dono = p.getTutor();
				verdade(p.getNome().equals(esperado[0]) && dono.getId() == Integer.parseInt(esperado[2]) + 1,
						"Pet id " + p.getId() + " (" + p.getNome() + ", tutor id " + dono.getId() + ") esperado "
								+ esperado[0] + " do tutor id " + (Integer.parseInt(esperado[2]) + 1));
			}

			for (Tutor t : tutores) {
				Set<Servico> doTutor = new HashSet<>();
				for (Pet p : t.getListaPets())
					doTutor.addAll(p.getListaServicos());
				verdade(doTutor.size() == SERVICOS_POR_TUTOR, t.getNome() + " tem " + doTutor.size() + " servicos");
				int posicao = java.util.Arrays.stream(ORDEM_SERVICOS).boxed().toList().indexOf(t.getId() - 1);
				int primeiro = posicao * SERVICOS_POR_TUTOR + 1;
				for (Servico s : doTutor)
					verdade(s.getId() >= primeiro && s.getId() < primeiro + SERVICOS_POR_TUTOR, "Servico id " + s.getId()
							+ " do tutor id " + t.getId() + " fora de " + primeiro + ".." + (primeiro + SERVICOS_POR_TUTOR - 1));
			}
		} finally {
			Util.desconectar();
		}
		System.out.println("OK   PopularTest: 5 tutores, 7 pets, 14 servicos por tutor em lavapet.db4o");
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
