package teste;

import static teste.TesteBase.*;

import appconsole.Apagar;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;

/** Responsavel: appconsole.Apagar (servico, pet e tutor ja cadastrados). Parte "D" do CRUD. */
public class ApagarTest {
	public static void main(String[] args) {
		TesteBase b = new TesteBase();
		try {
			b.com(TesteBase::semear);

			rodar("[Apagar] servico: 'n' mantem, 's' remove e os pets perdem o vinculo", () -> {
				b.com(db -> {
					entrada("1", "n");
					Apagar.deletarServico(db);
				});
				b.com(db -> igual(1, db.query(Servico.class).size(), "servicos apos recusar"));
				b.com(db -> {
					entrada("1", "s");
					Apagar.deletarServico(db);
				});
				b.com(db -> {
					igual(0, db.query(Servico.class).size(), "servicos restantes");
					igual(2, db.query(Pet.class).size(), "pets intactos");
					igual(0, unico(db, Pet.class, 1).getListaServicos().size(), "servicos do pet 1");
				});
			});

			rodar("[Apagar] pet: 'n' mantem, 's' remove e sai da lista do tutor", () -> {
				// o dono do pet 2 depende de o AlterarTest ja ter rodado; le do arquivo em vez de supor
				int[] dono = new int[2];
				b.com(db -> {
					Tutor t = unico(db, Pet.class, 2).getTutor();
					dono[0] = t.getId();
					dono[1] = t.getListaPets().size();
				});
				b.com(db -> {
					entrada("2", "n");
					Apagar.deletarPet(db);
				});
				b.com(db -> igual(2, db.query(Pet.class).size(), "pets apos recusar"));
				b.com(db -> {
					entrada("2", "s");
					Apagar.deletarPet(db);
				});
				b.com(db -> {
					igual(1, db.query(Pet.class).size(), "pets restantes");
					igual(dono[1] - 1, unico(db, Tutor.class, dono[0]).getListaPets().size(), "pets do dono");
					igual(2, db.query(Tutor.class).size(), "tutores intactos");
				});
			});

			rodar("[Apagar] tutor: 'n' mantem, 's' remove o tutor e seus pets", () -> {
				int[] petsQueFicam = new int[1];
				b.com(db -> petsQueFicam[0] = db.query(Pet.class).size()
						- unico(db, Tutor.class, 1).getListaPets().size());
				b.com(db -> {
					entrada("1", "n");
					Apagar.deletarTutor(db);
				});
				b.com(db -> igual(2, db.query(Tutor.class).size(), "tutores apos recusar"));
				b.com(db -> {
					entrada("1", "s");
					Apagar.deletarTutor(db);
				});
				b.com(db -> {
					verdade(unico(db, Tutor.class, 1) == null, "tutor 1 deveria ter sido apagado");
					igual(petsQueFicam[0], db.query(Pet.class).size(), "pets restantes");
					igual(1, db.query(Tutor.class).size(), "tutores restantes");
				});
			});
		} finally {
			b.fechar();
		}
	}
}
