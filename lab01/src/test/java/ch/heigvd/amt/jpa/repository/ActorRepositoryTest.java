package ch.heigvd.amt.jpa.repository;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestTransaction
class ActorRepositoryTest {
    @Inject
    ActorRepository actorRepository;

    @Inject
    EntityManager em;

    @Test
    public void testReadActor() {
        ActorRepository.ActorDTO actorDTO = actorRepository.read(128);
        assertEquals(actorDTO.id(), 128);
        assertEquals(actorDTO.firstName(), "CATE");
        assertEquals(actorDTO.lastName(), "MCQUEEN");
    }

    @Test
    public void testCreateActor() {
        Integer actorId = actorRepository.create("ALICE", "BOB");
        assertNotNull(actorId);

        ActorRepository.ActorDTO actorDTO = actorRepository.read(actorId);
        assertEquals(actorDTO.id(), actorId);
        assertEquals(actorDTO.firstName(), "ALICE");
        assertEquals(actorDTO.lastName(), "BOB");
    }

    @Test
    public void actorFirstNameMustBeShorterThat45Characters() {
        AtomicReference<Integer> atomicActorId = new AtomicReference<>();

        Exception exception = assertThrows(DataException.class, () -> {
            atomicActorId.set(actorRepository.create("*".repeat(46), "BOB"));
        });
        String expectedMessage = "value too long for type character varying(45)";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertNull(atomicActorId.get(), "Actor ID should be null");
    }

    @Test
    public void actorLastNameMustBeShorterThat45Characters() {
        AtomicReference<Integer> atomicActorId = new AtomicReference<>();

        Exception exception = assertThrows(DataException.class, () -> {
            atomicActorId.set(actorRepository.create("ALICE", "*".repeat(46)));
        });
        String expectedMessage = "value too long for type character varying(45)";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertNull(atomicActorId.get(), "Actor ID should be null");
    }

    @Test
    public void testUpdateActor() {
        Integer actorId = actorRepository.create("ALICE", "BOB");

        actorRepository.update(actorId, "FOO", "BAR");
        em.flush();
        em.clear();

        ActorRepository.ActorDTO actorDTO = actorRepository.read(actorId);
        assertEquals(actorDTO.id(), actorId);
        assertEquals(actorDTO.firstName(), "FOO");
        assertEquals(actorDTO.lastName(), "BAR");
    }

    @Test
    public void testUpdateActorWithoutClear() {
        Integer actorId = actorRepository.create("ALICE", "BOB");

        actorRepository.update(actorId, "FOO", "BAR");
        em.flush();

        ActorRepository.ActorDTO actorDTO = actorRepository.read(actorId);
        assertEquals(actorDTO.id(), actorId);
        assertEquals(actorDTO.firstName(), "FOO");
        assertEquals(actorDTO.lastName(), "BAR");
    }

    @Test
    public void testUpdateActorWithoutFlushAndClear() {
        Integer actorId = actorRepository.create("ALICE", "BOB");

        actorRepository.update(actorId, "FOO", "BAR");

        ActorRepository.ActorDTO actorDTO = actorRepository.read(actorId);
        assertEquals(actorDTO.id(), actorId);
        assertEquals(actorDTO.firstName(), "FOO");
        assertEquals(actorDTO.lastName(), "BAR");
    }

    @Test
    public void testDeleteActor() {
        Integer actorId = actorRepository.create("ALICE", "BOB");
        em.flush();

        actorRepository.delete(actorId);
        em.flush();

        ActorRepository.ActorDTO actorDTO = actorRepository.read(actorId);
        assertNull(actorDTO);
    }
}
