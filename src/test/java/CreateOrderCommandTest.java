import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.riders.sharing.command.Command;
import org.riders.sharing.command.CreateOrderCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.OrderDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.IllegalStatusException;
import org.riders.sharing.exception.NotFoundException;
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
import java.util.UUID;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.riders.sharing.model.enums.OrderStatus.ONGOING;
import static org.riders.sharing.model.enums.ScooterStatus.RENTED;

public class CreateOrderCommandTest extends BaseTest implements OrderTestData, CustomerTestData, ScooterTestData {
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final ScooterRepository scooterRepository = new ScooterRepositoryImpl(ConnectionPool.INSTANCE);
    private final OrderRepository orderRepository = new OrderRepositoryImpl(ConnectionPool.INSTANCE);

    private final CustomerService customerService = new CustomerServiceImpl(customerRepository);
    private final ScooterService scooterService = new ScooterServiceImpl(scooterRepository);
    private final OrderService orderService = new OrderServiceImpl(customerService, scooterService, orderRepository);

    private final Command createOrderCommand = new CreateOrderCommand(orderService);

    @Test
    public void createRespondsWith201AndOrder() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var savedCustomer = customerRepository.save(aCustomer().build());
        final var savedScooter = scooterRepository.save(aScooter().build());

        final var stringReader = new StringReader("""
            {
            "customerId" : "%s",
            "scooterId" : "%s"
            }
            """.formatted(savedCustomer.getId(), savedScooter.getId())
        );
        final var requestReader = new BufferedReader(stringReader);

        final var stingWriter = new StringWriter();
        final var responseWriter = new PrintWriter(stingWriter);

        final var expectedScooterStatus = RENTED;
        final var expectedResponseStatus = SC_CREATED;
        final var expectedOrderStatus = ONGOING;

        when(request.getReader()).thenReturn(requestReader);
        when(response.getWriter()).thenReturn(responseWriter);

        //when
        createOrderCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponseStatus);

        final var orderDtoFromResponse = ModelMapper.parse(stingWriter.toString(), OrderDto.class);
        final var orderFromDb = orderService.getById(orderDtoFromResponse.orderId());

        assertEquals(expectedScooterStatus, orderFromDb.getScooter().getStatus());
        assertEquals(expectedOrderStatus, orderFromDb.getStatus());
    }

    @Test
    public void createOrderThrowsBadRequest() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var savedCustomer = customerRepository.save(aCustomer().build());

        final var stringReader = new StringReader("""
            {
            "customerId" : "%s"
            }
            """.formatted(savedCustomer.getId())
        );
        final var badRequestReader = new BufferedReader(stringReader);

        when(request.getReader()).thenReturn(badRequestReader);

        //when & then
        assertThrows(
            BadRequestException.class,
            () -> createOrderCommand.execute(request, response)
        );
    }

    @Test
    public void createOrderThrowsIllegalStatus() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var savedCustomer = customerRepository.save(aCustomer().build());
        final var savedRentedScooter = scooterRepository.save(
            aScooter()
                .status(RENTED)
                .build()
        );

        final var stringReader = new StringReader("""
            {
            "customerId" : "%s",
            "scooterId" : "%s"
            }
            """.formatted(savedCustomer.getId(), savedRentedScooter.getId())
        );
        final var requestReader = new BufferedReader(stringReader);

        final var stingWriter = new StringWriter();
        final var responseWriter = new PrintWriter(stingWriter);

        when(request.getReader()).thenReturn(requestReader);
        when(response.getWriter()).thenReturn(responseWriter);

        //when & then
        assertThrows(
            IllegalStatusException.class,
            () -> createOrderCommand.execute(request, response)
        );
    }

    @Test
    public void createOrderThrowsNotFound() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var randomId = UUID.randomUUID();

        final var stringReader = new StringReader("""
            {
            "customerId" : "%s",
            "scooterId" : "%s"
            }
            """.formatted(randomId, randomId)
        );
        final var requestReader = new BufferedReader(stringReader);

        final var stingWriter = new StringWriter();
        final var responseWriter = new PrintWriter(stingWriter);

        when(request.getReader()).thenReturn(requestReader);
        when(response.getWriter()).thenReturn(responseWriter);

        //when & then
        assertThrows(
            NotFoundException.class,
            () -> createOrderCommand.execute(request, response)
        );
    }
}
