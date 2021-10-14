package movies.spring.data.neo4j.api;

import movies.spring.data.neo4j.movies.MovieResultDto;
import movies.spring.data.neo4j.movies.MovieService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Michael J. Simons
 */
@RestController
public class MovieController {

	private final MovieService movieService;

	MovieController(MovieService movieService) {
		this.movieService = movieService;
	}

	@GetMapping("/reactivesearch")
	public CompletableFuture<List<MovieResultDto>> reactiveSearch(@RequestParam("q") String title) {
		// Contrived example made up to show off the concurrency issue, we choose a number higher than
		// Neo4SpelSupport.StringBasedLiteralReplacement.DEFAULT_CACHE_SIZE
		// to try to force the eviction and general manipulation of the cache that trigger the bug
		CompletableFuture<List<MovieResultDto>>[] futures = new CompletableFuture[32];
		for (int i = 0; i < futures.length; i++ ) {
			futures[i] = CompletableFuture.supplyAsync(() -> movieService.reactiveSearchMoviesByTitle(stripWildcards(title)));
		}
		CompletableFuture<List<MovieResultDto>> total = CompletableFuture.allOf(futures).thenApply(voidRes -> {
			List<MovieResultDto> resultList = new LinkedList<>();
	 		for (CompletableFuture<List<MovieResultDto>> future: futures) {
				resultList.addAll(future.join());
			}
			return resultList;
		});
		return total;
	}

	private static String stripWildcards(String title) {
		String result = title;
		if (result.startsWith("*")) {
			result = result.substring(1);
		}
		if (result.endsWith("*")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
}
