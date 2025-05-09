import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.riders.sharing.command.Command;
import org.riders.sharing.command.CompletedCustomerOrdersCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.OrderDto;
import org.riders.sharing.dto.PageResponseDto;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.OrderRepository;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.repository.impl.OrderRepositoryImpl;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.service.impl.OrderServiceImpl;
import org.riders.sharing.service.impl.ScooterServiceImpl;
import org.riders.sharing.utils.ModelMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.riders.sharing.model.enums.OrderStatus.COMPLETED;


public class CompletedCustomerOrdersCommandTest extends BaseTest
    implements OrderTestData, CustomerTestData, ScooterTestData {
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final ScooterRepository scooterRepository = new ScooterRepositoryImpl(ConnectionPool.INSTANCE);
    private final OrderRepository orderRepository = new OrderRepositoryImpl(ConnectionPool.INSTANCE);

    private final CustomerService customerService = new CustomerServiceImpl(customerRepository);
    private final ScooterService scooterService = new ScooterServiceImpl(scooterRepository);
    private final OrderService orderService = new OrderServiceImpl(customerService, scooterService, orderRepository);

    private final Command completedCustomerOrdersCommand = new CompletedCustomerOrdersCommand(orderService);

    @Test
    public void completedCustomerRespondsWith200AndOrders() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var savedScooter = scooterRepository.save(aScooter().build());
        final var savedCustomer = customerRepository.save(aCustomer().build());
        final var orderList = List.of(
            anOrder().status(COMPLETED).customerId(savedCustomer.getId()).scooter(savedScooter).build(),
            anOrder().status(COMPLETED).customerId(savedCustomer.getId()).scooter(savedScooter).build(),
            anOrder().status(COMPLETED).customerId(savedCustomer.getId()).scooter(savedScooter).build(),
            anOrder().status(COMPLETED).customerId(savedCustomer.getId()).scooter(savedScooter).build()
        );
        orderList.forEach(orderRepository::save);

        final var orderDtoList = orderList.stream()
            .map(OrderDto::fromOrder)
            .toList();

        final var page = 2;
        final var pageSize = 2;

        final var stringReader = new StringReader("""
            {
            "customerId" : "%s",
            "pageRequestDto" :
                {
                "page" : "%d",
                "pageSize" : "%d"
                }
            }
            """
            .formatted(
                savedCustomer.getId(),
                page,
                pageSize
            )
        );
        final var requestReader = new BufferedReader(stringReader);

        final var stringWriter = new StringWriter();
        final var responseWriter = new PrintWriter(stringWriter);

        final var expectedResponseStatus = SC_OK;

        final var expectedTotalElements = orderList.size();
        final var expectedTotalPages = 2;
        final var expectedPageResponse = new PageResponseDto<>(
            orderDtoList.subList(2, 4),
            page,
            pageSize,
            expectedTotalElements,
            expectedTotalPages
        );

        when(request.getReader()).thenReturn(requestReader);
        when(response.getWriter()).thenReturn(responseWriter);

        //when
        completedCustomerOrdersCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponseStatus);

        final var actualPageResponse = ModelMapper.parse(
            stringWriter.toString(),
            new TypeReference<PageResponseDto<OrderDto>>() {
            }
        );

        assertEquals(expectedPageResponse, actualPageResponse);
    }

    @Test
    public void completedCustomerOrdersRespondsWith400() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var page = 2;
        final var pageSize = 2;

        final var stringReader = new StringReader("""
            {
                "pageRequestDto" : {
                    "page" : "%s",
                    "pageSize" : "%s"
                    }
            }
            """
            .formatted(
                page,
                pageSize
            )
        );
        final var requestReader = new BufferedReader(stringReader);

        final var expectedResponseStatus = SC_BAD_REQUEST;

        when(request.getReader()).thenReturn(requestReader);

        //when
        completedCustomerOrdersCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponseStatus);
    }

    @Test
    public void completedCustomerOrdersRespondsWith500() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var expectedResponseStatus = SC_INTERNAL_SERVER_ERROR;

        when(request.getReader()).thenThrow(RuntimeException.class);

        //when
        completedCustomerOrdersCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponseStatus);
    }
}
