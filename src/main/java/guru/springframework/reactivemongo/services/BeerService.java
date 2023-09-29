package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.model.BeerDTO;
import reactor.core.publisher.Mono;

public interface BeerService {

  Mono<BeerDTO> saveBeer(BeerDTO beerDTO);

  Mono<BeerDTO> getByIOd(String beerId);
}
