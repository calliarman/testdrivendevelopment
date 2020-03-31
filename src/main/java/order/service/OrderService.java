package order.service;

import java.util.List;

import common.ServiceException;
import order.model.domain.OrderSummary;

public interface OrderService {

	List<OrderSummary> getOrderSummary(long customerId) throws ServiceException;
}
