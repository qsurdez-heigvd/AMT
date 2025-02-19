package ch.heig.amt.vineward.security.resolver;

import ch.aegis.contract.AttributeResolver;
import ch.heig.amt.vineward.api.exception.ObjectNotFoundException;
import ch.heig.amt.vineward.business.model.Wine;
import ch.heig.amt.vineward.business.repository.WineRepository;
import ch.heig.amt.vineward.security.attribute.WineAttribute;
import ch.heig.amt.vineward.security.attribute.WineIdAttribute;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * Resolver for the {@link WineIdAttribute} to {@link WineAttribute} conversion, fetching the wine
 * from the database by its ID and mapping it to an attribute for policy evaluation.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Component
@RequiredArgsConstructor
public final class WineIdWineResolver implements AttributeResolver<WineIdAttribute, WineAttribute> {

    private final AttributeMapper attributeMapper;
    private final WineRepository wineRepository;

    @Override
    public Class<WineIdAttribute> getSourceAttributeType() {
        return WineIdAttribute.class;
    }

    @Override
    public Class<WineAttribute> getResolvedAttributeType() {
        return WineAttribute.class;
    }

    @Override
    public WineAttribute resolve(WineIdAttribute sourceAttribute) {
        return wineRepository.findById(sourceAttribute.getValue())
            .map(attributeMapper::toAttribute)
            .orElseThrow(
                () -> ObjectNotFoundException
                    .forWine()
                    .fromId(sourceAttribute.getValue())
            );
    }

    @Mapper
    interface AttributeMapper {

        @Mapping(target = "canton", source = "origin")
        WineAttribute toAttribute(Wine source);
    }
}
