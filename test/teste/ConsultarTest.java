package teste;

import static teste.TesteBase.*;

import appconsole.Consultar;

/** Responsavel: appconsole.Consultar (listar e buscar tutor, pet e servico). Parte "R" do CRUD. */
public class ConsultarTest {
	public static void main(String[] args) {
		TesteBase b = new TesteBase();
		try {
			b.com(TesteBase::semear);

			rodar("[Consultar] tutor: listar, buscar por id e por trecho do nome", () -> b.com(db -> {
				String lista = capturar(() -> Consultar.listarTutores(db));
				verdade(lista.contains("Ana") && lista.contains("Bia"), "listagem: " + lista);
				igual("Bia", Consultar.buscarTutorPorId(db, 2).getNome(), "buscarTutorPorId");
				verdade(Consultar.buscarTutorPorId(db, 99) == null, "id inexistente");

				entrada("4", "bi", "0");
				String achados = capturar(() -> Consultar.executar(db));
				verdade(achados.contains("Bia") && !achados.contains("Ana"), "consulta por trecho: " + achados);
			}));

			rodar("[Consultar] pet: listar, buscar por id e por nome, raca e tutor", () -> b.com(db -> {
				verdade(capturar(() -> Consultar.listarPets(db)).contains("Rex"), "listagem");
				igual("Rex", Consultar.buscarPetPorId(db, 1).getNome(), "buscarPetPorId");
				verdade(Consultar.buscarPetPorId(db, 99) == null, "id inexistente");

				for (String[] c : new String[][] { { "1", "re" }, { "2", "vira" }, { "3", "an" } }) {
					entrada("5", c[0], c[1], "0");
					String r = capturar(() -> Consultar.executar(db));
					verdade(r.contains("Rex"), "criterio " + c[0] + ": " + r);
				}
				entrada("5", "1", "zzz", "0");
				verdade(capturar(() -> Consultar.executar(db)).contains("Nenhum resultado"), "sem resultado");
			}));

			rodar("[Consultar] servico: listar, buscar por id e por pet, data, intervalo e valor minimo",
					() -> b.com(db -> {
						verdade(capturar(() -> Consultar.listarServicos(db)).contains("10/05/2026"), "listagem");
						igual(50, Consultar.buscarServicoPorId(db, 1).getValor().intValue(), "buscarServicoPorId");
						verdade(Consultar.buscarServicoPorId(db, 99) == null, "id inexistente");

						String[][] criterios = { { "1", "mel" }, { "2", "10/05/2026" },
								{ "3", "01/05/2026", "31/05/2026" }, { "4", "50" } };
						for (String[] c : criterios) {
							String[] linhas = new String[c.length + 2];
							linhas[0] = "6";
							System.arraycopy(c, 0, linhas, 1, c.length);
							linhas[c.length + 1] = "0";
							entrada(linhas);
							String r = capturar(() -> Consultar.executar(db));
							verdade(r.contains("id=1,"), "criterio " + c[0] + ": " + r);
						}
						entrada("6", "4", "100", "0");
						verdade(capturar(() -> Consultar.executar(db)).contains("Nenhum resultado"), "sem resultado");
					}));
		} finally {
			b.fechar();
		}
	}
}
