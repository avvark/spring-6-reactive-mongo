package guru.springframework.reactivemongo.web.fn;

import static org.hamcrest.Matchers.greaterThan;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.services.BeerService;
import java.math.BigDecimal;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeerEndpointTest {

  @Autowired private BeerService beerService;

  @Autowired
  WebTestClient webTestClient;

  @Test
  @Order(2)
  void testGetById() {
    BeerDTO beerDto = getSavedTestBeer();
    webTestClient
        .get()
        .uri(BeerRouterConfig.BEER_PATH_ID, beerDto.getId())
        .exchange()
        .expectStatus().isOk()
        .expectHeader().valueEquals("Content-type", "application/json")
        .expectBody(BeerDTO.class);
  }

  @Test
  void testGetByIdNotFound() {
    webTestClient
        .get()
        .uri(BeerRouterConfig.BEER_PATH_ID, 99)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  @Order(1)
  void testListBeersByStyle() {
    final String BEER_STYLE = "TEST";
    BeerDTO testDto = getSavedTestBeer();
    testDto.setBeerStyle(BEER_STYLE);

    webTestClient
        .post()
        .uri(BeerRouterConfig.BEER_PATH)
        .body(Mono.just(testDto), BeerDTO.class)
        .header("Content-Type", "application/json")
        .exchange();

    webTestClient
        .get()
        .uri(UriComponentsBuilder.fromPath(BeerRouterConfig.BEER_PATH)
            .queryParam("beerStyle", BEER_STYLE).build().toUri())
        .exchange()
        .expectStatus().isOk()
        .expectHeader().valueEquals("Content-type", "application/json")
        .expectBody().jsonPath("$.size()").isEqualTo(1);
  }

  @Test
  @Order(1)
  void testListBeers() {
    webTestClient
        .get()
        .uri(BeerRouterConfig.BEER_PATH)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().valueEquals("Content-type", "application/json")
        .expectBody().jsonPath("$.size()").value(greaterThan(1));
  }

  @Test
  @Order(2)
  void testCreateBeer() {
    webTestClient
        .post()
        .uri(BeerRouterConfig.BEER_PATH)
        .body(Mono.just(getTestBeer()), BeerDTO.class)
        .header("Content-type", "application/json")
        .exchange()
        .expectStatus()
        .isCreated()
        .expectHeader()
        .exists("Location");
  }

  @Test
  void testCreateBeerBadData() {
    Beer testBeer = getTestBeer();
    testBeer.setBeerName("");
    webTestClient
        .post()
        .uri(BeerRouterConfig.BEER_PATH)
        .body(Mono.just(testBeer), BeerDTO.class)
        .header("Content-type", "application/json")
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  private BeerDTO getSavedTestBeer() {
    return beerService.listBeers().blockFirst();
  }


  @Test
  @Order(3)
  void testUpdateBeer() {
    BeerDTO beerDto = getSavedTestBeer();
    beerDto.setBeerName("New");
    webTestClient
        .put()
        .uri(BeerRouterConfig.BEER_PATH_ID, beerDto.getId())
        .body(Mono.justOrEmpty(beerDto), BeerDTO.class)
        .header("Content-type", "application/json")
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  @Order(4)
  void testUpdateBeerBadRequest() {
    Beer testBeer = getTestBeer();
    testBeer.setBeerStyle("");
    webTestClient
        .put()
        .uri(BeerRouterConfig.BEER_PATH_ID, 1)
        .body(Mono.just(testBeer), BeerDTO.class)
        .header("Content-type", "application/json")
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void testUpdateBeerNotFound() {
    webTestClient
        .put()
        .uri(BeerRouterConfig.BEER_PATH_ID, 99)
        .body(Mono.just(getTestBeer()), BeerDTO.class)
        .header("Content-type", "application/json")
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  @Order(3)
  void testPatchBeer() {
    BeerDTO beerDto = getSavedTestBeer();
    beerDto.setBeerName("New");
    webTestClient
        .patch()
        .uri(BeerRouterConfig.BEER_PATH_ID, beerDto.getId())
        .body(Mono.just(beerDto), BeerDTO.class)
        .header("Content-type", "application/json")
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  void testPatchBeerBadRequest() {
    Beer testBeer = getTestBeer();
    testBeer.setBeerStyle("");
    webTestClient
        .patch()
        .uri(BeerRouterConfig.BEER_PATH_ID, 99)
        .body(Mono.just(testBeer), BeerDTO.class)
        .header("Content-type", "application/json")
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void testPatchBeerNotFound() {
    webTestClient
        .patch()
        .uri(BeerRouterConfig.BEER_PATH_ID, 99)
        .body(Mono.just(getTestBeer()), BeerDTO.class)
        .header("Content-type", "application/json")
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  @Order(999)
  void testDeleteBeer() {
    BeerDTO beerDto = getSavedTestBeer();
    webTestClient
        .delete()
        .uri(BeerRouterConfig.BEER_PATH_ID, beerDto.getId())
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  @Order(999)
  void testDeleteBeerNotFound() {
    webTestClient
        .delete()
        .uri(BeerRouterConfig.BEER_PATH_ID, 99)
        .exchange()
        .expectStatus()
        .isNotFound();
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
