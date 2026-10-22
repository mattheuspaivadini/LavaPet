package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pet {
	private int id;
	private String nome;
	private String raca;
	private Tutor tutor;
	private List<Servico> listaServicos = new ArrayList<>();

	public Pet(String nome, String raca, Tutor tutor) {
		if (tutor == null) {
			throw new IllegalArgumentException("Pet precisa de um tutor");
		}
		this.nome = nome;
		this.raca = raca;
		setTutor(tutor);
	}

	public Pet(int id, String nome, String raca, Tutor tutor) {
		this(nome, raca, tutor);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getRaca() {
		return raca;
	}

	public void setRaca(String raca) {
		this.raca = raca;
	}

	public Tutor getTutor() {
		return tutor;
	}

	public final void setTutor(Tutor novo) {
		if (this.tutor == novo) {
			return;
		}
		Tutor antigo = this.tutor;
		this.tutor = novo;
		if (antigo != null) {
			antigo.removerPet(this);
		}
		if (novo != null) {
			novo.adicionarPet(this);
		}
	}

	public List<Servico> getListaServicos() {
		return Collections.unmodifiableList(listaServicos);
	}

	public void adicionarServico(Servico servico) {
		if (servico == null || listaServicos.contains(servico)) {
			return;
		}
		listaServicos.add(servico);
		servico.adicionarPet(this);
	}

	public void removerServico(Servico servico) {
		if (servico == null || !listaServicos.remove(servico)) {
			return;
		}
		servico.removerPet(this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Pet [id=").append(id)
				.append(", nome=").append(nome)
				.append(", raca=").append(raca)
				.append(", tutor=").append(tutor == null ? "-" : tutor.getNome())
				.append(", servicos=[");
		for (int i = 0; i < listaServicos.size(); i++) {
			Servico s = listaServicos.get(i);
			if (i > 0) {
				sb.append(", ");
			}
			sb.append("#").append(s.getId()).append(" ").append(s.getData());
		}
		sb.append("]]");
		return sb.toString();
	}
}
