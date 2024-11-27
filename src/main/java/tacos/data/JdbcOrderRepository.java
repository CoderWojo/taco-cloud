package tacos.data;

import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tacos.Ingredient;
import tacos.Taco;
import tacos.TacoOrder;

@Repository
public class JdbcOrderRepository implements OrderRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional  // ogarnij
    public TacoOrder saveOrder(TacoOrder order) {
        order.setPlaced_At(new Date());
        String sql = "INSERT INTO Taco_Order" +
                    "(delivery_name, delivery_street, delivery_city, " +
                    "delivery_state, delivery_zip, cc_number, " +
                    "cc_expiration, cc_cvv, placed_at)" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Tworząc 'pscf' tworzymy implementację PreparedStatementCreator(Connection con) i 'con' jest dołączany przez Spring'a który nim zarządza w DataSource
        // 'pscf' ma wiedzę o:
            // Strukturze zapytania SQL,
            // Typach parametrów
            // ! Lecz nie przechowuje wartości parametrów
        // Dlatego musimy połączyć wartości parametrów z szablonem fabryki
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sql,
            Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, 
            Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
            Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP);

        pscf.setReturnGeneratedKeys(true); // niech po każdym zapytaniu, Spring automatycznie pobierze klucz podstawowy
        
        // zanim wykonamy operacje zapisywania order, przypiszmy wartość 'placedAt' aby można było użyć gettera do niej
        

        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(Arrays.asList(
            order.getDeliveryName(),
            order.getDeliveryStreet(),
            order.getDeliveryCity(),
            order.getDeliveryState(),
            order.getDeliveryZip(),
            order.getCcNumber(),
            order.getCcExpiration(),
            order.getCcCVV(),
            order.getPlaced_At()));

        // .update() zwraca liczbę wierszy zmodyfikowanych!
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(psc, keyHolder);
        long orderId = keyHolder.getKey().longValue();
        order.setId(orderId);
        
        List<Taco> tacos = order.getTacos();
        int i = 0;
        for(Taco t : tacos) {
            saveTaco(t, orderId, i);
            i++;
        }

        return order;
    }

    private Taco saveTaco(Taco taco, long orderId, int numberInOrder) {
        taco.setCreatedAt(new Date());
        // 1. Tworzymy PreparedStatementCreatorFactory aby określić zapytanie SQL i typy parametrów
        String sql = "INSERT INTO Taco" +
            "(name, taco_order, number_in_order, created_at)" +
            "VALUES(?, ?, ?, ?)";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sql,
            Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.TIMESTAMP);
        
        // id dla taco odbierzemy po wykonaniu instrukcji na 'jdbcTemplate'
            // Tworzymy PreparedStatementCreator na bazie 'pscf' aby określić wartości parametrów do tego konkretnego zapytania sql z 'pscf' 
        pscf.setReturnGeneratedKeys(true);

        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(
            Arrays.asList(
                taco.getName(),
                (int)orderId, 
                numberInOrder, 
                taco.getCreatedAt()));

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(psc, keyHolder);    // po wykonaniu .update(), umieść id w 'keyholder'

        long tacoId = keyHolder.getKey().longValue();
        taco.setId(tacoId);

        // zapisz referencję składników w Ingredient_Ref

        saveIngredientRefs(taco.getIngredients(), tacoId);

        return taco;
    }

    private void saveIngredientRefs(List<Ingredient> ingredients, long tacoId) {
        // Tutaj dla odmiany już bez fabryki i creator'a
        String sql = "INSERT INTO Ingredient_Ref(taco_id, ingredient, pos_in_taco) VALUES(?, ?, ?)";
        // PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sql, 
        //     Types.INTEGER, Types.VARCHAR, Types.INTEGER);

        int i = 0;
        for(Ingredient ingredient : ingredients){
            jdbcTemplate.update(sql, (int)tacoId, ingredient.getId(), i);
            i++;
        }
        
        // int i = 0;
        // for(Ingredient ingredient : ingredients) {
        //     PreparedStatementCreator psc = pscf.newPreparedStatementCreator(Arrays.asList(
        //         (int)tacoId,
        //         ingredient.getName(),
        //         i
        //     ));
        //     i++;

        //     jdbcTemplate.update(psc);
        // }
        
    }
}
