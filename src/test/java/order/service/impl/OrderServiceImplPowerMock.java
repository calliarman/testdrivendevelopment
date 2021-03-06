package order.service.impl;

import  java.util.LinkedList;

import order.model.message.OrderMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import order.dao.OrderDao;
import order.integration.WarehouseManagementService;
import order.model.entity.OrderEntity;
import order.model.entity.OrderItemEntity;
import order.model.transformer.OrderEntityToOrderST;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value={WarehouseManagementService.class})
public class OrderServiceImplPowerMock {

	private final static long CUSTOMER_ID = 1;
	private final static long ORDER_ID = 2L;
	private final static String ORDER_NUMBER = "1234";
	
	private OrderServiceImpl target = null;

	protected @Mock OrderDao mockOrderDao;
	protected @Mock OrderEntityToOrderST mockTransformer;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.target = new OrderServiceImpl();
		this.target.setOrderDao(mockOrderDao);
		this.target.setTransformer(mockTransformer);
	}
	
	@Test
	public void test_completeOrder_success() throws Exception {
		
		// Setup
		OrderItemEntity oiFixture1 = new OrderItemEntity();
		oiFixture1.setSku("SKU1");
		oiFixture1.setQuantity(1);
		
		OrderItemEntity oiFixture2 = new OrderItemEntity();
		oiFixture2.setSku("SKU2");
		oiFixture2.setQuantity(2);
		
		OrderEntity orderFixture = new OrderEntity();
		orderFixture.setOrderNumber(ORDER_NUMBER);
		orderFixture.setOrderItemList(new LinkedList<OrderItemEntity>());
		orderFixture.getOrderItemList().add(oiFixture1);
		orderFixture.getOrderItemList().add(oiFixture2);
		
		Mockito.when(mockOrderDao.findById(ORDER_ID))
		.thenReturn(orderFixture);
		
		// Add static mocking here
		PowerMockito.mockStatic(WarehouseManagementService.class);
		//PowerMockito.when(WarehouseManagementService.sendOrder(Matchers.any(OrderMessage.class))).thenReturn(true);
		
		// Execution
		target.completeOrder(ORDER_ID);
		
		// Verification
		Mockito.verify(mockOrderDao).findById(ORDER_ID);
		PowerMockito.verifyStatic();
	}
}
