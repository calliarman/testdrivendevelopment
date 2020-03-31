package order.integration;

import order.model.message.OrderMessage;

public class WarehouseManagementService {

	public static void sendOrder(OrderMessage orderMessage) throws WMSUnavailableException {
		throw new WMSUnavailableException("WMS is currently down for unknown reason");
	}
}
