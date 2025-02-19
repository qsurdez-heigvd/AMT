package ch.heig.amt.vineward.api.wine.mapping;

import ch.heig.amt.vineward.api.reference.ReferenceMapper;
import ch.heig.amt.vineward.api.wine.viewmodel.WineViewModel;
import ch.heig.amt.vineward.business.model.Wine;
import ch.heig.amt.vineward.business.model.WineVariety;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapping interface for review comment entities.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Mapper(uses = {ReferenceMapper.class})
public interface WineMapper {

    @Mapping(target = "reviews", ignore = true)
    @InheritConfiguration(name = "toViewModel")
    WineViewModel toLightViewModel(Wine wine);

    @Mapping(target = "region", source = "region.displayName")
    @Mapping(target = "origin", source = "origin.displayName")
    WineViewModel toViewModel(Wine wine);

    static String map(WineVariety variety) {
        return variety.getDisplayName();
    }
}
