package tacos.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tacos.Ingredient;

@Repository
public class JdbcIngredientRepository implements IngredientRepository{

    // Wstrzyknij instancję 'jdbcTemplate' z skonfirugowanym z DataSource
    private JdbcTemplate jdbcTemplate;

    // Dzięki wstrzyknięciu z pomocą konstruktora, nie ma chwili aby stan 'jdbcTemplate' był NULL
    // @Autowired zakomentowane, gdyż jeśli klasa ma jeden konstruktor, to Spring automatycznie odbiera ją jako Autowired
    public JdbcIngredientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = jdbcTemplate.query("SELECT * FROM Ingredient", this::mapRowToIngredient);
            
        return ingredients;
    }

    @Override
    public Optional<Ingredient> findById(String id) {
        // zawsze query zwraca kolekcję
        List<Ingredient> results = jdbcTemplate.query(
            "SELECT * FROM Ingredient WHERE id=?", 
            this::mapRowToIngredient, id);

        return results.size() == 0 ? 
        Optional.empty() :
        Optional.of(results.get(0));
    }

    @Override
    public Ingredient saveIngredient(Ingredient i) {
        jdbcTemplate.update(
            "INSERT INTO Ingredient(id, name, type) VALUES(?, ?, ?)", 
            i.getId(),
            i.getName(),
            i.getType().toString());// musimy wykonać .toString() bo tabela przyjmuje dane tekstowe a nie ENUM!
        return i;
    }
    
    private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException{
        return new Ingredient(
            rs.getString("id"), 
            rs.getString("name"), 
            Ingredient.Type.valueOf(rs.getString("type")));
    }
}
