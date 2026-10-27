package modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Servico {
	private int id;
	private String data;
	private BigDecimal valor;
	private List<Pet> listaPets = new ArrayList<>();

	public Servico(String data, BigDecimal valor) {
		if (data == null || valor == null) {
			throw new IllegalArgumentException("Servico precisa de data e valor");
		}
		this.data = data;
		this.valor = valor;
	}

	public Servico(int id, String data, BigDecimal valor) {
		this(data, valor);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		if (data == null) {
			throw new IllegalArgumentException("data nao pode ser nula");
		}
		this.data = data;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		if (valor == null) {
			throw new IllegalArgumentException("valor nao pode ser nulo");
		}
		this.valor = valor;
	}

	public List<Pet> getListaPets() {
		return Collections.unmodifiableList(listaPets);
	}

	public void adicionarPet(Pet pet) {
		if (pet == null || listaPets.contains(pet)) {
			return;
		}
		listaPets.add(pet);
		pet.adicionarServico(this);
	}

	public void removerPet(Pet pet) {
		if (pet == null || !listaPets.remove(pet)) {
			return;
		}
		pet.removerServico(this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Servico [id=").append(id)
				.append(", data=").append(data)
				.append(", valor=").append(valor.toPlainString())
				.append(", pets=[");
		for (int i = 0; i < listaPets.size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(listaPets.get(i).getNome());
		}
		sb.append("]]");
		return sb.toString();
	}
}
