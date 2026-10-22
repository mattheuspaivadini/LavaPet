import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Servico {
    private int id;
    private LocalDate data;
    private BigDecimal valor;
    private List<Pet> listaPets;

    public Servico() {
        this.listaPets = new ArrayList<>();
    }

    public Servico(int id, LocalDate data, BigDecimal valor) {
        this();
        this.id = id;
        this.data = data;
        this.valor = valor;
    }

    public void adicionarPet(Pet pet) {
        if (!this.listaPets.contains(pet)) {
            this.listaPets.add(pet);
            pet.vincularServico(this);
        }
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public List<Pet> getListaPets() { return listaPets; }
    public void setListaPets(List<Pet> listaPets) { this.listaPets = listaPets; }

    @Override
    public String toString() {
        return "Servico [id=" + id + ", data=" + data + ", valor=" + valor + ", totalPets=" + listaPets.size() + "]";
    }
}