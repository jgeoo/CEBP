//package com.stock.stock;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.stock.stock.models.Order;
//import com.stock.stock.repositories.OrderRepository;
//import com.stock.stock.repositories.TransactionRepository;
//import com.stock.stock.services.StockExchangeService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//public class StockExchangeServiceTest {
//
//    @Mock
//    private OrderRepository orderRepository;
//
//    @Mock
//    private TransactionRepository transactionRepository;
//
//    @InjectMocks
//    private StockExchangeService stockExchangeService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testPlaceBuyOrder() {
//        Order order = new Order(1L, "CompanyA", 100, 50.0, true);
//        stockExchangeService.placeBuyOrder(order);
//
//        // Verify the order is saved
//        verify(orderRepository, times(1)).save(order);
//
//        // Verify the order was added to buyOrders map
//        assertTrue(stockExchangeService.getBuyOrders().containsKey(order.getCompany()));
//    }
//
//    @Test
//    public void testPlaceSellOrder() {
//        Order order = new Order(2L, "CompanyA", 50, 55.0, false);
//        stockExchangeService.placeSellOrder(order);
//
//        // Verify the order is saved
//        verify(orderRepository, times(1)).save(order);
//
//        // Verify the order was added to sellOrders map
//        assertTrue(stockExchangeService.getSellOrders().containsKey(order.getCompany()));
//    }
//}
