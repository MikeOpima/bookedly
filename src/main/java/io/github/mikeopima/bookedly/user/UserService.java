package io.github.mikeopima.bookedly.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Admin-side user management: list users and promote them. */
@Service
public class UserService {

	private final UserRepository users;

	public UserService(UserRepository users) {
		this.users = users;
	}

	@Transactional(readOnly = true)
	public List<UserResponse> listAll() {
		return users.findAll(Sort.by("id")).stream().map(UserResponse::from).toList();
	}

	/** Empty if there is no user with that id. Promoting an admin again is harmless. */
	@Transactional
	public Optional<UserResponse> promoteToAdmin(Long id) {
		return users.findById(id).map(user -> {
			user.promoteToAdmin();
			return UserResponse.from(user);
		});
	}
}
