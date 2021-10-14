package movies.spring.data.neo4j.movies;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michael Hunger
 * @author Mark Angrish
 * @author Jennifer Reif
 * @author Michael J. Simons
 */
@Service
public class MovieService {

	private final MovieRepository movieRepository;

	MovieService(MovieRepository movieRepository) {

		this.movieRepository = movieRepository;
	}

	private Sort sort = Sort.by(new Sort.Order(Sort.DEFAULT_DIRECTION, "movie.title"));
	private PageRequest pageRequest = PageRequest.of(0, 1, sort);

	public List<MovieResultDto> reactiveSearchMoviesByTitle(String title) {
		return this.movieRepository.pagedSortedFinder(title, pageRequest).stream().map(MovieResultDto::new).collect(Collectors.toList());
	}

}
