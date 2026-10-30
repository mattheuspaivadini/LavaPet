package teste;

import static teste.TesteBase.*;

import appconsole.Listar;

/** Responsavel: appconsole.Listar (tutores, pets e servicos juntos, com os vinculos). Parte "R" do CRUD. */
public class ListarTest {
	public static void main(String[] args) {
		TesteBase b = new TesteBase();
		try {
			b.com(TesteBase::semear);

			rodar("[Listar] executar mostra tutores, pets e servico com seus vinculos", () -> b.com(db -> {
				String r = capturar(() -> Listar.executar(db));
				verdade(r.contains("Tutor [id=1, nome=Ana, pets=[Rex, Mel]]"), "tutor com pets: " + r);
				verdade(r.contains("Tutor [id=2, nome=Bia, pets=[]]"), "tutor sem pets: " + r);
				verdade(r.contains("Pet [id=1, nome=Rex, raca=Vira-lata, tutor=Ana, servicos=[#1 10/05/2026]]"),
						"pet com tutor e servico: " + r);
				verdade(r.contains("Servico [id=1, data=10/05/2026, valor=50.00, pets=[Rex, Mel]]"),
						"servico com pets: " + r);
			}));

			rodar("[Listar] base vazia nao lista nenhum registro", () -> {
				TesteBase vazia = TesteBase.temporaria();
				try {
					vazia.com(db -> {
						String r = capturar(() -> Listar.executar(db));
						verdade(!r.contains("[id="), "nao deveria listar registros: " + r);
					});
				} finally {
					vazia.fechar();
				}
			});
		} finally {
			b.fechar();
		}
	}
}
