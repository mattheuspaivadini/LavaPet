package teste;

import static teste.TesteBase.*;

import java.math.BigDecimal;

import appconsole.Cadastrar;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;

/** Responsavel: appconsole.Cadastrar (tutor, pet e servico). Parte "C" do CRUD. */
public class CadastrarTest {
	public static void main(String[] args) {
		TesteBase b = new TesteBase();
		boolean[] jaCriados = new boolean[1];
		b.com(db -> jaCriados[0] = temDados(db));
		if (jaCriados[0]) {
			System.out.println("AVISO os dados ja foram criados em lavapet.db4o; cadastro nao repetido.");
			return;
		}
		try {
			rodar("[Cadastrar] pet sem tutor cadastrado nao e criado", () -> b.com(db -> {
				verdade(Cadastrar.cadastrarPet(db) == null, "deveria retornar null");
				igual(0, db.query(Pet.class).size(), "pets");
			}));

			rodar("[Cadastrar] tutor persiste com id gerado", () -> {
				b.com(db -> {
					entrada("Ana");
					Cadastrar.cadastrarTutor(db);
					entrada("Bia");
					Cadastrar.cadastrarTutor(db);
				});
				b.com(db -> {
					igual(2, db.query(Tutor.class).size(), "quantidade de tutores");
					igual("Ana", unico(db, Tutor.class, 1).getNome(), "nome do tutor 1");
					igual("Bia", unico(db, Tutor.class, 2).getNome(), "nome do tutor 2");
				});
			});

			rodar("[Cadastrar] tutor com :cancelar nao grava", () -> b.com(db -> {
				entrada(":cancelar");
				verdade(Cadastrar.cadastrarTutor(db) == null, "deveria retornar null");
				igual(2, db.query(Tutor.class).size(), "tutores");
			}));

			rodar("[Cadastrar] pet persiste vinculado ao tutor", () -> {
				b.com(db -> {
					entrada("Rex", "Vira-lata", "1");
					Cadastrar.cadastrarPet(db);
				});
				b.com(db -> {
					Pet p = unico(db, Pet.class, 1);
					igual("Rex", p.getNome(), "nome");
					igual("Vira-lata", p.getRaca(), "raca");
					igual("Ana", p.getTutor().getNome(), "tutor do pet");
					igual(1, unico(db, Tutor.class, 1).getListaPets().size(), "pets do tutor 1");
				});
			});

			rodar("[Cadastrar] pet com tutor inexistente e recusado ate :cancelar", () -> b.com(db -> {
				entrada("X", "Y", "99", ":cancelar");
				verdade(Cadastrar.cadastrarPet(db) == null, "deveria retornar null");
				igual(1, db.query(Pet.class).size(), "pets");
			}));

			rodar("[Cadastrar] servico persiste com pets vinculados", () -> {
				b.com(db -> {
					entrada("Mel", "Poodle", "1");
					Cadastrar.cadastrarPet(db);
					entrada("10/05/2026", "50,00", "1,2");
					Cadastrar.cadastrarServico(db);
				});
				b.com(db -> {
					Servico s = unico(db, Servico.class, 1);
					igual("10/05/2026", s.getData(), "data");
					igual(new BigDecimal("50.00"), s.getValor(), "valor");
					igual(2, s.getListaPets().size(), "pets do servico");
					igual(1, unico(db, Pet.class, 1).getListaServicos().size(), "servicos do pet 1");
				});
			});

			rodar("[Cadastrar] servico com data invalida e pet inexistente e recusado ate :cancelar",
					() -> b.com(db -> {
						entrada("31/02/2026", "10/05/2026", "10", "99", ":cancelar");
						verdade(Cadastrar.cadastrarServico(db) == null, "deveria retornar null");
						igual(1, db.query(Servico.class).size(), "servicos");
					}));
		} finally {
			b.fechar();
		}
	}
}
