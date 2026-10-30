package teste;

import static teste.TesteBase.*;

import java.math.BigDecimal;

import appconsole.Alterar;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;

/** Responsavel: appconsole.Alterar (tutor, pet, servico e vinculo pet-servico). Parte "U" do CRUD. */
public class AlterarTest {
	public static void main(String[] args) {
		TesteBase b = new TesteBase();
		try {
			b.com(TesteBase::semear);

			rodar("[Alterar] tutor: novo nome persiste e Enter mantem o atual", () -> {
				b.com(db -> {
					entrada("1", "Ana Maria");
					Alterar.alterarTutor(db);
					entrada("2", "");
					Alterar.alterarTutor(db);
				});
				b.com(db -> {
					igual("Ana Maria", unico(db, Tutor.class, 1).getNome(), "nome alterado");
					igual("Bia", unico(db, Tutor.class, 2).getNome(), "nome mantido");
				});
			});

			rodar("[Alterar] pet: nome, raca e tutor", () -> {
				b.com(db -> {
					entrada("1", "Rex II", "Poodle", "s", "2");
					Alterar.alterarPet(db);
				});
				b.com(db -> {
					Pet p = unico(db, Pet.class, 1);
					igual("Rex II", p.getNome(), "nome");
					igual("Poodle", p.getRaca(), "raca");
					igual("Bia", p.getTutor().getNome(), "novo tutor");
					igual(1, unico(db, Tutor.class, 1).getListaPets().size(), "tutor antigo ficou so com a Mel");
					igual(1, unico(db, Tutor.class, 2).getListaPets().size(), "tutor novo com pet");
				});
			});

			rodar("[Alterar] servico: data e valor", () -> {
				b.com(db -> {
					entrada("1", "11/05/2026", "75,50", "n");
					Alterar.alterarServico(db);
				});
				b.com(db -> {
					Servico s = unico(db, Servico.class, 1);
					igual("11/05/2026", s.getData(), "data");
					igual(new BigDecimal("75.50"), s.getValor(), "valor");
					igual(2, s.getListaPets().size(), "pets mantidos");
				});
			});

			rodar("[Alterar] servico: troca dos pets", () -> {
				b.com(db -> {
					entrada("1", "", "", "s", "1");
					Alterar.alterarServico(db);
				});
				b.com(db -> {
					igual(1, unico(db, Servico.class, 1).getListaPets().size(), "pets do servico");
					igual(0, unico(db, Pet.class, 2).getListaServicos().size(), "servicos do pet 2");
				});
			});

			rodar("[Alterar] vinculo pet-servico removido dos dois lados", () -> {
				b.com(db -> {
					entrada("1", "1");
					Alterar.removerVinculoPetServico(db);
				});
				b.com(db -> {
					igual(0, unico(db, Servico.class, 1).getListaPets().size(), "pets do servico");
					igual(0, unico(db, Pet.class, 1).getListaServicos().size(), "servicos do pet 1");
				});
			});
		} finally {
			b.fechar();
		}
	}
}
