package appconsole;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;
import util.BancoMemoria;
import util.Util;

public class Demonstracao {
	private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu");

	public static void main(String[] args) {
		Util.executar(Demonstracao::executar);
	}

	public static void executar(BancoMemoria db) {
		exibirExemplosDiretos(db);
		menu(db);
	}

	public static void exibirExemplosDiretos(BancoMemoria db) {
		System.out.println("SISTEMA LAVAPET");

		System.out.println("1. TUTORES");
		for (Tutor t : db.query(Tutor.class)) {
			System.out.println("• " + t);
			for (Pet p : t.getListaPets()) {
				System.out.println("    └─ Pet: " + p.getNome() + " | Raça: " + p.getRaca());
			}
		}

		System.out.println("\n2. PETS");
		for (Pet p : db.query(Pet.class)) {
			System.out.println("• " + p);
			for (Servico s : p.getListaServicos()) {
				System.out.printf("    └─ Serviço #%d | Data: %s | Valor: R$ %.2f%n",
						s.getId(), s.getData().format(DATA), s.getValor());
			}
		}

		System.out.println("\n3. SERVIÇOS");
		BigDecimal totalFaturado = BigDecimal.ZERO;
		for (Servico s : db.query(Servico.class)) {
			System.out.printf("• Serviço #%d | Data: %s | Valor: R$ %.2f%n",
					s.getId(), s.getData().format(DATA), s.getValor());
			totalFaturado = totalFaturado.add(s.getValor());
			for (Pet p : s.getListaPets()) {
				String nomeTutor = (p.getTutor() != null) ? p.getTutor().getNome() : "Sem tutor";
				System.out.println("    └─ Pet atendido: " + p.getNome() + " (" + p.getRaca() + ") | Tutor: " + nomeTutor);
			}
		}

		List<Tutor> tutores = db.query(Tutor.class);
		List<Pet> pets = db.query(Pet.class);
		List<Servico> servicos = db.query(Servico.class);

		System.out.println("\nRESUMO GERAL DO LAVAPET");
		System.out.println("Total de Tutores:  " + tutores.size());
		System.out.println("Total de Pets:     " + pets.size());
		System.out.println("Total de Serviços: " + servicos.size());
		System.out.printf("Faturamento Total: R$ %.2f%n", totalFaturado);
	}

	public static void menu(BancoMemoria db) {
		Entrada.reiniciar();
		boolean continuar = true;
		while (continuar && !Entrada.encerrada()) {
			exibirMenuPrincipal();
			String opcao = Entrada.ler("Opção");
			switch (opcao) {
			case "1":
				exibirExemplosDiretos(db);
				break;
			case "2":
				Listar.executar(db);
				break;
			case "3":
				Cadastrar.executar(db);
				break;
			case "4":
				Consultar.executar(db);
				break;
			case "5":
				Alterar.executar(db);
				break;
			case "6":
				Deletar.executar(db);
				break;
			case "0":
				continuar = false;
				break;
			default:
				System.out.println("Opção inválida. Escolha uma opção do menu.");
				break;
			}
		}
		System.out.println("Até logo.");
	}

	private static void exibirMenuPrincipal() {
		System.out.println("=== LavaPet - Menu ===\n"
				+ "1 Exibir exemplos completos\n"
				+ "2 Listagem simples\n"
				+ "3 Cadastrar\n"
				+ "4 Consultar dados\n"
				+ "5 Alterar dados\n"
				+ "6 Deletar dados\n"
				+ "0 Sair");
	}
}
