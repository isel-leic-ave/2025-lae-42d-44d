package pt.isel;

import kotlin.Pair;

import java.util.Map;

import static java.util.Map.entry;

public class ArtistSpotify2ArtistBaseline implements Mapper<ArtistSpotify, Artist> {
    private static Map<Pair<Class<?>, Class<?>>, Mapper<?, ?>> mappers = Map.ofEntries(
            entry(new Pair<>(State.class, Country.class), new State2Country())
    );

    private static <T, R> Mapper<T, R> loadMapper(Class<T> srcType, Class<R> destType) {
        return (Mapper<T, R>) mappers.get(new Pair<>(srcType, destType));
    }


    @Override
    public Artist mapFrom(ArtistSpotify src) {
        return new Artist(
                src.getKind(),
                src.getName(),
                loadMapper(State.class, Country.class).mapFrom(src.getState())
        );
    }
}

class State2Country implements Mapper<State, Country> {

    @Override
    public Country mapFrom(State src) {
        return new Country(src.getName(), src.getIdiom());
    }
}