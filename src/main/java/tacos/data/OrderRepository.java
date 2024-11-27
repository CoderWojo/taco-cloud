package tacos.data;

import tacos.TacoOrder;

public interface OrderRepository {
    TacoOrder saveOrder(TacoOrder order);
}
