package guru.springframework.reactivemongo.services;

import static org.junit.jupiter.api.Assertions.*;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.mappers.BeerMapper;
import guru.springframework.reactivemongo.model.BeerDTO;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

@SpringBootTest
class BeerServiceImplTest {

  @Autowired BeerService beerService;

  @Autowired BeerMapper beerMapper;

  BeerDTO beerDTO;

  @BeforeEach
  public void setUp() {
    beerDTO = beerMapper.beerToBeerDto(getTestBeer());
  }

  @Test
  void saveBeer() {
    Mono<BeerDTO> savedMono = beerService.saveBeer(Mono.just(beerDTO));
    savedMono.subscribe(savedDto -> System.out.println(savedDto.getId()));

  }

  public static Beer getTestBeer() {
    return Beer.builder()
        .beerName("Space Dust")
        .beerStyle("IPA")
        .price(BigDecimal.TEN)
        .quantityOnHand(12)
        .upc("123231")
        .build();
  }
}
