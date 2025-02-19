package ch.heig.amt.vineward.api.wine;

import ch.heig.amt.vineward.api.wine.mapping.WineMapper;
import ch.heig.amt.vineward.api.wine.viewmodel.WineViewModel;
import ch.heig.amt.vineward.business.repository.WineRepository;
import ch.heig.amt.vineward.security.attribute.WineIdAttribute;
import ch.heig.amt.vineward.security.policy.AuthorizeReadWine;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for wines.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@RestController
@RequestMapping(path = "/v1/wines", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class WineController {

    private final WineRepository wineRepository;
    private final WineMapper wineMapper;

    @GetMapping
    @Transactional(rollbackFor = Throwable.class, readOnly = true)
    @AuthorizeReadWine
    public List<WineViewModel> getWines() {
        return wineRepository.findAll().stream()
            .map(wineMapper::toLightViewModel)
            .toList();
    }

    @GetMapping("/{id}")
    @Transactional(rollbackFor = Throwable.class, readOnly = true)
    @AuthorizeReadWine
    public WineViewModel getWine(@PathVariable WineIdAttribute id) {
        return wineRepository.findById(id.getValue())
            .map(wineMapper::toViewModel)
            .orElseThrow(() -> new IllegalArgumentException("Wine not found"));
    }
}
