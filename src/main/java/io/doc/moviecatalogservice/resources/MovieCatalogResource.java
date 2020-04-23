package io.doc.moviecatalogservice.resources;

import io.doc.moviecatalogservice.CatalogItem;
import io.doc.moviecatalogservice.models.Movie;
import io.doc.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private  RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
        UserRating userRating =  restTemplate.getForObject("http://MOVIE-RATING-SERVICE:8083/ratingsdata/users/"+userId+"", UserRating.class);

        return userRating.getUserRating().stream().map(rating -> {
            Movie movie = restTemplate.getForObject("http://MOVIE-INFO-SERVICE:8082/movies/"+rating.getMovieId()+"", Movie.class);

            return new CatalogItem(
                    movie.getName(),
                    movie.getMovieId(),
                    rating.getRating());
            }).collect(Collectors.toList());
    }
}
