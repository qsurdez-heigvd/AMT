-- TODO: exercise 3, sql migration to support a role for staff

CREATE TABLE IF NOT EXISTS role (
    id SERIAL PRIMARY KEY,
    role VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS staff_role (
    users_staff_id BIGINT NOT NULL,
    roles_id BIGINT NOT NULL,
    FOREIGN KEY (users_staff_id) REFERENCES staff(staff_id),
    FOREIGN KEY (roles_id) REFERENCES role(id)
);