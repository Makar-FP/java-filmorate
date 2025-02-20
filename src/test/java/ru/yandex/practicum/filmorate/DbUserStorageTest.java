package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbUserStorage.class})
class DbUserStorageTest {
	private final DbUserStorage dbUserStorage;
	public static final long TEST_USER_ID = 1;

	static User getTestUser() {
		User user = new User();
		user.setId(TEST_USER_ID);
		user.setName("testName1");
		user.setLogin("testLogin1");
		user.setEmail("test1@test.com");
		user.setBirthday(LocalDate.of(2000, 5, 14));
		return user;
	}

	@Test
	void getUserById() {
		User user = getTestUser();
		Optional<User> userOptional = Optional.ofNullable(dbUserStorage.getById(1));

		assertThat(userOptional)
				.isPresent()
				.get()
				.usingRecursiveComparison()
				.isEqualTo(user);

	}

	@Test
	void addNewUser() {
		User user = new User();
		user.setName("testName1");
		user.setLogin("testLogin1");
		user.setEmail("test1@test.com");
		user.setBirthday(LocalDate.of(2000, 5, 14));

		User userDb = dbUserStorage.create(user);
		assertNotNull(userDb, "UserDb should not be null");
		assertNotNull(userDb.getId(), "UserDb id should not be null");
	}
}
