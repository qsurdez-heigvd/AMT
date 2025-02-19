package ch.heigvd.amt.jpa.repository;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.exception.DataException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class AddressRepositoryTest {

    @Inject
    AddressRepository addressRepository;

    @Inject
    EntityManager em;

    @Test
    public void testReadAddress() {
        AddressRepository.AddressDTO addressDTO = addressRepository.read(1);
        assertEquals(addressDTO.id(), 1);
        assertEquals(addressDTO.address(), "47 MySakila Drive");
        assertEquals(addressDTO.district(), " ");
        assertEquals(addressDTO.city(), "Lethbridge");
        assertEquals(addressDTO.phone(), " ");
    }

    @Test
    public void testCreateAddress() {
        String address = "9966 Bag End";
        String district = "The Shire";
        String city = "Alessandria";
        String phone = "123456";

        Integer addressId = addressRepository.create(address, district, city, phone);
        AddressRepository.AddressDTO addressDTO = addressRepository.read(addressId);
        assertEquals(addressDTO.id(), addressId);
        assertEquals(addressDTO.address(), address);
        assertEquals(addressDTO.district(), district);
        assertEquals(addressDTO.city(), city);
        assertEquals(addressDTO.phone(), phone);
    }

    @Test
    public void addressNameShouldBeLowerThen50Characters() {
        AtomicReference<Integer> atomicAddressId = new AtomicReference<>();
        String address = "*".repeat(51);
        String district = "The Shire";
        String city = "Alessandria";
        String phone = "123456";

        Exception exception = assertThrows(DataException.class, () -> {
            atomicAddressId.set(addressRepository.create(address, district, city, phone));
        });

        String expectedMessage = "value too long for type character varying(50)";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertNull(atomicAddressId.get(), "Address ID should be null");
    }

    @Test
    public void districtNameShouldBeLowerThan20Characters() {
        AtomicReference<Integer> atomicAddressId = new AtomicReference<>();
        String address = "9966 Bag End";
        String district = "*".repeat(21);
        String city = "Alessandria";
        String phone = "123456";

        Exception exception = assertThrows(DataException.class, () -> {
            atomicAddressId.set(addressRepository.create(address, district, city, phone));
        });

        String expectedMessage = "value too long for type character varying(20)";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertNull(atomicAddressId.get(), "Address ID should be null");
    }

    @Test
    public void phoneNameShouldBeLowerThan20Characters() {
        AtomicReference<Integer> atomicAddressId = new AtomicReference<>();
        String address = "9966 Bag End";
        String district = "The Shire";
        String city = "Alessandria";
        String phone = "*".repeat(21);

        Exception exception = assertThrows(DataException.class, () -> {
            atomicAddressId.set(addressRepository.create(address, district, city, phone));
        });

        String expectedMessage = "value too long for type character varying(20)";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertNull(atomicAddressId.get(), "Address ID should be null");
    }

    @Test
    public void testUpdateAddress() {
        String address = "9966 Bag End";
        String district = "The Shire";
        String city = "Alessandria";
        String phone = "123456";
        String addressUpdated = "9999 Valinor";
        String districtUpdated = "The Undying Lands";
        String cityUpdated = "Springs";
        String phoneUpdated = "00000000";

        Integer addressId = addressRepository.create(address, district, city, phone);

        addressRepository.update(addressId, addressUpdated, districtUpdated, cityUpdated, phoneUpdated);
        em.flush();
        em.clear();

        AddressRepository.AddressDTO addressDTO = addressRepository.read(addressId);
        assertEquals(addressDTO.id(), addressId);
        assertEquals(addressDTO.address(), addressUpdated);
        assertEquals(addressDTO.district(), districtUpdated);
        assertEquals(addressDTO.city(), cityUpdated);
        assertEquals(addressDTO.phone(), phoneUpdated);
    }

    @Test
    public void testUpdateAddressWithoutClear() {
        String address = "9966 Bag End";
        String district = "The Shire";
        String city = "Alessandria";
        String phone = "123456";
        String addressUpdated = "9999 Valinor";
        String districtUpdated = "The Undying Lands";
        String cityUpdated = "Springs";
        String phoneUpdated = "00000000";

        Integer addressId = addressRepository.create(address, district, city, phone);

        addressRepository.update(addressId, addressUpdated, districtUpdated, cityUpdated, phoneUpdated);
        em.flush();

        AddressRepository.AddressDTO addressDTO = addressRepository.read(addressId);
        assertEquals(addressDTO.id(), addressId);
        assertEquals(addressDTO.address(), addressUpdated);
        assertEquals(addressDTO.district(), districtUpdated);
        assertEquals(addressDTO.city(), cityUpdated);
        assertEquals(addressDTO.phone(), phoneUpdated);
    }

    @Test
    public void testUpdateAddressWithoutClearAndFlush() {
        String address = "9966 Bag End";
        String district = "The Shire";
        String city = "Alessandria";
        String phone = "123456";
        String addressUpdated = "9999 Valinor";
        String districtUpdated = "The Undying Lands";
        String cityUpdated = "Springs";
        String phoneUpdated = "00000000";

        Integer addressId = addressRepository.create(address, district, city, phone);

        addressRepository.update(addressId, addressUpdated, districtUpdated, cityUpdated, phoneUpdated);

        AddressRepository.AddressDTO addressDTO = addressRepository.read(addressId);
        assertEquals(addressDTO.id(), addressId);
        assertEquals(addressDTO.address(), addressUpdated);
        assertEquals(addressDTO.district(), districtUpdated);
        assertEquals(addressDTO.city(), cityUpdated);
        assertEquals(addressDTO.phone(), phoneUpdated);
    }

    @Test
    public void testDeleteAddress() {
        String address = "9966 Bag End";
        String district = "The Shire";
        String city = "Alessandria";
        String phone = "123456";
        Integer addressId = addressRepository.create(address, district, city, phone);
        em.flush();

        addressRepository.delete(addressId);
        em.flush();

        AddressRepository.AddressDTO addressDTO = addressRepository.read(addressId);
        assertNull(addressDTO);
    }

}
