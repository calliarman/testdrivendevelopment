package order.service.impl;

import java.util.LinkedList;
import java.util.List;

import order.service.OrderService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import common.DataAccessException;
import common.ServiceException;
import order.dao.OrderDao;
import order.model.domain.OrderSummary;
import order.model.entity.OrderEntity;
import order.model.transformer.OrderEntityToOrderST;


public class OrderServiceImplTest {

	private final static long CUSTOMER_ID = 1;
	
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
	public void test_getOrderSummary_success() throws Exception {
		
		/*OrderServiceImpl target = new OrderServiceImpl();
		
		// Setup
		OrderDao mockOrderDao = Mockito.mock(OrderDao.class);
		target.setOrderDao(mockOrderDao);
		
		OrderEntityToOrderST mockTransformer =
				Mockito.mock(OrderEntityToOrderST.class);
		target.setTransformer(mockTransformer);*/
		
		OrderEntity orderEntityFixture = new OrderEntity();
		List<OrderEntity> orderEntityFixtureList = new LinkedList<>();
		orderEntityFixtureList.add(orderEntityFixture);
		
		Mockito.when(mockOrderDao.findByCustomerId(CUSTOMER_ID))
		.thenReturn(orderEntityFixtureList);
		
		OrderSummary orderSummaryFixture = new OrderSummary();
		
		Mockito.when(mockTransformer.transform(orderEntityFixture))
		.thenReturn(orderSummaryFixture);
		
		// Execution
		List<OrderSummary> result = target.getOrderSummary(CUSTOMER_ID);
		
		// Verification
		Mockito.verify(mockOrderDao).findByCustomerId(CUSTOMER_ID);
		Mockito.verify(mockTransformer).transform(orderEntityFixture);
		
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertSame(orderSummaryFixture, result.get(0));
		
	}
	
	@Test
	public void test_openNewOrder_successfullyRetriesDataInsert() throws Exception {
		
		// Setup
		OrderEntity orderEntityFixture = new OrderEntity();
		Mockito.when(mockOrderDao.insert(Mockito.any(OrderEntity.class)))
		.thenThrow(new DataAccessException("First Ex")).thenReturn(orderEntityFixture);
		
		// Execution
		this.target.openNewOrder(CUSTOMER_ID);
		
		// Verification
		Mockito.verify(mockOrderDao, Mockito.times(2)).insert(Mockito.any(OrderEntity.class));
		
	}
	
	@Test(expected=ServiceException.class)
	public void test_openNewOrder_failedDataInsert() throws Exception {
		
		// Setup
		Mockito.when(mockOrderDao.insert(Mockito.any(OrderEntity.class)))
		.thenThrow(new DataAccessException("First Ex"))
		.thenThrow(new DataAccessException("Second Ex"));
		
		try {
			// Execution
			this.target.openNewOrder(CUSTOMER_ID);
		}
		finally {
			// Verification
			Mockito.verify(mockOrderDao, Mockito.times(2)).insert(Mockito.any(OrderEntity.class));
		}
	}
	
	@Test
	public void test_openNewOrder_success() throws Exception {
		
		// Setup
		OrderEntity orderEntityResultFixture = new OrderEntity();
		Mockito.when(mockOrderDao.insert(Mockito.any(OrderEntity.class)))
		.thenReturn(orderEntityResultFixture);
		
		// Execution
		String orderNumber = this.target.openNewOrder(CUSTOMER_ID);
		
		// Verification
		ArgumentCaptor<OrderEntity> orderEntityCaptor =
				ArgumentCaptor.forClass(OrderEntity.class);
		Mockito.verify(mockOrderDao).insert(orderEntityCaptor.capture());
		
		OrderEntity capturedOrderEntity = orderEntityCaptor.getValue();
		
		Assert.assertNotNull(capturedOrderEntity);
		Assert.assertEquals(CUSTOMER_ID, capturedOrderEntity.getCustomerId());
		Assert.assertEquals(orderNumber, capturedOrderEntity.getOrderNumber());
	}
}
